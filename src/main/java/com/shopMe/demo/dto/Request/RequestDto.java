package com.shopMe.demo.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

public class RequestDto {

    private Integer id;
    private String status;

    private double sta;

    private double money;

    private String message;

    public RequestDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSta() {
        return sta;
    }

    public void setSta(double sta) {
        this.sta = sta;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}