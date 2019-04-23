package com.itsoluations.vavisa.darhaa.model.address.address.areaAndCity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("CityName")
    @Expose
    private String CityName;

    public City() {
    }

    public City(String cityName) {
        CityName = cityName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

}
