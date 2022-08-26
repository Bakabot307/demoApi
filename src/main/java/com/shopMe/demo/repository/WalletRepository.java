package com.shopMe.demo.repository;

import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Optional<Wallet> findByUser(User user);
}
