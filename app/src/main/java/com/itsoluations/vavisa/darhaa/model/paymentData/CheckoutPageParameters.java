package com.itsoluations.vavisa.darhaa.model.paymentData;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.itsoluations.vavisa.darhaa.model.address.address.AddressAdd;

public class CheckoutPageParameters implements Parcelable {

    @Nullable
    @SerializedName("address_id")
    @Expose
    private String address_id;

    @Nullable
    @SerializedName("address")
    @Expose
    private AddressAdd address;

    @Nullable
    @SerializedName("shipping_address")
    @Expose
    private AddressAdd address2;

    @Nullable
    @SerializedName("shipping_address_id")
    @Expose
    private String shipping_address_id;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("device_id")
    @Expose
    private String device_id;

    @SerializedName("shipping_method_code")
    @Expose
    private String shipping_method_code;

    @SerializedName("shipping_method_title")
    @Expose
    private String shipping_method_title;

    @SerializedName("shipping_method_cost")
    @Expose
    private String shipping_method_cost;

    @SerializedName("payment_method_code")
    @Expose
    private String payment_method_code;

    @SerializedName("payment_method_title")
    @Expose
    private String payment_method_title;

    @SerializedName("coupon_code")
    @Expose
    private String coupon_code;

    @SerializedName("comment")
    @Expose
    private String comment;

    public CheckoutPageParameters() {
    }

    public CheckoutPageParameters(String address_id, String shipping_address_id, String user_id, String device_id, String shipping_method_code, String shipping_method_title,
                                  String shipping_method_cost2, String payment_method_code, String payment_method_title, String coupon_code) {
        this.address_id = address_id;
        this.shipping_address_id = shipping_address_id;
        this.user_id = user_id;
        this.device_id = device_id;
        this.shipping_method_code = shipping_method_code;
        this.shipping_method_title = shipping_method_title;
        this.shipping_method_cost = shipping_method_cost2;
        this.payment_method_code = payment_method_code;
        this.payment_method_title = payment_method_title;
        this.coupon_code = coupon_code;
    }

    public CheckoutPageParameters(String address_id, String shipping_address_id, String user_id, String device_id, String shipping_method_code, String shipping_method_title,
                                  String shipping_method_cost2, String payment_method_code, String payment_method_title) {
        this.address_id = address_id;
        this.shipping_address_id = shipping_address_id;
        this.user_id = user_id;
        this.device_id = device_id;
        this.shipping_method_code = shipping_method_code;
        this.shipping_method_title = shipping_method_title;
        this.shipping_method_cost = shipping_method_cost2;
        this.payment_method_code = payment_method_code;
        this.payment_method_title = payment_method_title;
    }

    public CheckoutPageParameters(AddressAdd address,AddressAdd address2, String user_id, String device_id, String shipping_method_code, String shipping_method_title,
                                  String shipping_method_cost, String payment_method_code, String payment_method_title, String coupon_code) {
        this.address = address;
        this.address2 = address2;
        this.user_id = user_id;
        this.device_id = device_id;
        this.shipping_method_code = shipping_method_code;
        this.shipping_method_title = shipping_method_title;
        this.shipping_method_cost = shipping_method_cost;
        this.payment_method_code = payment_method_code;
        this.payment_method_title = payment_method_title;
        this.coupon_code = coupon_code;
    }

    public CheckoutPageParameters(AddressAdd address, String shipping_address_id, String user_id, String device_id, String shipping_method_code, String shipping_method_title,
                                  String shipping_method_cost, String payment_method_code, String payment_method_title) {
        this.address = address;
        this.shipping_address_id = shipping_address_id;
        this.user_id = user_id;
        this.device_id = device_id;
        this.shipping_method_code = shipping_method_code;
        this.shipping_method_title = shipping_method_title;
        this.shipping_method_cost = shipping_method_cost;
        this.payment_method_code = payment_method_code;
        this.payment_method_title = payment_method_title; }


    protected CheckoutPageParameters(Parcel in) {
        address_id = in.readString();
        address = in.readParcelable(AddressAdd.class.getClassLoader());
        address2 = in.readParcelable(AddressAdd.class.getClassLoader());
        shipping_address_id = in.readString();
        user_id = in.readString();
        device_id = in.readString();
        shipping_method_code = in.readString();
        shipping_method_title = in.readString();
        shipping_method_cost = in.readString();
        payment_method_code = in.readString();
        payment_method_title = in.readString();
        coupon_code = in.readString();
        comment = in.readString();
    }

    public static final Creator<CheckoutPageParameters> CREATOR = new Creator<CheckoutPageParameters>() {
        @Override
        public CheckoutPageParameters createFromParcel(Parcel in) {
            return new CheckoutPageParameters(in);
        }

        @Override
        public CheckoutPageParameters[] newArray(int size) {
            return new CheckoutPageParameters[size];
        }
    };

    public AddressAdd getAddress2() {
        return address2;
    }

    public void setAddress2(AddressAdd address2) {
        this.address2 = address2;
    }

    public AddressAdd getAddress() {
        return address;
    }

    public void setAddress(AddressAdd address) {
        this.address = address;
    }

    public String getShipping_method_cost() {
        return shipping_method_cost;
    }

    public void setShipping_method_cost(String shipping_method_cost) {
        this.shipping_method_cost = shipping_method_cost; }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getShipping_address_id() {
        return shipping_address_id;
    }

    public void setShipping_address_id(String shipping_address_id) {
        this.shipping_address_id = shipping_address_id; }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getShipping_method_code() {
        return shipping_method_code;
    }

    public void setShipping_method_code(String shipping_method_code) {
        this.shipping_method_code = shipping_method_code; }

    public String getShipping_method_title() {
        return shipping_method_title;
    }

    public void setShipping_method_title(String shipping_method_title) {
        this.shipping_method_title = shipping_method_title; }

    public String getShipping_method_cost2() {
        return shipping_method_cost;
    }

    public void setShipping_method_cost2(String shipping_method_cost2) {
        this.shipping_method_cost = shipping_method_cost2; }

    public String getPayment_method_code() {
        return payment_method_code;
    }

    public void setPayment_method_code(String payment_method_code) {
        this.payment_method_code = payment_method_code; }

    public String getPayment_method_title() {
        return payment_method_title;
    }

    public void setPayment_method_title(String payment_method_title) {
        this.payment_method_title = payment_method_title; }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getComment() {
        return comment; }

    public void setComment(String comment) {
        this.comment = comment; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(address_id);
        dest.writeParcelable(address, flags);
        dest.writeParcelable(address2, flags);
        dest.writeString(shipping_address_id);
        dest.writeString(user_id);
        dest.writeString(device_id);
        dest.writeString(shipping_method_code);
        dest.writeString(shipping_method_title);
        dest.writeString(shipping_method_cost);
        dest.writeString(payment_method_code);
        dest.writeString(payment_method_title);
        dest.writeString(coupon_code);
        dest.writeString(comment);
    }

}
