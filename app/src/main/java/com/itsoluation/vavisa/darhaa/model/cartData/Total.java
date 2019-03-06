package com.itsoluation.vavisa.darhaa.model.cartData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Total {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("text")
    @Expose
    private String text;

    public Total() {
    }

    public Total(String title, String total) {
        this.title = title;
        text = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotal() {
        return text;
    }

    public void setTotal(String total) {
        text = total;
    }
}
