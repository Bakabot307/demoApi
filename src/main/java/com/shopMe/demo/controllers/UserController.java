package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.dto.user.SignInDto;
import com.shopMe.demo.dto.user.SignInResponseDto;
import com.shopMe.demo.dto.user.SignupDto;
import com.shopMe.demo.exceptions.CustomException;
import com.shopMe.demo.dto.user.SignUpResponseDto;
import com.shopMe.demo.model.User;
import com.shopMe.demo.service.AuthenticationService;
import com.shopMe.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.shopMe.demo.Security.jwt.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;


@RestController
public class UserController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authManager;
    @Autowired JwtTokenUtil jwtUtil;

    @PostMapping("/signup")
    public SignUpResponseDto Signup(@RequestBody SignupDto signupDto, HttpServletRequest request) throws CustomException {
        return userService.signUp(signupDto,request);
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

}
