package com.shopMe.demo.service;


import com.shopMe.demo.model.Market;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;

import java.util.Date;

@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;

    public void AddToList( User user){
        Market list = new Market();
       list.setCreatedDate(new Date());
       list.setUser(user);
       list.setSta(100);
       list.setPrice(10000);
        marketRepository.save(list);
    }
}
