package com.itsoluations.vavisa.darhaa.model.address.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressAdd implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private Integer user_id;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("address_1")
    @Expose
    private String address_1;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("country_id")
    @Expose
    private String country_id;

    @SerializedName("postcode")
    @Expose
    private String postcode;

    @SerializedName("zone_id")
    @Expose
    private String zone_id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("default")
    @Expose
    private String default_;

    @SerializedName("company")
    @Expose
    private String company;

    @SerializedName("zone")
    @Expose
    private String zone;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("address_2")
    @Expose
    private String address_2;




    public AddressAdd() {
    }

    public AddressAdd(Integer user_id, String firstname, String address_1, String city, String country_id, String postcode, String zone_id, String title, String default_) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.address_1 = address_1;
        this.city = city;
        this.country_id = country_id;
        this.postcode = postcode;
        this.zone_id = zone_id;
        this.title = title;
        this.default_ = default_;
    }

    public AddressAdd(Integer user_id, String firstname, String address_1, String city, String country_id, String postcode, String zone_id, String title) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.address_1 = address_1;
        this.city = city;
        this.country_id = country_id;
        this.postcode = postcode;
        this.zone_id = zone_id;
        this.title = title;
    }


    public AddressAdd(String firstname, String address_1, String city, String country_id,
                      String postcode, String zone_id, String company, String zone, String country,String address_2) {
        this.firstname = firstname;
        this.address_1 = address_1;
        this.city = city;
        this.country_id = country_id;
        this.postcode = postcode;
        this.zone_id = zone_id;
        this.company = company;
        this.zone = zone;
        this.country = country;
        this.address_2 = address_2;
    }

    protected AddressAdd(Parcel in) {
        if (in.readByte() == 0) {
            user_id = null;
        } else {
            user_id = in.readInt();
        }
        firstname = in.readString();
        address_1 = in.readString();
        city = in.readString();
        country_id = in.readString();
        postcode = in.readString();
        zone_id = in.readString();
        title = in.readString();
        default_ = in.readString();
        company = in.readString();
        zone = in.readString();
        country = in.readString();
        address_2 = in.readString();
    }

    public static final Creator<AddressAdd> CREATOR = new Creator<AddressAdd>() {
        @Override
        public AddressAdd createFromParcel(Parcel in) {
            return new AddressAdd(in);
        }

        @Override
        public AddressAdd[] newArray(int size) {
            return new AddressAdd[size];
        }
    };

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefault_() {
        return default_;
    }

    public void setDefault_(String default_) {
        this.default_ = default_;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (user_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(user_id);
        }
        dest.writeString(firstname);
        dest.writeString(address_1);
        dest.writeString(city);
        dest.writeString(country_id);
        dest.writeString(postcode);
        dest.writeString(zone_id);
        dest.writeString(title);
        dest.writeString(default_);
        dest.writeString(company);
        dest.writeString(zone);
        dest.writeString(country);
        dest.writeString(address_2);
    }
}
