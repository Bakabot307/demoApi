package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.dto.market.AddToMarketDto;
import com.shopMe.demo.dto.market.MarketDto;
import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.model.Market;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import com.shopMe.demo.service.AuthenticationService;
import com.shopMe.demo.service.MarketService;
import com.shopMe.demo.service.UserService;
import com.shopMe.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @PostMapping("/add")
    public  ResponseEntity<ApiResponse> AddMarket(@RequestParam("token") String token, @RequestBody AddToMarketDto marketDto) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);

        marketService.AddToList(user,marketDto);
        return new ResponseEntity<>(new ApiResponse(true, "nice"), HttpStatus.CREATED);
    }

    @GetMapping("/")
    public  ResponseEntity<List<MarketDto>> getMarketForUser(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        List<MarketDto> marketDto = marketService.getPlacingMarket(user);
        return new ResponseEntity<>(marketDto, HttpStatus.OK);
    }


}
