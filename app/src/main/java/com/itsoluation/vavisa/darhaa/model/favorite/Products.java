package com.itsoluation.vavisa.darhaa.model.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Products {
    @SerializedName("product_id")
    @Expose
    private Integer product_id;

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

    public Products() {
    }

    public Products(Integer product_id, String thumb, String name, String price, String special) {
        this.product_id = product_id;
        this.thumb = thumb;
        this.name = name;
        this.price = price;
        this.special = special;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
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
}
