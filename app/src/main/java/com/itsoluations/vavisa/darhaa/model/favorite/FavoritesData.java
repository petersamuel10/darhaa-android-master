package com.itsoluations.vavisa.darhaa.model.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FavoritesData {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("")
    @Expose
    private ArrayList<Products> products;


    public FavoritesData() {
    }

    public FavoritesData(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public FavoritesData(ArrayList<Products> products) {
        this.products = products;
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

    public ArrayList<Products> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Products> products) {
        this.products = products;
    }
}
