package com.shopMe.demo.service;

import com.shopMe.demo.dto.Order.AddToOrderDto;
import com.shopMe.demo.dto.wallet.WalletDto;
import com.shopMe.demo.model.OrderItem;
import com.shopMe.demo.model.Product;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.Date;
import java.util.Optional;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemsRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WalletService walletService;


    public void placeOrder(User user, AddToOrderDto addToOrderDto) {
        Optional<Product>  product  = productRepository.findById(addToOrderDto.getProductId());

        WalletDto walletDto = walletService.getUserWalletDto(user);

        double sta;

        sta = walletDto.getSTA()-product.get().getPrice();

        walletDto.setSTA(sta);
        walletService.updateWallet(user,walletDto);


        orderItemRepository.save(new OrderItem(1,0,new Date(),user,product.get()));

    }
}
