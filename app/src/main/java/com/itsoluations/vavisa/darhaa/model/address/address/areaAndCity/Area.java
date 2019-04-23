package com.itsoluations.vavisa.darhaa.model.address.address.areaAndCity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Area {


    @SerializedName("zone_id")
    @Expose
    private String zone_id;

    @SerializedName("country_id")
    @Expose
    private String country_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("status")
    @Expose
    private String status;

    public Area() {
    }

    public Area(String zone_id, String country_id, String name, String code, String status) {
        this.zone_id = zone_id;
        this.country_id = country_id;
        this.name = name;
        this.code = code;
        this.status = status;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
