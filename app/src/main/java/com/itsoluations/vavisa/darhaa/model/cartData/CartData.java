package com.itsoluations.vavisa.darhaa.model.cartData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartData {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("weight")
    @Expose
    private String weight;

    @SerializedName("products")
    @Expose
    private ArrayList<Product> products;

    @SerializedName("totals")
    @Expose
    private ArrayList<Total> totals;

    @SerializedName("min_checkout_price")
    @Expose
    private String min_checkout_price;


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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Total> getTotals() {
        return totals;
    }

    public void setTotals(ArrayList<Total> totals) {
        this.totals = totals;
    }

    public String getMin_checkout_price() {
        return min_checkout_price; }

    public void setMin_checkout_price(String min_checkout_price) {
        this.min_checkout_price = min_checkout_price; }
}
