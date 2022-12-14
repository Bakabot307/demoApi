package com.shopMe.demo.controllers;


import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.dto.Request.RequestDataDto;
import com.shopMe.demo.dto.Request.RequestDto;
import com.shopMe.demo.dto.Request.RequestUpdateDto;

import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.log.LogsService;
import com.shopMe.demo.model.Request;
import com.shopMe.demo.user.User;
import com.shopMe.demo.service.*;
import com.shopMe.demo.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private WalletService walletService;
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogsService logsService;

    @GetMapping("/")
    public ResponseEntity<List<Request>> getAllByUserAndStatus(@RequestParam("token") String token, @RequestParam String status) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        List<Request> list = requestService.getAllByUserandStatus(user,status);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDataDto>> getAllByStatus(@RequestParam String status) throws AuthenticationFailException {
        List<Request> list = requestService.getAllByStatus(status);

        List<RequestDataDto> requestDtos =new ArrayList<>();

        for (Request request : list){
            RequestDataDto requestDto = new RequestDataDto();

            requestDto.setId(request.getId());
            requestDto.setStatus(request.getStatus());
            requestDto.setSta(request.getSta());
            requestDto.setCreatedDate(request.getCreatedDate());
            requestDto.setMessage(request.getMessage());
            requestDto.setMoney(request.getMoney());
            requestDto.setCheckedDate(request.getCheckedDate());
            requestDto.setUserId(request.getUser().getId());
            requestDto.setType(request.getType());


            requestDtos.add(requestDto);

        }


        return new ResponseEntity<>(requestDtos, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse> addRequest(@RequestParam("token") String token, @RequestBody RequestDto requestDto) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        requestService.addRequest(user,requestDto);
        return new ResponseEntity<>(new ApiResponse(true, "Added to request"), HttpStatus.CREATED);
    }

//    @PutMapping("/")
//    public ResponseEntity<ApiResponse> updateRequest(@RequestParam("token") String token, @RequestBody RequestUpdateDto requestDto) throws AuthenticationFailException {
//        authenticationService.authenticate(token);
//        User user = authenticationService.getUser(token);
//        Optional<User> user2 = userService.getById(requestDto.getUserId());
//
//        if(Role.admin!=user.getRole()){
//            return new ResponseEntity<>(new ApiResponse(true, "need to be admin"), HttpStatus.FORBIDDEN);
//        } else {
//            requestService.updateRequest(user2.get(),requestDto);
//            return new ResponseEntity<>(new ApiResponse(true, "updated"), HttpStatus.OK);
//        }
//    }

    @PutMapping("/payout")
    public ResponseEntity<ApiResponse> payoutMoney(@RequestParam("token") String token, @RequestBody RequestUpdateDto requestDto) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        if(requestDto.getStatus().equalsIgnoreCase("accept")){
            walletService.payout(requestDto.getMoney(),user);
            requestService.updateRequest(user,requestDto);
            return new ResponseEntity<>(new ApiResponse(true, "accepted request"), HttpStatus.OK);
        } else if (requestDto.getStatus().equalsIgnoreCase("decline")){
            requestService.updateRequest(user,requestDto);
            return new ResponseEntity<>(new ApiResponse(true, "declined request"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(true, "failed"), HttpStatus.BAD_REQUEST);
    }
}
