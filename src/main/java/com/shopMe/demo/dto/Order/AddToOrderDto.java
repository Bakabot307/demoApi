package com.shopMe.demo.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopMe.demo.model.Product;
import com.shopMe.demo.model.User;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddToOrderDto {

    private Integer id;

    private  int quantity;

    private  double sta;

    private Date createdDate;

    private Integer productId;

    public AddToOrderDto(Integer id, int quantity, double sta, Date createdDate, Integer productId) {
        this.id = id;
        this.quantity = quantity;
        this.sta = sta;
        this.createdDate = createdDate;
        this.productId = productId;
    }

    public AddToOrderDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSta() {
        return sta;
    }

    public void setSta(double sta) {
        this.sta = sta;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
