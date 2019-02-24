package com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AreaAndCities {

    @SerializedName("areas")
    @Expose
    private ArrayList<Area> areas;

    @SerializedName("cities")
    @Expose
    private ArrayList<City> cities;

    public AreaAndCities() {
    }

    public AreaAndCities(ArrayList<Area> areas, ArrayList<City> cities) {
        this.areas = areas;
        this.cities = cities;
    }

    public ArrayList<Area> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<Area> areas) {
        this.areas = areas;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }
}
