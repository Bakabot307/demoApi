package com.shopMe.demo.dto.user;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

public class PhoneLoginDto {
    @Column(name = "phoneNumber",length = 20,unique = true,nullable = true)
    @NotBlank(message = "Please provide a valid phone number")
    private String phoneNumber;


    @Column(name = "password",length = 255, nullable = false)
    @NotBlank(message = "Password cannot be null")
    @Length(min = 8,message = "Password must have at least 8 characters")
    @Length(max = 255,message = "Last name must have below 255 characters")
    private String password;

    @Column(name = "code",length = 6, nullable = false)
    @NotBlank(message = "code cannot be null")
    private String code;

    public PhoneLoginDto() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
