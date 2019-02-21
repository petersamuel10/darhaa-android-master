package com.itsoluation.vavisa.darhaa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerInfo {

    @SerializedName("customer_id")
    @Expose
    private Integer customer_id;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("telephone")
    @Expose
    private String telephone;

    private String password;
    private String confirm;

    public CustomerInfo() {
    }

    public CustomerInfo(Integer customer_id, String firstname, String email, String telephone) {
        this.customer_id = customer_id;
        this.firstname = firstname;
        this.email = email;
        this.telephone = telephone;
    }

    public CustomerInfo(String firstname, String email, String telephone, String password, String confirm) {
        this.firstname = firstname;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.confirm = confirm;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
