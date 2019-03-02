package com.itsoluation.vavisa.darhaa.model.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Catecory {

    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("isSubCat")
    @Expose
    private boolean isSubCat ;

    public Catecory() {
    }

    public Catecory(String category_id, String image, String name, boolean isSubCat) {
        this.category_id = category_id;
        this.image = image;
        this.name = name;
        this.isSubCat = isSubCat;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSubCat() {
        return isSubCat;
    }

    public void setSubCat(boolean subCat) {
        isSubCat = subCat;
    }
}
