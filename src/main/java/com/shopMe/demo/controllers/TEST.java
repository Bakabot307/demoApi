package com.shopMe.demo.controllers;


import com.shopMe.demo.Security.jwt.JwtTokenUtil;
import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.dto.user.SignInDto;
import com.shopMe.demo.dto.user.SignInResponseDto;
import com.shopMe.demo.model.Market;
import com.shopMe.demo.model.User;
import com.shopMe.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.shopMe.demo.repository.*;
import org.springframework.web.servlet.function.EntityResponse;

import javax.annotation.security.RolesAllowed;
import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TEST {


    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/")
    public ResponseEntity<?> login(@RequestBody @Valid SignInDto request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            SignInResponseDto response = new SignInResponseDto(user.getEmail(), accessToken);
            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
