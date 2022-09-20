package com.shopMe.demo.repository;

import com.shopMe.demo.model.OrderItem;
import com.shopMe.demo.model.Product;
import com.shopMe.demo.controllers.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem,Integer> {
    List<OrderItem> findByUser(User user);

    OrderItem findByProduct(Product product);

    List<OrderItem> findAllByStatus(String pending);
}
