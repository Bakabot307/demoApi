package com.shopMe.demo.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="logs")
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String status;

    private double sta;

    private double money;
    private String message;

    private Date createdDate;


    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private User user;

    public Logs() {
    }

    public Logs(String status, String message, Date createdDate, User user) {
        this.status = status;
        this.message = message;
        this.createdDate = createdDate;
        this.user = user;
    }

    public Logs(String status, double sta, String message, Date createdDate, User user) {
        this.status = status;
        this.sta = sta;
        this.message = message;
        this.createdDate = createdDate;
        this.user = user;
    }

    public Logs(double money, String message, Date createdDate, User user,String status) {
        this.status = status;
        this.money = money;
        this.message = message;
        this.createdDate = createdDate;
        this.user = user;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getSta() {
        return sta;
    }

    public void setSta(double sta) {
        this.sta = sta;
    }
}
