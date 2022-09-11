package com.shopMe.demo.service;

import com.shopMe.demo.model.Logs;
import com.shopMe.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;

import java.util.Date;
import java.util.List;

@Service
public class LogsService {

    @Autowired
    private LogsRepository logsRepository;



    private UserService userService;

    @Autowired
    public LogsService(@Lazy UserService userService) {
        this.userService = userService;
    }

    public List<Logs> getAllLogListByUser(User user) {
        List<Logs> listLog = logsRepository.findAllByUserOrderByCreatedDateDesc(user);
       return listLog;
    }

    public void addLogToUser(User user, String message, String status,String type ){
        Logs log = new Logs(status, message,new Date(),user, type);
        logsRepository.save(log);
    }
    public void addLogToUserWithSta(User user, String message, double sta, String status, String type ){
        Logs log = new Logs(status,sta, message,new Date(),user,type);
        logsRepository.save(log);
    }

    public void addLog(Logs logs ){
        logsRepository.save(logs);
    }

    public void addExchangeMoneyToStaLog(User user, String message, double sta, String status ){
        Logs log = new Logs();
        log.setSta(sta);
        log.setReceiverId(user.getId());
//        log.setUser(userService.getOwner());
        log.setMessage(message);
        log.setStatus(status);
        logsRepository.save(log);

    }



    public void StaSendingLog(User user,Integer receiverId, String message, double sta, String status, String type ){
        Logs log = new Logs(status, sta, message,new Date(),receiverId,user,type);
        logsRepository.save(log);
    }

    public void addLogToUserWithMoney(User user, String message, double money, String status, String type ){
        Logs log = new Logs(money, message,new Date(),user,status,type);
        logsRepository.save(log);
    }

    public List<Logs> getAllLogListByStatus(String status) {
       return logsRepository.findAllByStatus(status);

    }

    public List<Logs> getAllLog(String status) {
        return logsRepository.findAll();
    }
}
