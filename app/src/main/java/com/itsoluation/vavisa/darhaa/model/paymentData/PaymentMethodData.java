package com.itsoluation.vavisa.darhaa.model.paymentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentMethodData {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("terms")
    @Expose
    private String terms;

    @SerializedName("sort_order")
    @Expose
    private String sort_order;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }
}
