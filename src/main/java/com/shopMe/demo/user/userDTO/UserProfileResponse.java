package com.shopMe.demo.user.userDTO;


import com.shopMe.demo.user.User;
import com.shopMe.demo.log.Logs;
import com.shopMe.demo.model.Role;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserProfileResponse {
    private Integer id;


    private String firstName;


    private String lastName;


    private String email;


    private String phoneNumber;


    private String avatar;


    private Date createdDate;

    private Boolean enabled;


    private Boolean phoneEnabled;


    private Boolean emailVerified;


    private Set<Role> roles = new HashSet<>();

    private List<Logs> logs;

    public UserProfileResponse() {
    }


    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.avatar = user.getPhotosImagePath();
        this.createdDate = user.getCreatedDate();
        this.enabled = user.getEnabled();
        this.phoneEnabled = user.getPhoneEnabled();
        this.emailVerified = user.getEmailVerified();
        this.roles = user.getRoles();
        this.logs = user.getLogs();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getPhoneEnabled() {
        return phoneEnabled;
    }

    public void setPhoneEnabled(Boolean phoneEnabled) {
        this.phoneEnabled = phoneEnabled;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }


    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Logs> getLogs() {
        return logs;
    }

    public void setLogs(List<Logs> logs) {
        this.logs = logs;
    }
}
