package com.shopMe.demo.dto.market;


public class AddToMarketDto {

    private Integer id;
    private double sta;

    private double price;

    private String status;

    private String type;

    public AddToMarketDto() {
    }


    public double getSta() {
        return sta;
    }

    public void setSta(double sta) {
        this.sta = sta;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
