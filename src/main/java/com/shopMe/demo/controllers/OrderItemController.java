package com.shopMe.demo.controllers;

import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.dto.Order.AddToOrderDto;
import com.shopMe.demo.dto.OrderItemDto;
import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.model.User;
import com.shopMe.demo.service.AuthenticationService;
import com.shopMe.demo.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("token") String token,@RequestBody AddToOrderDto addToOrderDto)
            throws AuthenticationFailException {
        // validate token
        authenticationService.authenticate(token);
        // retrieve user
        User user = authenticationService.getUser(token);
        // place the order
        orderItemService.placeOrder(user,addToOrderDto);
        return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
    }

    @PostMapping("/")
    public ResponseEntity<List<OrderItemDto>> getOrderByUser(@RequestParam("token") String token)
            throws AuthenticationFailException {
        // validate token
        authenticationService.authenticate(token);
        // retrieve user
        User user = authenticationService.getUser(token);
        // place the order
        List<OrderItemDto> orderItemList = orderItemService.getAllByUser(user);
        return new ResponseEntity<>(orderItemList,HttpStatus.OK);
    }


    @Scheduled(fixedDelayString = "PT1M",initialDelay = 1000L)
    void updateOrder()  {
        orderItemService.checkOrderTime();
    }

    }
