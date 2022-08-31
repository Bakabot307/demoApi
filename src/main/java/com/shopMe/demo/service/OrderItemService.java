package com.shopMe.demo.service;

import com.shopMe.demo.dto.Order.AddToOrderDto;
import com.shopMe.demo.dto.wallet.WalletDto;
import com.shopMe.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.function.EntityResponse;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
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


        orderItemRepository.save(new OrderItem(1,0,new Date(),"Investing",user,product.get()));

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
            System.out.println("created date" +createdDate);
            System.out.println("today date" +today);

            double profit,percentPerMonth;
            int investMonth;

            investMonth = orderItem.getProduct().getInvestYear()*12;
            double month = (double)daysBetween/30;


            percentPerMonth = orderItem.getProduct().getPercentage()/investMonth;

            profit = orderItem.getProduct().getPrice()*percentPerMonth*month;
            System.out.println(orderItem.getProduct().getPrice());
            System.out.println(percentPerMonth);
            System.out.println(month);

            if(month>=investMonth){
                orderItem.setStatus("completed");
                orderItem.setStaProfit(profit);
                orderItemRepository.save(orderItem);
            } else {
                orderItem.setStaProfit(profit);
                System.out.println(profit);
                orderItemRepository.save(orderItem);
                System.out.println("not done");
            }


        }




    }
}
