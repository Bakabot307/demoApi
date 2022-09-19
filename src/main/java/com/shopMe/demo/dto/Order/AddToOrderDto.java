package com.shopMe.demo.dto.Order;

public class AddToOrderDto {

    private Integer id;


    private Integer productId;


    public AddToOrderDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


}
