package com.shopMe.demo.dto.market;


import com.shopMe.demo.model.Market;

import java.util.Date;

public class MarketDto {

    private Integer id;

    private double sta;

    private double staAvailable;

    private double price;

    private Date createdDate;

    private String status;

    private String type;

    private Integer userId;

    public MarketDto(Market market) {
        this.id = market.getId();
        this.sta = market.getSta();
        this.staAvailable = market.getStaAvailable();
        this.price = market.getPrice();
        this.createdDate = market.getCreatedDate();
        this.status = market.getStatus();
        this.type = market.getType();
        this.userId = market.getUser().getId();
    }

    public MarketDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getSta() {
        return sta;
    }

    public void setSta(double sta) {
        this.sta = sta;
    }

    public double getStaAvailable() {
        return staAvailable;
    }

    public void setStaAvailable(double staAvailable) {
        this.staAvailable = staAvailable;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
