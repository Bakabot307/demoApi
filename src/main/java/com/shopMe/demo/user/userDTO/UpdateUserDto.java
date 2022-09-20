package com.shopMe.demo.user.userDTO;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

public class UpdateUserDto {
    @Column(name = "first_name",nullable = false,length = 255)
    @NotBlank(message = "First name cannot be null")
    @Length(min = 3,max = 255,message = "First name must have 3-255 characters")
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 255)
    @NotBlank(message = "Last name cannot be null")
    @Length(min = 3,max = 255,message = "Last name must have 3-255 characters")
    private String lastName;

    @Column(name = "password",length = 255, nullable = false)
    @NotBlank(message = "Password cannot be null")
    @Length(min = 8,message = "Password must have at least 8 characters")
    @Length(max = 255,message = "Last name must have below 255 characters")
    private String password;

    public UpdateUserDto() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
