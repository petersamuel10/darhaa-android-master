package com.itsoluation.vavisa.darhaa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("customerInfo")
    @Expose
    private CustomerInfo customerInfo;

    @SerializedName("userAccessToken")
    @Expose
    private String userAccessToken;


    public User() {
    }

    public User(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public User(String status, String message, CustomerInfo customerInfo, String userAccessToken) {
        this.status = status;
        this.message = message;
        this.customerInfo = customerInfo;
        this.userAccessToken = userAccessToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public String getUserAccessToken() {
        return userAccessToken;
    }

    public void setUserAccessToken(String userAccessToken) {
        this.userAccessToken = userAccessToken;
    }
}
