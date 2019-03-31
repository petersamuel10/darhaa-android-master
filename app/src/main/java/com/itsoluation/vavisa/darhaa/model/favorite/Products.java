package com.itsoluation.vavisa.darhaa.model.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Products {
    @SerializedName("product_id")
    @Expose
    private String product_id;

    @SerializedName("thumb")
    @Expose
    private String thumb;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("special")
    @Expose
    private String special;

    @SerializedName("minimum")
    @Expose
    private String minimum;

    @SerializedName("stock")
    @Expose
    private Boolean stock;

    @SerializedName("wishList")
    @Expose
    private Boolean wishList;



    public Products() {
    }

    public Products(String product_id, String thumb, String name, String price,
                    String special, String minimum, Boolean wishList, Boolean stock) {
        this.product_id = product_id;
        this.thumb = thumb;
        this.name = name;
        this.price = price;
        this.special = special;
        this.minimum = minimum;
        this.stock = stock;
        this.wishList = wishList;
    }

    public Products(String product_id, String thumb, String name, String price, String special) {
        this.product_id = product_id;
        this.thumb = thumb;
        this.name = name;
        this.price = price;
        this.special = special;
    }



    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public Boolean getStock() {
        return stock;
    }

    public void setStock(Boolean stock) {
        this.stock = stock;
    }

    public Boolean getWishList() {
        return wishList;
    }

    public void setWishList(Boolean wishList) {
        this.wishList = wishList;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }
}
