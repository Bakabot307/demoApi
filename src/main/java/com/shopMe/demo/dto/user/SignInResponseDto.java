package com.shopMe.demo.dto.user;

import com.shopMe.demo.model.User;

import javax.persistence.Column;

public class SignInResponseDto {
    private String status;
    private String token;




    public SignInResponseDto(String status, String token) {
        this.status = status;
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
