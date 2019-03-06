package com.itsoluation.vavisa.darhaa.model.address.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressAdd {

    @SerializedName("user_id")
    @Expose
    private Integer user_id;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("address_1")
    @Expose
    private String address_1;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("country_id")
    @Expose
    private String country_id;

    @SerializedName("postcode")
    @Expose
    private String postcode;

    @SerializedName("zone_id")
    @Expose
    private String zone_id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("default")
    @Expose
    private String default_;

    public AddressAdd() {
    }

    public AddressAdd(Integer user_id, String firstname, String address_1, String city, String country_id, String postcode, String zone_id, String title, String default_) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.address_1 = address_1;
        this.city = city;
        this.country_id = country_id;
        this.postcode = postcode;
        this.zone_id = zone_id;
        this.title = title;
        this.default_ = default_;
    }

    public AddressAdd(Integer user_id, String firstname, String address_1, String city, String country_id, String postcode, String zone_id, String title) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.address_1 = address_1;
        this.city = city;
        this.country_id = country_id;
        this.postcode = postcode;
        this.zone_id = zone_id;
        this.title = title;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefault_() {
        return default_;
    }

    public void setDefault_(String default_) {
        this.default_ = default_;
    }
}
