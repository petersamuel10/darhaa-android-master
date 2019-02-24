package com.itsoluation.vavisa.darhaa.model.address.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressGet {

    @SerializedName("address_id")
    @Expose
    private String address_id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("area")
    @Expose
    private String area;

    @SerializedName("city")
    @Expose
    private String city;

    public AddressGet() {
    }

    public AddressGet(String address_id, String title, String address, String country, String area, String city) {
        this.address_id = address_id;
        this.title = title;
        this.address = address;
        this.country = country;
        this.area = area;
        this.city = city;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
