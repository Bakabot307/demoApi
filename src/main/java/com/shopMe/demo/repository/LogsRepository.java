package com.shopMe.demo.repository;

import com.shopMe.demo.model.Logs;
import com.shopMe.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepository extends PagingAndSortingRepository<Logs, Integer> {
    List<Logs> findAllByUserOrderByCreatedDateDesc(User user);

    List<Logs> findAllByStatus(String status);
}
