package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import com.shopMe.demo.service.MarketService;
import com.shopMe.demo.service.UserService;
import com.shopMe.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @Autowired
    private WalletService walletService;

    @Autowired
    UserService userService;

    @PostMapping("/add")
    public  ResponseEntity<ApiResponse> AddMarket(){

        Optional<User> user = userService.getById(1);
        marketService.AddToList(user.get());
        return new ResponseEntity<>(new ApiResponse(true, "nice"), HttpStatus.CREATED);
    }
}
