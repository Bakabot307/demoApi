package com.shopMe.demo.dto.wallet;

public class WalletDto {
    private Integer id;

    private double STA;

    private double money;

    public WalletDto(Integer id, double STA, double money) {
        this.id = id;
        this.STA = STA;
        this.money = money;
    }

    public WalletDto() {
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
}
