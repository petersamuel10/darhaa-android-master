package com.itsoluation.vavisa.darhaa.model.paymentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.itsoluation.vavisa.darhaa.model.favorite.Products;

import java.util.ArrayList;

public class PaymentMethodDataStatus {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("")
    @Expose
    private ArrayList<PaymentMethodData> paymentMethods;

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

    public ArrayList<PaymentMethodData> paymentMethods() {
        return paymentMethods;
    }

    public void paymentMethods(ArrayList<PaymentMethodData> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }
}
