package com.shopMe.demo.repository;

import com.shopMe.demo.model.Market;
import com.shopMe.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends JpaRepository<Market, Integer> {

    List<Market> getAllByStatusOrderByCreatedDateDesc(String status);

    Market findByUser(User user);

    List<Market> findAllByUser(User user);
}
