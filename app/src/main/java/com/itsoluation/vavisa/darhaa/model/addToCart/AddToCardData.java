package com.itsoluation.vavisa.darhaa.model.addToCart;

import java.util.ArrayList;

public class AddToCardData {

    public ArrayList<Options> options;
    public String user_id;
    public String quantity;
    public String product_id;
    public String device_id;


    public AddToCardData() {
    }

    public AddToCardData(String user_id, String quantity, String product_id, String device_id) {
        this.user_id = user_id;
        this.quantity = quantity;
        this.product_id = product_id;
        this.device_id = device_id;
    }

    public AddToCardData(ArrayList<Options> options, String user_id, String quantity, String product_id, String device_id) {
        this.options = options;
        this.user_id = user_id;
        this.quantity = quantity;
        this.product_id = product_id;
        this.device_id = device_id;
    }

    public ArrayList<Options> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Options> options) {
        this.options = options;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
