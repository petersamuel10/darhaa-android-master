package com.itsoluation.vavisa.darhaa.model.cartData;

import java.util.ArrayList;

public class EditCart {

    public ArrayList<Quantity> quantity;
    public String user_id;
    public String device_id;

    public EditCart() {
    }

    public EditCart(ArrayList<Quantity> quantity, String user_id, String device_id) {
        this.quantity = quantity;
        this.user_id = user_id;
        this.device_id = device_id;
    }

    public ArrayList<Quantity> getQuantity() {
        return quantity;
    }

    public void setQuantity(ArrayList<Quantity> quantity) {
        this.quantity = quantity;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
