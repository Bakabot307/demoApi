package com.shopMe.demo.log;

import com.shopMe.demo.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepository extends PagingAndSortingRepository<Logs, Integer> {
    List<Logs> findAllByUserOrderByCreatedDateDesc(User user);

    List<Logs> findAllByStatus(String status);
}
