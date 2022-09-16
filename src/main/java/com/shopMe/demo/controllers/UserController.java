package com.shopMe.demo.controllers;

import com.shopMe.demo.Security.jwt.JwtTokenUtil;
import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.config.FileUploadUtil;
import com.shopMe.demo.config.Twilio.TwilioSmsSender;
import com.shopMe.demo.config.Twilio.VerificationResult;
import com.shopMe.demo.dto.user.*;
import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.exceptions.CustomException;
import com.shopMe.demo.exceptions.UserNotFoundException;
import com.shopMe.demo.model.Role;
import com.shopMe.demo.model.User;
import com.shopMe.demo.service.LogsService;
import com.shopMe.demo.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ) throws CustomException {

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
            return new ResponseEntity<>(new ApiResponse(false, "Email or password is wrong!"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/phoneLogin")
    public ResponseEntity<?> LoginWithPhone(@RequestBody @Valid PhoneLoginDto request) throws AuthenticationFailException, CustomException {
            User user = userService.findByPhoneNumber(request.getPhoneNumber());
            if (!Objects.nonNull(user)) {
                throw new AuthenticationFailException("user not present");
            }


            VerificationResult result = twilioSmsSender.checkverification(request.getPhoneNumber(), request.getCode());
             if(!result.isValid()){
            return new ResponseEntity<>("Something wrong/ Otp incorrect",HttpStatus.BAD_REQUEST);
              }

            if (userService.isLogin(request.getPhoneNumber(), request.getPassword()) && result.isValid()) {
                String accessToken = jwtUtil.generateAccessToken(user);
                String refreshToken = jwtUtil.generateRefreshToken(user);
                SignInResponseDto response = new SignInResponseDto(accessToken, refreshToken);
                return ResponseEntity.ok().body(response);
            } else {
                return new ResponseEntity<>("Email or password is wrong",HttpStatus.BAD_REQUEST);
            }

    }



    @GetMapping("/phoneSignup/sms")
    public ResponseEntity<String> SendCode(String phone){
        VerificationResult result = twilioSmsSender.SmsSender(phone);
        if(result.isValid())
        {
            return new ResponseEntity<>("Otp Sent..",HttpStatus.OK);
        }
        return new ResponseEntity<>("Otp failed to sent..",HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> Verify(@RequestParam @Valid String code) {
        boolean verified = userService.Verify(code);
        if (verified) {
            return new ResponseEntity<>(new ApiResponse(true, "Verified successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "Failed to verify!"), HttpStatus.BAD_REQUEST);
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
