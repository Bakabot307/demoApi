package com.shopMe.demo.user;

import com.shopMe.demo.Security.jwt.JwtTokenUtil;
import com.shopMe.demo.Settings.EmailSettingBag;
import com.shopMe.demo.Settings.SettingService;
import com.shopMe.demo.Utility;
import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.config.FileUploadUtil;
import com.shopMe.demo.config.MessageStrings;
import com.shopMe.demo.config.Twilio.TwilioSmsSender;
import com.shopMe.demo.config.Twilio.VerificationResult;

import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.exceptions.CustomException;
import com.shopMe.demo.model.Role;
import com.shopMe.demo.service.LogsService;
import com.shopMe.demo.user.userDTO.*;
import io.swagger.annotations.ApiParam;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Objects;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    LogsService logsService;

    @Autowired
    TwilioSmsSender twilioSmsSender;

    @Autowired
    SettingService settingService;

    @Value("${site.url}")
    private String SITE_URL;


    @PostMapping(value = {"/signup"})
    public ResponseEntity<ApiResponse> Signup(
            @RequestBody @Valid SignupDto signupDto,
            HttpServletRequest request
    ) throws CustomException, MessagingException, UnsupportedEncodingException {

        if (Objects.nonNull(userService.findByEmail(signupDto.getEmail()))) {
            throw new CustomException("User already exists");
        }
        // first encrypt the password
        String encryptedPassword;
        System.out.println(signupDto.getPassword());
        System.out.println(signupDto.getEmail());

        encryptedPassword = passwordEncoder.encode(signupDto.getPassword());
        System.out.println(encryptedPassword);
        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), encryptedPassword);
        user.setCreatedDate(new Date());
        user.setEnabled(false);
        user.addRole(new Role(2L));

        String randomCode = RandomString.make(64);
        user.setEmailVerifyCode(randomCode);
        user.setAvatar(null);


        User savedUser = userService.save(user);


        if (Objects.nonNull(savedUser)) {
            userService.sendEmail(user, request);
            logsService.addLogToUserActivity(savedUser,"account","success","Created new account!");
            return new ResponseEntity<>(new ApiResponse(true, "created user successfully!"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "Failed to create new user"), HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping(value = {"/phoneSignup"})
    public ResponseEntity<?> SignupWithPhone(
            @RequestBody @Valid PhoneSignupDto signupDto
    ) throws CustomException, UserNotFoundException {

        if(isPhoneNumberValid(signupDto.getPhoneNumber())){
            throw new CustomException("Phone number is not valid");
        }
        if (Objects.nonNull(userService.findByPhoneNumber(signupDto.getPhoneNumber()))) {
            throw new CustomException("User already exists");
        }

        VerificationResult result = twilioSmsSender.checkverification(signupDto.getPhoneNumber(), signupDto.getCode());
        if(result.isValid())
        {
            String encryptedPassword;
            encryptedPassword = passwordEncoder.encode(signupDto.getPassword());
            System.out.println(encryptedPassword);
            User user = new User();
            user.setFirstName(signupDto.getFirstName());
            user.setLastName(signupDto.getLastName());
            user.setPassword(encryptedPassword);
            user.setPhoneNumber(signupDto.getPhoneNumber());
            user.setCreatedDate(new Date());
            user.setEnabled(true);
            user.addRole(new Role(2L));

            String randomCode = RandomString.make(64);
            user.setEmailVerifyCode(randomCode);
            user.setAvatar(null);
            User savedUser = userService.save(user);

            if (Objects.nonNull(savedUser)) {
                logsService.addLogToUserActivity(savedUser,"account","success","Created new account!");
                String accessToken = jwtUtil.generateAccessToken(user);
                String refreshToken = jwtUtil.generateRefreshToken(user);
                SignInResponseDto response = new SignInResponseDto(accessToken, refreshToken);
                return ResponseEntity.ok().body(response);
            } else {
                return new ResponseEntity<>(new ApiResponse(false, "Failed to create new user"), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Something wrong/ Otp incorrect",HttpStatus.BAD_REQUEST);
        }




    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        String allCountryRegex ="\\+9665\\d{8}|05\\d{8}|\\+1\\(\\d{3}\\)\\d{3}-\\d{4}|\\+1\\d{10}|\\d{3}-\\d{3}-\\d{4}";
        return phoneNumber.matches(allCountryRegex);
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody @Valid SignInDto request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            SignInResponseDto response = new SignInResponseDto(accessToken, refreshToken);
            return ResponseEntity.ok().body(response);
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>(new ApiResponse(false, MessageStrings.USER_INFO_NOT_MATCH), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/phoneLogin")
    public ResponseEntity<?> LoginWithPhone(@RequestBody @Valid PhoneLoginDto request) throws AuthenticationFailException, UserNotFoundException {
            User user = userService.findByPhoneNumber(request.getPhoneNumber());
            if (!Objects.nonNull(user)) {
                return new ResponseEntity<>(new ApiResponse(false, MessageStrings.USER_PHONE_NOT_FOUND), HttpStatus.BAD_REQUEST);
            }

        if (userService.isLogin(request.getPhoneNumber(), request.getPassword())) {
                String accessToken = jwtUtil.generateAccessToken(user);
                String refreshToken = jwtUtil.generateRefreshToken(user);
                SignInResponseDto response = new SignInResponseDto(accessToken, refreshToken);
                return ResponseEntity.ok().body(response);
            } else {
                return new ResponseEntity<>(new ApiResponse(false, MessageStrings.USER_INFO_NOT_MATCH), HttpStatus.BAD_REQUEST);
            }

    }



    @GetMapping("/phoneSignup/sms")
    public ResponseEntity<String> SendCode(@RequestParam String phone){
        VerificationResult result = twilioSmsSender.SmsSender(phone);
        if(result.isValid())
        {
            return new ResponseEntity<>("Otp Sent..",HttpStatus.OK);
        }
        return new ResponseEntity<>("Otp failed to sent..",HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> Verify(@RequestParam String code) {
        boolean verified = false;
        try {
            verified = userService.Verify(code);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse(false, MessageStrings.USER_NOT_FOUND), HttpStatus.BAD_REQUEST);
        }
        if (verified) {
            return new ResponseEntity<>(new ApiResponse(true, "Verified successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "Failed to verify!"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/forget_password")
    public ResponseEntity<?> forgotPassword(HttpServletRequest request,
                                            @RequestParam String email){
        try {
            String token = userService.updateResetPasswordToken(email);
            String link = SITE_URL+ "/reset_password?token=" + token;
            System.out.println("email " + email);
            System.out.println("link " + link);
            try {
                sendEmail(link,email);
            } catch (MessagingException | UnsupportedEncodingException e) {
                return new ResponseEntity<>("Failed to send request", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Sent request successfully!"), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(MessageStrings.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("reset_password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestParam("newPassword") String newPassword) {
        User user = userService.getByResetPasswordCode(token);
        if (user !=null) {
            try {
                userService.updatePassword(user,newPassword);
            } catch (UserNotFoundException e) {
                return new ResponseEntity<>(MessageStrings.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("OK",HttpStatus.OK);
        }
        return new ResponseEntity<>("Token is invalid!", HttpStatus.BAD_REQUEST);
    }

    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSetting = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSetting);

        String toAddress = email;
        String subject = "Here's the link to reset your password";

        String content ="<p>Hello, </p>"
                +"<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link +"\">Change your password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you remember your password, "
                + "or you have not made the request.</p ";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    @GetMapping("reset_phone_password")
    public ResponseEntity<?> resetPhonePassword(@RequestParam("code") String code,
                                           @RequestParam("phoneNumber") String number,
                                           @RequestParam("newPassword") String newPassword) {
        try {
            //TO_DO: add check phone code
            User user = userService.findByPhoneNumber(number);
            userService.updatePasswordWithPhone(newPassword,user);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(MessageStrings.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }




    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
       String authorizationHeader = request.getHeader("Authorization");
       if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
           try {
               String refreshToken =authorizationHeader.substring("Bearer ".length());
               User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
               String accessToken = jwtUtil.generateAccessToken(user1);

               SignInResponseDto responseAuth = new SignInResponseDto(accessToken, refreshToken);
               return ResponseEntity.ok().body(responseAuth);
           } catch (BadCredentialsException ex) {
               return new ResponseEntity<>(new ApiResponse(false, "Something is wrong with refresh token"), HttpStatus.EXPECTATION_FAILED);
           }
       } else {
           return new ResponseEntity<>(new ApiResponse(false, "Refresh token not found"), HttpStatus.NOT_FOUND);
       }

    }

    @PutMapping("/user/edit")
    @RolesAllowed("ROLE_USER")

    public ResponseEntity<ApiResponse> editUser(
            @RequestBody @Valid UpdateUserDto updateUserDto
    ) throws UserNotFoundException {

        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User updatingUser = userService.getById(user1.getId());
        updatingUser.Update(updateUserDto);
        userService.save(updatingUser);
        return new ResponseEntity<>(new ApiResponse(true, "Updated user successfully!"), HttpStatus.OK);
    }


    @PutMapping("/user/avatar")
    @RolesAllowed("ROLE_USER")
    public ResponseEntity<ApiResponse> editAvatar(
            @RequestParam("image") MultipartFile multipartFile) throws IOException, UserNotFoundException {

        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User updatingUser = userService.getById(user1.getId());
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            updatingUser.setAvatar(fileName);
            String uploadDir = "user-photos/" + updatingUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (updatingUser.getAvatar().isEmpty()) updatingUser.setAvatar(null);
        }
        userService.updateAvatar(updatingUser);
        return new ResponseEntity<>(new ApiResponse(true, "Updated avatar successfully!"), HttpStatus.OK);
    }
}
