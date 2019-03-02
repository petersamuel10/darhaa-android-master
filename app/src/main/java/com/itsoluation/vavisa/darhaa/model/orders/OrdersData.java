package com.itsoluation.vavisa.darhaa.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrdersData {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("orders")
    @Expose
    private ArrayList<Orders> orders;

    @SerializedName("total")
    @Expose
    private String total;

    @SerializedName("page")
    @Expose
    private String page;

    @SerializedName("limit")
    @Expose
    private String limit;

    public OrdersData() {
    }

    public OrdersData(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public OrdersData(ArrayList<Orders> orders, String total, String page, String limit) {
        this.orders = orders;
        this.total = total;
        this.page = page;
        this.limit = limit;
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

    public ArrayList<Orders> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Orders> orders) {
        this.orders = orders;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
