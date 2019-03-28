package com.itsoluation.vavisa.darhaa.model.category_products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;

import java.util.ArrayList;

public class CategoryProducts {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("products")
    @Expose
    private ArrayList<Products> products;

    @SerializedName("productPerPage")
    @Expose
    private String productPerPage;

    @SerializedName("page")
    @Expose
    private String page;

    @SerializedName("limit")
    @Expose
    private String limit;

    @SerializedName("MinPrice")
    @Expose
    private String MinPrice;

    @SerializedName("productPerPage")
    @Expose
    private String productPerPage;
}
