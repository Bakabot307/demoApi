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
        Logs log = new Logs(status,message,new Date(),user);
        logsRepository.save(log);
    }
}
