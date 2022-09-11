package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.config.FileUploadUtil;
import com.shopMe.demo.dto.user.SignInDto;
import com.shopMe.demo.dto.user.SignInResponseDto;
import com.shopMe.demo.dto.user.SignupDto;
import com.shopMe.demo.exceptions.CustomException;
import com.shopMe.demo.dto.user.SignUpResponseDto;
import com.shopMe.demo.exceptions.UserNotFoundException;
import com.shopMe.demo.model.Role;
import com.shopMe.demo.model.User;
import com.shopMe.demo.service.AuthenticationService;
import com.shopMe.demo.service.UserService;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.utility.RandomString;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;


@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authManager;
    @Autowired JwtTokenUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping(value={"/signup"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public SignUpResponseDto Signup(SignupDto signupDto,
                                    @RequestPart(value = "image",required = false) MultipartFile multipartFile,
                                    HttpServletRequest request
    ) throws CustomException, IOException, UserNotFoundException {

        if (Objects.nonNull(userService.findByEmail(signupDto.getEmail()))) {
            throw new CustomException("User already exists");
        }
        // first encrypt the password
        String encryptedPassword;
        System.out.println(signupDto.getPassword());
        System.out.println(signupDto.getFirstName());
        encryptedPassword = passwordEncoder.encode(signupDto.getPassword());

        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), encryptedPassword);
        user.setCreatedDate(new Date());
        user.setEnabled(false);
        user.addRole(new Role(2L));

        String randomCode = RandomString.make(64);
        user.setEmailVerifyCode(randomCode);

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setAvatar(fileName);
            String uploadDir = "user-photos/" + user.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getAvatar().isEmpty()) user.setAvatar(null);
        }
        return userService.signUp(user,request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody SignInDto signInDto) {
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

//    @GetMapping("/user/edit/{id}")
//    public ResponseEntity<ApiResponse> editUser(@PathVariable(name = "id") Integer id,
//                           @RequestBody UpdateUserDto updateUserDto,
//                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
//
//            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//            user.setPhotos(fileName);
//            User savedUser = service.save(user);
//
//            String uploadDir = "user-photos/" + savedUser.getId();
//
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
//        return new ResponseEntity<>(new ApiResponse(true, "Updated successfully!"), HttpStatus.OK);
//    }

    @PostMapping(value={"/upload"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String upload(SignupDto  signupDto,
                             @RequestPart(value = "image",required = false) MultipartFile multipartFile,
                                    HttpServletRequest request
    ) throws CustomException, IOException, UserNotFoundException {

        System.out.println(signupDto.getFirstName());
        System.out.println(signupDto.getEmail());
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        String uploadDir = "test/" + fileName;
        FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        return "test";
    }

}
