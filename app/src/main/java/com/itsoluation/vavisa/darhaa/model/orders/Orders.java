package com.itsoluation.vavisa.darhaa.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Orders {

    @SerializedName("order_id")
    @Expose
    private String order_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("date_added")
    @Expose
    private String date_added;


    public Orders() {
    }

    public Orders(String order_id, String name, String status, String date_added) {
        this.order_id = order_id;
        this.name = name;
        this.status = status;
        this.date_added = date_added;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }
}
