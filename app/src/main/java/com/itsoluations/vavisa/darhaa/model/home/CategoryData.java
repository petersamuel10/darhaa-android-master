package com.itsoluations.vavisa.darhaa.model.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryData {

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
    private String isSubCat;

    @SerializedName("isProducts")
    @Expose
    private String isProduct ;

    @SerializedName("minimum")
    @Expose
    private String minimum;




    public CategoryData() {
    }

    public CategoryData(String category_id, String image, String name, String isSubCat, String isProduct) {
        this.category_id = category_id;
        this.image = image;
        this.name = name;
        this.isSubCat = isSubCat;
        this.isProduct = isProduct;
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

    public String getIsSubCat() {
        return isSubCat;
    }

    public void setIsSubCat(String isSubCat) {
        this.isSubCat = isSubCat;
    }

    public String getIsProduct() {
        return isProduct;
    }

    public void setIsProduct(String isProduct) {
        this.isProduct = isProduct;
    }
}
