package com.itsoluations.vavisa.darhaa.model.addToCart;

public class Options {

    String id;
    String value;

    public Options() {
    }

    public Options(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}