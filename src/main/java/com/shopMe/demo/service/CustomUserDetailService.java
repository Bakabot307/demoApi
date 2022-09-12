//package com.shopMe.demo.service;
//
//import com.shopMe.demo.model.Role;
//import com.shopMe.demo.model.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import com.shopMe.demo.repository.*;
//@Service
//public class CustomUserDetailService implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(username);
//        if (user != null) {
//            return user;
//        } else {
//            throw new UsernameNotFoundException("Not found: " + username);
//        }
//    }
//    }
