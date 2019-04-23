package com.itsoluations.vavisa.darhaa.model.address.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Countries {

    @SerializedName("country_id")
    @Expose
    private String country_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("country_code")
    @Expose
    private String country_code;

    public Countries() {
    }

    public Countries(String country_id, String name, String country_code) {
        this.country_id = country_id;
        this.name = name;
        this.country_code = country_code;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
