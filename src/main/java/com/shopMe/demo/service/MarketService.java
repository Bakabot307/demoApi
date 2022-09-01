package com.shopMe.demo.service;


import com.shopMe.demo.dto.market.AddToMarketDto;
import com.shopMe.demo.model.Logs;
import com.shopMe.demo.model.Market;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private LogsService logsService;

    public void AddToList( User user, AddToMarketDto marketDto){
        Market list = new Market();
        Wallet wallet = walletService.getUserWallet(user);

        List<Market> marketList = marketRepository.findAllByUser(user);
        boolean updated = false;

       for(Market marketL : marketList){
           if(marketL.getPrice()==marketDto.getPrice()&&marketL.getStatus().equalsIgnoreCase("placing")
                   &&marketL.getType().equalsIgnoreCase(marketDto.getType())){
               marketL.setSta(marketL.getSta()+ marketDto.getSta());
               marketL.setStaAvailable(marketL.getStaAvailable()+ marketDto.getSta());
               marketRepository.save(marketL);
               if(checkMarketPrice(marketDto)){
                   updateMarket(user,marketL,marketDto.getStatus());
               }
               updated = true;
           }
       }
       if(!updated){
           list.setCreatedDate(new Date());
           list.setUser(user);
           list.setSta(marketDto.getSta());
           list.setPrice(marketDto.getPrice());
           list.setStatus(marketDto.getStatus());
           list.setType(marketDto.getType());
           list.setStaAvailable(marketDto.getSta());
           Market market = marketRepository.save(list);
           marketRepository.flush();

           if(market.getType().equalsIgnoreCase("sell")){
               wallet.setSTA(wallet.getSTA()- marketDto.getSta());
               walletService.save(wallet);
           }

           Logs log = new Logs();
           log.setStatus(marketDto.getStatus());
           log.setSta(marketDto.getSta());
           log.setMessage(marketDto.getType());
           log.setUser(user);
           log.setCreatedDate(list.getCreatedDate());
           log.setMoney(marketDto.getPrice());
           logsService.addLog(user,log);


           if(checkMarketPrice(marketDto)){
               updateMarket(user,market,marketDto.getStatus());
           }
       }
    }
    public void updateMarket( User user, Market market,String status){
        Wallet wallet = walletService.getUserWallet(user);

        List<Market> list = marketRepository.getAllByStatusOrderByCreatedDateDesc(status);

        for (Market marketL : list) {
            Wallet walletL = walletService.getUserWallet(marketL.getUser());

            if (market.getSta() == 0) {
                break;

            }
            if(marketL.getPrice()== market.getPrice() && marketL.getSta() != 0
                    && marketL.getSta()!=0
                    && marketL.getType().equalsIgnoreCase("sell")
                    && market.getType().equalsIgnoreCase("buy")
            ){

                if( marketL.getStaAvailable() >= market.getStaAvailable()){

                    double amount = market.getSta();
                    market.setStaAvailable(0);
                    market.setStatus("completed");
                    marketL.setStaAvailable(marketL.getStaAvailable()-amount);

                    walletL.setMoney(walletL.getMoney()+ amount * marketL.getPrice());
                    wallet.setMoney(wallet.getMoney()- amount * marketL.getPrice());
                    wallet.setSTA(amount);

                    if(marketL.getStaAvailable()==0){
                        marketL.setStatus("completed");
                    }

                    walletService.save(walletL);
                    walletService.save(wallet);
                    marketRepository.save(market);
                    marketRepository.save(marketL);

                    Logs log = new Logs();
                    Logs log2 = new Logs();

                    log.setStatus(market.getStatus());
                    log.setSta(market.getSta());
                    log.setMessage(market.getType());
                    log.setUser(user);
                    log.setCreatedDate(market.getCreatedDate());
                    log.setMoney(market.getPrice());

                    log2.setStatus(marketL.getStatus());
                    log2.setSta(marketL.getSta());
                    log2.setMessage(marketL.getType());
                    log2.setUser(marketL.getUser());
                    log2.setCreatedDate(marketL.getCreatedDate());
                    log2.setMoney(marketL.getPrice());

                    logsService.addLog(user,log);
                    logsService.addLog(marketL.getUser(),log2);

                } else if (marketL.getStaAvailable()< market.getStaAvailable()){

                    double amount = marketL.getStaAvailable();
                    market.setStaAvailable(market.getStaAvailable()- marketL.getStaAvailable());
                    marketL.setStaAvailable(0);
                    marketL.setStatus("completed");


                    walletL.setSTA(amount);
                    walletL.setMoney(walletL.getMoney()+ amount* marketL.getPrice());
                    wallet.setMoney(wallet.getMoney()- amount* marketL.getPrice());

                    walletService.save(walletL);
                    walletService.save(wallet);
                    marketRepository.save(market);
                    marketRepository.save(marketL);

                    Logs log = new Logs();
                    Logs log2 = new Logs();

                    log.setStatus(market.getStatus());
                    log.setSta(market.getSta());
                    log.setMessage(market.getType());
                    log.setUser(user);
                    log.setCreatedDate(market.getCreatedDate());
                    log.setMoney(market.getPrice());

                    log2.setStatus(marketL.getStatus());
                    log2.setSta(marketL.getSta());
                    log2.setMessage(marketL.getType());
                    log2.setUser(marketL.getUser());
                    log2.setCreatedDate(marketL.getCreatedDate());
                    log2.setMoney(marketL.getPrice());

                    logsService.addLog(user,log);
                    logsService.addLog(marketL.getUser(),log2);
                }

            }else
            if(marketL.getPrice()== market.getPrice() && marketL.getSta() != 0
                    && marketL.getSta()!=0
                    && marketL.getType().equalsIgnoreCase("buy")
                    && market.getType().equalsIgnoreCase("sell")
            ){
                if( marketL.getStaAvailable() >= market.getStaAvailable()){

                    double amount = market.getSta();
                    market.setStaAvailable(0);
                    market.setStatus("completed");
                    marketL.setStaAvailable(marketL.getStaAvailable()-amount);

                    walletL.setMoney(walletL.getMoney()+ amount * marketL.getPrice());
                    wallet.setMoney(wallet.getMoney()- amount * marketL.getPrice());
                    wallet.setSTA(amount);

                    if(marketL.getStaAvailable()==0){
                        marketL.setStatus("completed");
                    }

                    walletService.save(walletL);
                    walletService.save(wallet);
                    marketRepository.save(market);
                    marketRepository.save(marketL);

                    Logs log = new Logs();
                    Logs log2 = new Logs();

                    log.setStatus(market.getStatus());
                    log.setSta(market.getSta());
                    log.setMessage(market.getType());
                    log.setUser(user);
                    log.setCreatedDate(market.getCreatedDate());
                    log.setMoney(market.getPrice());

                    log2.setStatus(marketL.getStatus());
                    log2.setSta(marketL.getSta());
                    log2.setMessage(marketL.getType());
                    log2.setUser(marketL.getUser());
                    log2.setCreatedDate(marketL.getCreatedDate());
                    log2.setMoney(marketL.getPrice());

                    logsService.addLog(user,log);
                    logsService.addLog(marketL.getUser(),log2);

                } else if (marketL.getStaAvailable()< market.getStaAvailable()){

                    double amount = marketL.getStaAvailable();
                    market.setStaAvailable(market.getStaAvailable()- marketL.getStaAvailable());
                    marketL.setStaAvailable(0);
                    marketL.setStatus("completed");


                    walletL.setSTA(amount);
                    walletL.setMoney(walletL.getMoney()+ amount* marketL.getPrice());
                    wallet.setMoney(wallet.getMoney()- amount* marketL.getPrice());

                    walletService.save(walletL);
                    walletService.save(wallet);
                    marketRepository.save(market);
                    marketRepository.save(marketL);

                    Logs log = new Logs();
                    Logs log2 = new Logs();

                    log.setStatus(market.getStatus());
                    log.setSta(market.getSta());
                    log.setMessage(market.getType());
                    log.setUser(user);
                    log.setCreatedDate(market.getCreatedDate());
                    log.setMoney(market.getPrice());

                    log2.setStatus(marketL.getStatus());
                    log2.setSta(marketL.getSta());
                    log2.setMessage(marketL.getType());
                    log2.setUser(marketL.getUser());
                    log2.setCreatedDate(marketL.getCreatedDate());
                    log2.setMoney(marketL.getPrice());

                    logsService.addLog(user,log);
                    logsService.addLog(marketL.getUser(),log2);
                }
            }

        }

    }
    public boolean checkMarketPrice(AddToMarketDto marketDto){

        List<Market> list = marketRepository.getAllByStatusOrderByCreatedDateDesc(marketDto.getStatus());

       for (Market market : list){
           if(marketDto.getSta()==0){
               return false;

           }

           if((marketDto.getPrice()== marketDto.getPrice())){
               return true;
           }
       }
    return  false;
    }
    public void marketAction( User user, AddToMarketDto marketDto){
        Market list = new Market();
        Wallet wallet = walletService.getUserWallet(user);
        list.setCreatedDate(new Date());
        list.setUser(user);
        list.setSta(marketDto.getSta());
        list.setPrice(marketDto.getPrice());

        if(marketDto.getSta()==0){
            list.setStatus("completed");
        } else {
            list.setStatus("selling");
        }

        list.setType("sell");

        wallet.setSTA(wallet.getSTA()- marketDto.getSta());
        walletService.save(wallet);

        marketRepository.save(list);

        Logs log = new Logs();

        log.setStatus("success");
        log.setSta(marketDto.getSta());
        log.setMessage("sell sta");
        log.setUser(user);
        log.setCreatedDate(list.getCreatedDate());
        log.setMoney(marketDto.getPrice());
        logsService.addLog(user,log);
    }

}
