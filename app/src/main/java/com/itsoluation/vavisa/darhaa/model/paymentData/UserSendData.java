package com.itsoluation.vavisa.darhaa.model.paymentData;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressAdd;

public class UserSendData implements Parcelable {

    @SerializedName("device_id")
    @Expose
    private String device_id;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("address_id")
    @Expose
    private String address_id;


    @SerializedName("address")
    @Expose
    private AddressAdd address;

    public UserSendData() {
    }

    // for user
    public UserSendData(String device_id, String user_id, String address_id) {
        this.device_id = device_id;
        this.user_id = user_id;
        this.address_id = address_id;
    }

    // for guest
    public UserSendData(String device_id, String user_id, AddressAdd address) {
        this.device_id = device_id;
        this.user_id = user_id;
        this.address = address;
    }


    protected UserSendData(Parcel in) {
        device_id = in.readString();
        user_id = in.readString();
        address_id = in.readString();
        address = in.readParcelable(AddressAdd.class.getClassLoader());
    }

    public static final Creator<UserSendData> CREATOR = new Creator<UserSendData>() {
        @Override
        public UserSendData createFromParcel(Parcel in) {
            return new UserSendData(in);
        }

        @Override
        public UserSendData[] newArray(int size) {
            return new UserSendData[size];
        }
    };

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public AddressAdd getAddress() {
        return address;
    }

    public void setAddress(AddressAdd address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(device_id);
        dest.writeString(user_id);
        dest.writeString(address_id);
        dest.writeParcelable(address, flags);
    }
}
