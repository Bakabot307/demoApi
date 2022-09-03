package com.shopMe.demo.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Date;

public class RequestDataDto {
    private Integer id;

    private String status;

    private double sta;

    private double money;

    private Date createdDate;

    private Date checkedDate;

    private String message;

    private Integer userId;

    private String type;

    public RequestDataDto() {
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
