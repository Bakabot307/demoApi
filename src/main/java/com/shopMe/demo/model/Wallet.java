package com.shopMe.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;

@Entity
@Table(name="wallet")
public class Wallet {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;


    private double STA;

    private double money;

    @JsonIgnore
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public Wallet() {
    }

    public Wallet( double STA, double money, User user) {
        this.STA = STA;
        this.money = money;
        this.user = user;
    }

    public Wallet(double money, User user) {
        this.money = money;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getSTA() {
        return STA;
    }

    public void setSTA(double STA) {
        this.STA = STA;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
