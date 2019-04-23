package com.itsoluations.vavisa.darhaa.model.cartData;

public class Quantity {

    public String id;
    public String value;

    public Quantity() {
    }

    public Quantity(String id, String value) {
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
