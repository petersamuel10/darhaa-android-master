package com.itsoluation.vavisa.darhaa.model.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HomeData {

    @SerializedName("recent_category")
    @Expose
    private CategoryData recent_category;

    @SerializedName("categories")
    @Expose
    private ArrayList<CategoryData> categories;

    public HomeData() {
    }

    public HomeData(CategoryData recent_category, ArrayList<CategoryData> categories) {
        this.recent_category = recent_category;
        this.categories = categories;
    }

    public CategoryData getRecent_category() {
        return recent_category;
    }

    public void setRecent_category(CategoryData recent_category) {
        this.recent_category = recent_category;
    }

    public ArrayList<CategoryData> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryData> categories) {
        this.categories = categories;
    }
}
