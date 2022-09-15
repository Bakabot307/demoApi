package com.shopMe.demo.repository;

import com.shopMe.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAll();

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);


    @Query("SELECT u FROM User u WHERE u.emailVerifyCode = ?1")
    public User findByEmailVerifyCode(String code);

    @Query("UPDATE User u SET u.enabled = true, u.emailVerifyCode = null WHERE u.id = ?1")
    @Modifying
    public void enable(Integer id);
}
