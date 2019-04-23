package com.itsoluations.vavisa.darhaa.model.paymentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.itsoluations.vavisa.darhaa.model.cartData.Product;
import com.itsoluations.vavisa.darhaa.model.cartData.Total;

import java.util.ArrayList;

public class CheckoutProductPage {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("products")
    @Expose
    private ArrayList<Product> products;

    @SerializedName("totals")
    @Expose
    private ArrayList<Total> totals;

    @SerializedName("payment")
    @Expose
    private String payment;

    @SerializedName("order_id")
    @Expose
    private String order_id;

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

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
