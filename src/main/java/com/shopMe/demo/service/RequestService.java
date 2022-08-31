package com.shopMe.demo.service;

import com.shopMe.demo.dto.Request.RequestDataDto;
import com.shopMe.demo.dto.Request.RequestDto;
import com.shopMe.demo.dto.Request.RequestUpdateDto;
import com.shopMe.demo.model.Request;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;
    public List<Request> getAllByUserandStatus(User user, String status){

        List<Request> requestList = requestRepository.findAllByUserAndStatus(user,status);

        return requestList;
    }


    public void addRequest(User user, RequestDto requestDto){

        Optional<Wallet> OWallet  = walletRepository.findByUser(user);
        Request  request = new Request();

        request.setMoney(requestDto.getMoney());
        request.setSta(requestDto.getSta());
        request.setStatus("pending");
        request.setMessage(requestDto.getMessage());
        request.setUser(user);
        request.setWallet(OWallet.get());
        request.setCreatedDate(new Date());

        requestRepository.save(request);
    }

    public void updateRequest(User user, RequestUpdateDto requestDto) {
        Optional<Request> ORequest  = requestRepository.findById(requestDto.getId());
        Request request = ORequest.get();

        request.setStatus(requestDto.getStatus());
        request.setMoney(requestDto.getMoney());
        request.setSta(requestDto.getSta());
        request.setCheckedDate(new Date());

        requestRepository.save(request);
    }


    public List<Request> getAllByStatus(String status) {
        return requestRepository.findAllByStatus(status);
    }
}
