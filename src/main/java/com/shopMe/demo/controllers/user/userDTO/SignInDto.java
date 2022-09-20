package com.shopMe.demo.controllers.user.userDTO;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignInDto {
    @Column(name = "email",length = 255,unique = true,nullable = false)
    @Email(message = "Please provide a valid email address")
    private String email;
    @Column(name = "password",length = 255, nullable = false)
    @NotBlank(message = "Password cannot be null")
    @Length(min = 8,message = "Password must have at least 8 characters")
    @Length(max = 255,message = "Last name must have below 255 characters")
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
