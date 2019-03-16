package com.itsoluation.vavisa.darhaa.model.paymentData;

public class ShippingAddressData {

    String firstname;
    String company;
    String address_1;
    String address_2;
    String postcode;
    String city;
    String zone_id;
    String zone;
    String country_id;
    String country;

    public ShippingAddressData() {
    }

    public ShippingAddressData(String firstname, String company, String address_1, String address_2, String postcode, String city, String zone_id, String zone, String country_id, String country) {
        this.firstname = firstname;
        this.company = company;
        this.address_1 = address_1;
        this.address_2 = address_2;
        this.postcode = postcode;
        this.city = city;
        this.zone_id = zone_id;
        this.zone = zone;
        this.country_id = country_id;
        this.country = country;
    }

    public ShippingAddressData(String firstname, String address_1, String city, String zone_id, String zone, String country_id, String country) {
        this.firstname = firstname;
        this.address_1 = address_1;
        this.city = city;
        this.zone_id = zone_id;
        this.zone = zone;
        this.country_id = country_id;
        this.country = country;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
