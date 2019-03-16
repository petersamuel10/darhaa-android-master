package com.itsoluation.vavisa.darhaa.model.paymentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ShippingMethodDataStatus {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("")
    @Expose
    private ArrayList<ShippingMethodData> shippingMethods;

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

    public ArrayList<ShippingMethodData> getShippingMethods() {
        return shippingMethods;
    }

    public void setShippingMethods(ArrayList<ShippingMethodData> shippingMethods) {
        this.shippingMethods = shippingMethods;
    }
}
