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
