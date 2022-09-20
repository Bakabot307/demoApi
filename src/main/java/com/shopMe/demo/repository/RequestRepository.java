package com.shopMe.demo.repository;

import com.shopMe.demo.model.Request;
import com.shopMe.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByUserAndStatus(User user, String status);

    List<Request> findAllByStatus(String status);
}
