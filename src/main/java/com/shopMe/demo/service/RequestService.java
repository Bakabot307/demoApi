package com.shopMe.demo.service;

import com.shopMe.demo.dto.Request.RequestDataDto;
import com.shopMe.demo.dto.Request.RequestDto;
import com.shopMe.demo.dto.Request.RequestUpdateDto;
import com.shopMe.demo.model.Logs;
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
    private LogsService logsService;

    @Autowired
    private WalletRepository walletRepository;
    public List<Request> getAllByUserandStatus(User user, String status){

        List<Request> requestList = requestRepository.findAllByUserAndStatus(user,status);

        return requestList;
    }


    public void addRequest(User user, RequestDto requestDto){

        Optional<Wallet> OWallet  = walletRepository.findByUser(user);
        Request  request = new Request();
        if(requestDto.getType().equalsIgnoreCase("withdraw")){
            OWallet.get().setMoney(OWallet.get().getMoney()-requestDto.getMoney());
            OWallet.get().setPendingMoney(OWallet.get().getPendingMoney()+ requestDto.getMoney());
            walletRepository.save(OWallet.get());
        }

        request.setMoney(requestDto.getMoney());
        request.setStatus(requestDto.getStatus());
        request.setMessage(requestDto.getMessage());
        request.setUser(user);
        request.setWallet(OWallet.get());
        request.setCreatedDate(new Date());
        request.setType(requestDto.getType());

        Logs log = new Logs();
        log.setMoney(requestDto.getMoney());
        log.setCreatedDate(request.getCreatedDate());
        log.setMessage(requestDto.getMessage());
        log.setUser(user);
        log.setStatus(requestDto.getStatus());
        log.setType(requestDto.getType());
        logsService.addLog(log);
        requestRepository.save(request);
    }

    public void updateRequest(User user, RequestUpdateDto requestDto) {
        Optional<Request> ORequest  = requestRepository.findById(requestDto.getId());
        Request request = ORequest.get();

        Optional<Wallet> wallet = walletRepository.findByUser(user);
        if(requestDto.getStatus().equalsIgnoreCase("rejected")){
            if(request.getType().equalsIgnoreCase("deposit")){
               request.setStatus(requestDto.getStatus());
               request.setCheckedDate(new Date());
                request.setStatus(requestDto.getStatus());

                Logs log = new Logs();
                log.setMoney(request.getMoney());
                log.setCreatedDate(request.getCheckedDate());
                log.setMessage(request.getMessage());
                log.setUser(user);
                log.setStatus(requestDto.getStatus());
                log.setType(request.getType());

                logsService.addLog(log);
                requestRepository.save(request);
            } else if (request.getType().equalsIgnoreCase("withdraw")) {

                System.out.println("withdraw");
                System.out.println(request.getMoney());
                System.out.println(wallet.get().getPendingMoney());
                wallet.get().setPendingMoney(wallet.get().getPendingMoney()-request.getMoney());
                wallet.get().setMoney(wallet.get().getMoney()+ request.getMoney());

                request.setCheckedDate(new Date());
                request.setStatus(requestDto.getStatus());

                walletRepository.save(wallet.get());
                requestRepository.save(request);
                Logs log = new Logs();
                log.setMoney(request.getMoney());
                log.setCreatedDate(request.getCheckedDate());
                log.setMessage(request.getMessage());
                log.setUser(user);
                log.setStatus(requestDto.getStatus());
                log.setType(request.getType());

                logsService.addLog(log);
            }

        } else if(requestDto.getStatus().equalsIgnoreCase("accepted")){
            if(request.getType().equalsIgnoreCase("deposit")){
               wallet.get().setMoney(wallet.get().getMoney()+request.getMoney());
                request.setCheckedDate(new Date());
                request.setStatus(requestDto.getStatus());
                requestRepository.save(request);
                Logs log = new Logs();
                log.setMoney(request.getMoney());
                log.setCreatedDate(request.getCheckedDate());
                log.setMessage(request.getMessage());
                log.setUser(user);
                log.setStatus(requestDto.getStatus());
                log.setType(request.getType());

                logsService.addLog(log);
            } else if (request.getType().equalsIgnoreCase("withdraw")) {
                wallet.get().setPendingMoney(wallet.get().getPendingMoney()-request.getMoney());
                request.setCheckedDate(new Date());
                request.setStatus(requestDto.getStatus());
                requestRepository.save(request);
                Logs log = new Logs();
                log.setMoney(request.getMoney());
                log.setCreatedDate(request.getCheckedDate());
                log.setMessage(request.getMessage());
                log.setUser(user);
                log.setStatus(requestDto.getStatus());
                log.setType(request.getType());

                logsService.addLog(log);
            }
        }

    }







    public List<Request> getAllByStatus(String status) {
        return requestRepository.findAllByStatus(status);
    }
}
