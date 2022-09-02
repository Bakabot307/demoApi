package com.shopMe.demo.dto.Request;

public class WithdrawDto {
    private String message;
    private double money;

    public WithdrawDto() {}


    public WithdrawDto(String message, double money) {
        this.message = message;
        this.money = money;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
