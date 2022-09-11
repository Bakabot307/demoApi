package com.shopMe.demo.controllers;


import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shopMe.demo.repository.*;
import org.springframework.web.servlet.function.EntityResponse;

import javax.annotation.security.RolesAllowed;
import javax.swing.text.html.parser.Entity;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TEST {


    @Autowired
    private MarketRepository marketRepository;
    @GetMapping("/")
    @RolesAllowed("user")
    public ResponseEntity<ApiResponse> TEST(){

        return new ResponseEntity<>(new ApiResponse(true, "failed"), HttpStatus.OK);



    }
}
