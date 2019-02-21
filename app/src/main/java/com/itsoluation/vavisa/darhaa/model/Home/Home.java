package com.itsoluation.vavisa.darhaa.model.Home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Home {

    @SerializedName("recent_category")
    @Expose
    private Catecory recent_category;

    @SerializedName("categories")
    @Expose
    private ArrayList<Catecory> categories;

    public Home() {
    }

    public Home(Catecory recent_category, ArrayList<Catecory> categories) {
        this.recent_category = recent_category;
        this.categories = categories;
    }

    public Catecory getRecent_category() {
        return recent_category;
    }

    public void setRecent_category(Catecory recent_category) {
        this.recent_category = recent_category;
    }

    public ArrayList<Catecory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Catecory> categories) {
        this.categories = categories;
    }
}
