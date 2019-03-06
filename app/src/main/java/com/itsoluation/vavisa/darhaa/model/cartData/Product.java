package com.itsoluation.vavisa.darhaa.model.cartData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Product {

    @SerializedName("cart_id")
    @Expose
    private String cart_id;

    @SerializedName("thumb")
    @Expose
    private String thumb;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("model")
    @Expose
    private String model;

    @SerializedName("option")
    @Expose
    private ArrayList<Options> option;

    @SerializedName("quantity")
    @Expose
    private String quantity;

    @SerializedName("stock")
    @Expose
    private Boolean stock;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("total")
    @Expose
    private String total;

    @SerializedName("minimum")
    @Expose
    private String minimum;

    @SerializedName("total_quantity")
    @Expose
    private String total_quantity;

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ArrayList<Options> getOption() {
        return option;
    }

    public void setOption(ArrayList<Options> option) {
        this.option = option;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Boolean getStock() {
        return stock;
    }

    public void setStock(Boolean stock) {
        this.stock = stock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(String total_quantity) {
        this.total_quantity = total_quantity;
    }
}
