package com.shopMe.demo.repository;

import com.shopMe.demo.model.OrderItem;
import com.shopMe.demo.model.Product;
import com.shopMe.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem,Integer> {
    List<OrderItem> findByUser(User user);

    OrderItem findByProduct(Product product);

    List<OrderItem> findAllByStatus(String pending);
}
