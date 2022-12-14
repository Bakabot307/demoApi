package com.shopMe.demo.repository;

import com.shopMe.demo.model.Market;
import com.shopMe.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends JpaRepository<Market, Integer> {

    List<Market> getAllByStatusOrderByCreatedDateDesc(String status);

    Market findByUser(User user);

    List<Market> findAllByUser(User user);

    List<Market> getByUser(User user);

    List<Market> getAllByUserAndStatusOrderByCreatedDateDesc(User user, String status);

    List<Market> getAllByStatusAndTypeOrderByPriceDesc(String status, String Type);

    @Query("SELECT m FROM Market m WHERE not exists ( SELECT u from User u where m.user.id= :id)")
    List<Market> getAllButNotUser(@Param("id") Integer id);


}
