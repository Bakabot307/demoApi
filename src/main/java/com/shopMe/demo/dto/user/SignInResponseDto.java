package com.shopMe.demo.dto.user;

import com.shopMe.demo.model.User;

import javax.persistence.Column;

public class SignInResponseDto {
    private String status;
    private String token;

    private UserDataDto userDataDto;


    public SignInResponseDto(String status, String token, UserDataDto userDataDto) {
        this.status = status;
        this.token = token;
        this.userDataDto = userDataDto;
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

    public UserDataDto getUserDataDto() {
        return userDataDto;
    }

    public void setUserDataDto(UserDataDto userDataDto) {
        this.userDataDto = userDataDto;
    }
}
