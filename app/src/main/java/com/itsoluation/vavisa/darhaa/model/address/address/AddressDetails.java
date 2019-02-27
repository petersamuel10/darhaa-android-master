package com.itsoluation.vavisa.darhaa.model.address.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressDetails {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("address_1")
    @Expose
    private String address_1;

    @SerializedName("postcode")
    @Expose
    private String postcode;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("country_id")
    @Expose
    private String country_id;

    @SerializedName("zone_id")
    @Expose
    private String zone_id;

    @SerializedName("zone")
    @Expose
    private String zone;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("country_code")
    @Expose
    private String country_code;

    @SerializedName("default")
    @Expose
    private Boolean default_;

    public AddressDetails() {
    }

    public AddressDetails(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public AddressDetails(String firstname, String title, String address_1, String postcode, String city, String country_id, String zone_id, String zone, String country, String country_code, Boolean default_) {
        this.firstname = firstname;
        this.title = title;
        this.address_1 = address_1;
        this.postcode = postcode;
        this.city = city;
        this.country_id = country_id;
        this.zone_id = zone_id;
        this.zone = zone;
        this.country = country;
        this.country_code = country_code;
        this.default_ = default_;
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public Boolean getDefault_() {
        return default_;
    }

    public void setDefault_(Boolean default_) {
        this.default_ = default_;
    }
}
