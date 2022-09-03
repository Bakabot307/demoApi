package com.shopMe.demo.dto;

import com.shopMe.demo.model.OrderItem;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class OrderItemDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quantity")
    private @NotNull int quantity;

    @Column(name = "staProfit")
    private @NotNull double staProfit;

    @Column(name = "price")
    private @NotNull double price;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "claim_date")
    private Date claimDate;

    private String status;

    private Integer userId;

    private Integer productId;

    public OrderItemDto() {
    }

    public OrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getProduct().getPrice();
        this.staProfit = orderItem.getStaProfit();
        this.createdDate = orderItem.getCreatedDate();
        this.claimDate = orderItem.getClaimDate();
        this.status = orderItem.getStatus();
        this.userId = orderItem.getUser().getId();
        this.productId = orderItem.getProduct().getId();
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

    public double getStaProfit() {
        return staProfit;
    }

    public void setStaProfit(double staProfit) {
        this.staProfit = staProfit;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
