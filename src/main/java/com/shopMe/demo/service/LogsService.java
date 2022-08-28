package com.shopMe.demo.service;

import com.shopMe.demo.model.Cart;
import com.shopMe.demo.model.Logs;
import com.shopMe.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;

import java.util.Date;
import java.util.List;

@Service
public class LogsService {

    @Autowired
    private LogsRepository logsRepository;
    public List<Logs> getAllLogList(User user) {
        List<Logs> listLog = logsRepository.findAllByUserOrderByCreatedDateDesc(user);
       return listLog;
    }
    public void addLogToUser(User user, String message, String status ){
        Logs log = new Logs(status, message,new Date(),user);
        logsRepository.save(log);
    }
    public void addLogToUserWithSta(User user, String message, double sta, String status ){
        Logs log = new Logs(status,sta, message,new Date(),user);
        logsRepository.save(log);
    }

    public void StaSendingLog(User user,Integer receiverId, String message, double sta, String status ){
        Logs log = new Logs(status, sta, message,new Date(),receiverId,user);
        logsRepository.save(log);
    }

    public void addLogToUserWithMoney(User user, String message, double money, String status ){
        Logs log = new Logs(money, message,new Date(),user,status);
        logsRepository.save(log);
    }
}
