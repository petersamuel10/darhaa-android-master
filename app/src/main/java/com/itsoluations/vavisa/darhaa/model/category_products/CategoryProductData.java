package com.itsoluations.vavisa.darhaa.model.category_products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.itsoluations.vavisa.darhaa.model.favorite.Products;

import java.util.ArrayList;

public class CategoryProductData {

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
    private Integer productPerPage;

    @SerializedName("page")
    @Expose
    private String page;

    @SerializedName("limit")
    @Expose
    private Integer limit;

    @SerializedName("MinPrice")
    @Expose
    private String MinPrice;

    @SerializedName("MaxPrice")
    @Expose
    private String MaxPrice;

    @SerializedName("totalProducts")
    @Expose
    private Integer totalProducts;

    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;

    public CategoryProductData() {
    }

    public CategoryProductData(String status, String message, ArrayList<Products> products,
                               Integer productPerPage, String page, Integer limit, String minPrice,
                               String maxPrice, Integer totalProducts, Integer totalPages) {
        this.status = status;
        this.message = message;
        this.products = products;
        this.productPerPage = productPerPage;
        this.page = page;
        this.limit = limit;
        MinPrice = minPrice;
        MaxPrice = maxPrice;
        this.totalProducts = totalProducts;
        this.totalPages = totalPages;
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

    public Integer getProductPerPage() {
        return productPerPage;
    }

    public void setProductPerPage(Integer productPerPage) {
        this.productPerPage = productPerPage;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getMinPrice() {
        return MinPrice;
    }

    public void setMinPrice(String minPrice) {
        MinPrice = minPrice;
    }

    public String getMaxPrice() {
        return MaxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        MaxPrice = maxPrice;
    }

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
