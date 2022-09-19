package com.shopMe.demo.service;


import com.shopMe.demo.dto.market.AddToMarketDto;
import com.shopMe.demo.dto.market.MarketDto;
import com.shopMe.demo.model.Logs;
import com.shopMe.demo.model.Market;
import com.shopMe.demo.user.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;

import java.util.*;

@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private LogsService logsService;

    public void AddToList(User user, AddToMarketDto marketDto) {
        Market list = new Market();
        Wallet wallet = walletService.getUserWallet(user);

        List<Market> marketList = marketRepository.findAllByUser(user);

        boolean updated = false;

        for (Market marketL : marketList) {
            if (marketL.getPrice() == marketDto.getPrice() && marketL.getStatus().equalsIgnoreCase(marketDto.getStatus())
                    && marketL.getType().equalsIgnoreCase(marketDto.getType())) {
                marketL.setSta(marketL.getSta() + marketDto.getSta());
                marketL.setStaAvailable(marketL.getStaAvailable() + marketDto.getSta());
                marketRepository.save(marketL);
                if (checkMarketPrice(marketDto)) {
                    updateMarket(user, marketL, marketDto.getStatus());
                }
                updated = true;
            }
        }
        if (!updated) {
            list.setCreatedDate(new Date());
            list.setUser(user);
            list.setSta(marketDto.getSta());
            list.setPrice(marketDto.getPrice());
            list.setStatus(marketDto.getStatus());
            list.setType(marketDto.getType());
            list.setStaAvailable(marketDto.getSta());
            Market market = marketRepository.save(list);
            marketRepository.flush();

            if (market.getType().equalsIgnoreCase("sell")) {
                wallet.setSTA(wallet.getSTA() - marketDto.getSta());
                walletService.save(wallet);
            }
            if(market.getType().equalsIgnoreCase("buy")){
                wallet.setMoney(wallet.getMoney()-marketDto.getSta()* marketDto.getPrice());
                walletService.save(wallet);
            }

            Logs log = new Logs();
            log.setStatus(marketDto.getStatus());
            log.setSta(marketDto.getSta());
            log.setMessage(marketDto.getType());
            log.setUser(user);
            log.setCreatedDate(list.getCreatedDate());
            log.setMoney(marketDto.getPrice());
            log.setType(marketDto.getType());
            logsService.addLog(log);


            if (checkMarketPrice(marketDto)) {
                updateMarket(user, market, marketDto.getStatus());
            }
        }
    }

    public void updateMarket(User user, Market market, String status) {
        Wallet wallet = walletService.getUserWallet(user);

        List<Market> list = marketRepository.getAllByStatusOrderByCreatedDateDesc(status);

        List<Market> userList = marketRepository.getByUser(user);

        list.removeAll(userList);

        for (Market marketL : list) {
            Wallet walletL = walletService.getUserWallet(marketL.getUser());

            if (market.getSta() == 0) {
                break;

            }
            if (marketL.getPrice() == market.getPrice() && marketL.getSta() != 0
                    && marketL.getSta() != 0
                    && marketL.getType().equalsIgnoreCase("sell")
                    && market.getType().equalsIgnoreCase("buy")
            ) {

                if (marketL.getStaAvailable() >= market.getStaAvailable()) {

                    double amount = market.getSta();
                    market.setStaAvailable(0);
                    market.setStatus("completed");
                    marketL.setStaAvailable(marketL.getStaAvailable() - amount);

                    walletL.setMoney(walletL.getMoney() + amount * marketL.getPrice());
                    wallet.setMoney(wallet.getMoney() - amount * marketL.getPrice());
                    wallet.setSTA(amount);

                    if (marketL.getStaAvailable() == 0) {
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
                    log.setType(market.getType());

                    log2.setStatus(marketL.getStatus());
                    log2.setSta(marketL.getSta());
                    log2.setMessage(marketL.getType());
                    log2.setUser(marketL.getUser());
                    log2.setCreatedDate(marketL.getCreatedDate());
                    log2.setMoney(marketL.getPrice());
                    log2.setType(marketL.getType());


                    logsService.addLog(log);
                    logsService.addLog(log2);

                } else if (marketL.getStaAvailable() < market.getStaAvailable()) {

                    double amount = marketL.getStaAvailable();
                    market.setStaAvailable(market.getStaAvailable() - marketL.getStaAvailable());
                    marketL.setStaAvailable(0);
                    marketL.setStatus("completed");


                    walletL.setSTA(amount);
                    walletL.setMoney(walletL.getMoney() + amount * marketL.getPrice());
                    wallet.setMoney(wallet.getMoney() - amount * marketL.getPrice());

                    walletService.save(walletL);
                    walletService.save(wallet);
                    marketRepository.save(market);
                    marketRepository.save(marketL);

                    Logs log = new Logs(market);
                    Logs log2 = new Logs(marketL);


                    logsService.addLog(log);
                    logsService.addLog(log2);
                }

            } else if (marketL.getPrice() == market.getPrice() && marketL.getSta() != 0
                    && marketL.getSta() != 0
                    && marketL.getType().equalsIgnoreCase("buy")
                    && market.getType().equalsIgnoreCase("sell")
            ) {
                if (marketL.getStaAvailable() >= market.getStaAvailable()) {
                    System.out.println("sell");
                    double amount = market.getSta();
                    market.setStaAvailable(0);
                    market.setStatus("completed");
                    marketL.setStaAvailable(marketL.getStaAvailable() - amount);


                    wallet.setMoney(wallet.getMoney() + amount * marketL.getPrice());
                    wallet.setSTA(wallet.getSTA()-amount);
                    walletL.setSTA(walletL.getSTA()+amount);

                    if (marketL.getStaAvailable() == 0) {
                        marketL.setStatus("completed");
                    }

                    walletService.save(walletL);
                    walletService.save(wallet);
                    marketRepository.save(market);
                    marketRepository.save(marketL);

                    Logs log = new Logs(market);
                    Logs log2 = new Logs(marketL);


                    logsService.addLog(log);
                    logsService.addLog(log2);

                } else if (marketL.getStaAvailable() < market.getStaAvailable()) {

                    double amount = marketL.getStaAvailable();

                    market.setStaAvailable(market.getStaAvailable() - amount);
                    marketL.setStaAvailable(0);
                    marketL.setStatus("completed");


                    walletL.setSTA(walletL.getSTA()+ amount);
                    walletL.setMoney(walletL.getMoney() + amount * marketL.getPrice());
                    wallet.setMoney(wallet.getMoney() - amount * marketL.getPrice());
                    wallet.setSTA(wallet.getSTA()+amount);

                    walletService.save(walletL);
                    walletService.save(wallet);
                    marketRepository.save(market);
                    marketRepository.save(marketL);

                    Logs log = new Logs(market);
                    Logs log2 = new Logs(marketL);

                    logsService.addLog(log);
                    logsService.addLog(log2);
                }
            }

        }

    }

    public boolean checkMarketPrice(AddToMarketDto marketDto) {

        List<Market> list = marketRepository.getAllByStatusOrderByCreatedDateDesc(marketDto.getStatus());

        for (Market market : list) {
            if (marketDto.getSta() == 0) {
                return false;

            }

            if ((marketDto.getPrice() == marketDto.getPrice())) {
                return true;
            }
        }
        return false;
    }


    public List<MarketDto> getPlacingMarketByStatus(String status) {
        List<Market> list = marketRepository.getAllByStatusOrderByCreatedDateDesc(status);
        List<MarketDto> marketDtos = new ArrayList<>();
        for (Market market : list) {
            MarketDto marketDto = new MarketDto(market);
            marketDtos.add(marketDto);
        }


        return marketDtos;
    }

    public List<MarketDto> getPlacingMarketByType(User user,String type) {
        List<Market> list = marketRepository.getAllByStatusAndTypeOrderByPriceDesc("placing",type);
        List<Market> userList = marketRepository.getByUser(user);
        list.removeAll(userList);
        List<MarketDto> marketDtos = new ArrayList<>();
        for (Market market : list) {
            MarketDto marketDto = new MarketDto(market);
            marketDtos.add(marketDto);
        }


        return marketDtos;
    }



    public void cancelMarket(User user, Integer id){
        Optional<Market> market = marketRepository.findById(id);
        Wallet wallet = walletService.getUserWallet(market.get().getUser());

        if(market.get().getType().equalsIgnoreCase("sell")){
            wallet.setSTA(wallet.getSTA()+market.get().getSta());
            market.get().setStatus("cancelled");
            marketRepository.save(market.get());
        } else if (market.get().getType().equalsIgnoreCase("buy")){
            market.get().setStatus("cancelled");
            wallet.setMoney(wallet.getMoney()+market.get().getSta()*market.get().getPrice());
            marketRepository.save(market.get());

        }
    }

    public List<MarketDto> getUserMarketByStatus(User user, String status) {
        List<Market> userList = marketRepository.getAllByUserAndStatusOrderByCreatedDateDesc(user,status);

        List<MarketDto> marketDtos = new ArrayList<>();
        for (Market market : userList) {
            MarketDto marketDto = new MarketDto(market);
            marketDtos.add(marketDto);
        }
        return marketDtos;
    }
}
