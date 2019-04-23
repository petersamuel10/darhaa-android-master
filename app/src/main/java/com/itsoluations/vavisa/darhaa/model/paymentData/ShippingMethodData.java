package com.itsoluations.vavisa.darhaa.model.paymentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShippingMethodData {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("cost")
    @Expose
    private String cost;

    @SerializedName("tax_class_id")
    @Expose
    private String tax_class_id;

    @SerializedName("sort_order")
    @Expose
    private String sort_order;

    @SerializedName("error")
    @Expose
    private String error;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTax_class_id() {
        return tax_class_id;
    }

    public void setTax_class_id(String tax_class_id) {
        this.tax_class_id = tax_class_id;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
