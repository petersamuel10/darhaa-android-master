package com.itsoluation.vavisa.darhaa.web_service;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressAdd;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluation.vavisa.darhaa.model.address.address.Countries;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.AreaAndCities;
import com.itsoluation.vavisa.darhaa.model.Home.Home;
import com.itsoluation.vavisa.darhaa.model.ProfileData;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.User;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("index.php?route=restapi/register")
    Observable<User> register(@Field("firstname") String firstname, @Field("email") String email, @Field("telephone") String telephone,
                              @Field("password") String password, @Field("confirm") String confirm);

    @FormUrlEncoded
    @POST("index.php?route=restapi/login")
    Observable<User> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("index.php?route=restapi/logout")
    Observable<Status> logout(@Field("user_id") Integer user_id, @Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("index.php?route=restapi/forgotten")
    Observable<User> forgotten(@Field("email") String email);

    @GET("index.php?route=restapi/home")
    Observable<Home> home();


    @GET("index.php?route=restapi/profile/getProfile")
    Observable<ProfileData> getProfile(@Query("user_id") Integer user_id);

    @GET("index.php?route=restapi/product/getWishlist")
    Observable<JsonElement> getWishList(@Query("user_id") Integer user_id);

    @GET("index.php?route=restapi/address")
    Observable<ArrayList<AddressGet>> addressBook(@Query("user_id") Integer user_id );

    @POST("index.php?route=restapi/address/addAddress")
    Observable<Status> addAddress(@Body AddressAdd address);

    @POST("index.php?route=restapi/common/getCountries")
    Observable<ArrayList<Countries>> getCountries();

    @GET("index.php?route=restapi/common/getAreaCities")
    Observable<AreaAndCities> getAreaAndCities(@Query("country_id") String country_id, @Query("country_code") String country_code);




}
