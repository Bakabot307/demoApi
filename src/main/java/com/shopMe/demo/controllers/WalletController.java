package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.dto.cart.AddToCartDto;
import com.shopMe.demo.dto.cart.CartDto;
import com.shopMe.demo.dto.product.ProductDto;
import com.shopMe.demo.dto.wallet.WalletDto;
import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.exceptions.ProductNotExistException;
import com.shopMe.demo.model.Category;
import com.shopMe.demo.model.Product;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import com.shopMe.demo.service.AuthenticationService;
import com.shopMe.demo.service.LogsService;
import com.shopMe.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private LogsService logsService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestParam("token") String token)
            throws ProductNotExistException, AuthenticationFailException {
        // first authenticate the token
        authenticationService.authenticate(token);

        // get the user
        User user = authenticationService.getUser(token);


        walletService.createWallet(user);

        // return response
        logsService.addLogToUser(user,"created new wallet","success");
        return new ResponseEntity<>(new ApiResponse(true, "Created wallet"), HttpStatus.CREATED);


    }

    @PostMapping("/")
    public ResponseEntity<WalletDto> getUserWallet(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);

        // get the user
        User user = authenticationService.getUser(token);

        // get items in the cart for the user.
        WalletDto walletDto = walletService.getUserWalletDto(user);

        return new ResponseEntity<WalletDto>(walletDto,HttpStatus.OK);
    }

    @PutMapping("/deposit")
    public ResponseEntity<ApiResponse> depositWallet(@RequestParam("token") String token,@RequestParam("money") double money) throws AuthenticationFailException {
        authenticationService.authenticate(token);

        // get the user
        User user = authenticationService.getUser(token);
        // get items in the cart for the user.
        walletService.depositWallet(money,user);
        String message = "deposited " + money;
        logsService.addLogToUserWithMoney(user,message,money,"success");
        return new ResponseEntity<>(new ApiResponse(true, message), HttpStatus.OK);
    }

    @PutMapping("/exchange")
    public ResponseEntity<ApiResponse> exchangeMoney(@RequestParam("token") String token,
                                                     @RequestParam("sta") double sta
                                                     ) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        // get the user
        User user = authenticationService.getUser(token);

        // get items in the cart for the user.
        walletService.exchangeMoney(sta,user);
        String message = "Exchanged money to " + sta;
        logsService.addLogToUserWithSta(user,message,sta,"success");
        return new ResponseEntity<>(new ApiResponse(true, message), HttpStatus.OK);
    }

    @PutMapping("/send")
    public ResponseEntity<ApiResponse> sendSta(@RequestParam("token") String token,
                                                     @RequestParam("sta") double sta,
                                               @RequestParam("receiver") String receiver
    ) throws AuthenticationFailException {
        authenticationService.authenticate(token);

        // get the user
        User user = authenticationService.getUser(token);

        // get items in the cart for the user.
        walletService.sendSta(sta,user,receiver);
        String message = "sent "+ sta +  " to " + receiver;
        logsService.addLogToUserWithSta(user,message,sta,"success");
        return new ResponseEntity<>(new ApiResponse(true, message), HttpStatus.OK);
    }

}
