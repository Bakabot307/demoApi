package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.config.FileUploadUtil;
import com.shopMe.demo.dto.user.*;
import com.shopMe.demo.exceptions.CustomException;
import com.shopMe.demo.exceptions.UserNotFoundException;
import com.shopMe.demo.model.Role;
import com.shopMe.demo.model.User;
import com.shopMe.demo.service.AuthenticationService;
import com.shopMe.demo.service.UserService;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.shopMe.demo.Security.jwt.JwtTokenUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.EntityResponse;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.Objects;


@RestController
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authManager;
    @Autowired JwtTokenUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping(value={"/signup"})
    public ResponseEntity<ApiResponse> Signup(
            @RequestBody SignupDto signupDto,
                                 HttpServletRequest request
    ) throws CustomException, UserNotFoundException, MessagingException, UnsupportedEncodingException {

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


        if(Objects.nonNull(savedUser)){
            userService.sendEmail(user,request);
            return new ResponseEntity<>(new ApiResponse(true, "created user successfully!"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "Failed to create new user"), HttpStatus.BAD_REQUEST);

        }




    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody SignInDto signInDto) {
        System.out.println(signInDto.getPassword());
        System.out.println(signInDto.getEmail());
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInDto.getEmail(), signInDto.getPassword())

            );

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            SignInResponseDto response = new SignInResponseDto(user.getEmail(), accessToken);

            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            System.out.println("bad");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> Verify(@RequestParam String code){
        boolean verified = userService.Verify(code);
        if(verified){
            return new ResponseEntity<>(new ApiResponse(true, "Verified successfully!"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(false, "Failed to verify!"), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/user/edit/{id}")
    @RolesAllowed("ROLE_USER")
    public ResponseEntity<ApiResponse> editUser(
            @PathVariable(name = "id") Integer id,
                            @RequestBody UpdateUserDto updateUserDto
                           ) throws  UserNotFoundException {

            User updatingUser = userService.getById(id);
            updatingUser.Update(updateUserDto);

        userService.save(updatingUser);
        return new ResponseEntity<>(new ApiResponse(true, "Updated user successfully!"), HttpStatus.OK);
    }


    @PutMapping("/user/avatar/{id}")
    @RolesAllowed("ROLE_USER")
    public ResponseEntity<ApiResponse> editAvatar(
            @PathVariable(name = "id") Integer id,
            @RequestParam("image") MultipartFile multipartFile) throws IOException, UserNotFoundException {

        User updatingUser = userService.getById(id);
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
