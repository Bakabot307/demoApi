package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.dto.market.AddToMarketDto;
import com.shopMe.demo.dto.market.MarketDto;
import com.shopMe.demo.dto.market.UpdateMarketDto;
import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.user.User;
import com.shopMe.demo.service.AuthenticationService;
import com.shopMe.demo.service.MarketService;
import com.shopMe.demo.user.UserService;
import com.shopMe.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return new ResponseEntity<>(new ApiResponse(true, "added"), HttpStatus.CREATED);
    }

    @GetMapping("/")
    public  ResponseEntity<List<MarketDto>> getMarketForUserByType(@RequestParam("token") String token,@RequestParam String type) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        List<MarketDto> marketDto = marketService.getPlacingMarketByType(user,type);
        return new ResponseEntity<>(marketDto, HttpStatus.OK);
    }
    @GetMapping("/all")
    public  ResponseEntity<List<MarketDto>> getAllByStatus(@RequestParam("status") String status) throws AuthenticationFailException {
        List<MarketDto> marketDto = marketService.getPlacingMarketByStatus(status);
        return new ResponseEntity<>(marketDto, HttpStatus.OK);
    }




    @GetMapping("/user")
    public  ResponseEntity<List<MarketDto>> getUserMarketByStatus(@RequestParam("token") String token,String status) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        List<MarketDto> marketDto = marketService.getUserMarketByStatus(user,status);
        return new ResponseEntity<>(marketDto, HttpStatus.OK);
    }

    @PutMapping("/user")
    public  ResponseEntity<ApiResponse> updateMarketForUser(@RequestParam("token") String token,@RequestBody UpdateMarketDto marketDto) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        if (marketDto.getStatus().equalsIgnoreCase("cancelled")){
            marketService.cancelMarket(user,marketDto.getId());
            return new ResponseEntity<>(new ApiResponse(true,"cancelled"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(true,"no data"), HttpStatus.BAD_REQUEST);
    }



}
