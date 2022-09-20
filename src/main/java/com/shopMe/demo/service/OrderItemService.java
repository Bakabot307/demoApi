package com.shopMe.demo.service;

import com.shopMe.demo.config.DateUtils;
import com.shopMe.demo.dto.Order.AddToOrderDto;
import com.shopMe.demo.dto.OrderItemDto;
import com.shopMe.demo.dto.wallet.WalletDto;
import com.shopMe.demo.log.Logs;
import com.shopMe.demo.log.LogsService;
import com.shopMe.demo.model.*;
import com.shopMe.demo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemsRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LogsService logsService;

    private WalletService walletService;

    @Autowired
    public OrderItemService(@Lazy WalletService walletService) {
        this.walletService = walletService;
    }


    public void placeOrder(User user, AddToOrderDto addToOrderDto) {
        Optional<Product>  product  = productRepository.findById(addToOrderDto.getProductId());

        WalletDto walletDto = walletService.getUserWalletDto(user);
        double sta;
        sta = walletDto.getSTA()-product.get().getPrice();
        walletDto.setSTA(sta);
        walletService.updateWallet(user,walletDto);
        OrderItem orderItem = new OrderItem();
        LocalDate claimDate;
        Date createdDate = new Date();

        claimDate = DateUtils.asLocalDate(createdDate).plusMonths(product.get().getInvestMonth());

        orderItem.setQuantity(1);
        orderItem.setCreatedDate(new Date());
        orderItem.setClaimDate(DateUtils.asDate(claimDate));
        orderItem.setProduct(product.get());
        orderItem.setStaProfit(0);
        orderItem.setStatus("Investing");
        orderItem.setUser(user);

        orderItemRepository.save(orderItem);

        Logs log = new Logs();
        log.setCreatedDate(orderItem.getCreatedDate());
        log.setSta(orderItem.getPrice());
        log.setStatus(orderItem.getStatus());
        log.setUser(user);
        log.setType("Investing");
        log.setMessage("Investing");
logsService.addLog(log);
    }

    public void checkOrderTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        List<OrderItem> orderItemList = orderItemRepository.findAllByStatus("Investing");

        for (OrderItem orderItem:orderItemList){
            LocalDate createdDate = orderItem.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate claimDate;
            LocalDate today = (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
            claimDate = orderItem.getClaimDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long daysLeft = ChronoUnit.DAYS.between(today, claimDate);
            long daysBetween = ChronoUnit.DAYS.between(createdDate, today);

            double profit,percentPerMonth,profitNow;
            int investMonth;

            investMonth = orderItem.getProduct().getInvestMonth();
            double month = (double)daysBetween/30;


            percentPerMonth = orderItem.getProduct().getPercentage()/investMonth;

            profit = (orderItem.getProduct().getPrice()*percentPerMonth/100*investMonth);
            profitNow = orderItem.getProduct().getPrice()+(orderItem.getProduct().getPrice()*percentPerMonth/100*month);


            if(month>=investMonth){
                orderItem.setStatus("completed");
                orderItem.setStaProfit(profit);
                Wallet wallet = walletService.getUserWallet(orderItem.getUser());
                wallet.setSTA(wallet.getSTA()+profit);
                Logs log = new Logs();

                log.setStatus("success");
                log.setSta(profit);
                log.setMessage("Claimed profit");
                log.setUser(orderItem.getUser());
                log.setCreatedDate(new Date());
                log.setMoney(0);
                log.setType("claim");
                logsService.addLog(log);
                walletService.save(wallet);
                orderItemRepository.save(orderItem);
            } else {
                orderItem.setStaProfit(profitNow);
                orderItemRepository.save(orderItem);

            }


        }




    }

    public List<OrderItemDto> getAllByUser(User user) {
       List<OrderItem> orderList =  orderItemRepository.findByUser(user);
       List<OrderItemDto> orderItemDtos = new ArrayList<>();

        for (OrderItem orderItem :orderList){
            OrderItemDto orderItemDto = new OrderItemDto(orderItem);
           orderItemDtos.add(orderItemDto);
        }
       return orderItemDtos;

    }
}
