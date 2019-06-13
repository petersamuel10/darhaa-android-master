package com.itsoluations.vavisa.darhaa.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Status {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @Nullable
    @SerializedName("totalCartItems")
    @Expose
    private String totalCartItems;

    public Status() {
    }

    public Status(String status, String message) {
        this.status = status;
        this.message = message;
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

    @Nullable
    public String getTotalCartItems() {
        return totalCartItems;
    }

    public void setTotalCartItems(@Nullable String totalCartItems) {
        this.totalCartItems = totalCartItems;
    }
}
