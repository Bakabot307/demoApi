package com.shopMe.demo.dto.market;

public class UpdateMarketDto {
    private Integer id;
    private String status;

    public UpdateMarketDto() {
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
}
