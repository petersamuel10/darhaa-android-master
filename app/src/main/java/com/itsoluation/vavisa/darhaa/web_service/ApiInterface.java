package com.itsoluation.vavisa.darhaa.web_service;

import com.google.gson.JsonElement;
import com.itsoluation.vavisa.darhaa.model.cartData.CartData;
import com.itsoluation.vavisa.darhaa.model.home.Home;
import com.itsoluation.vavisa.darhaa.model.ProfileData;
import com.itsoluation.vavisa.darhaa.model.Status;
import com.itsoluation.vavisa.darhaa.model.User;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressDetails;
import com.itsoluation.vavisa.darhaa.model.address.address.AddressGet;
import com.itsoluation.vavisa.darhaa.model.address.address.Countries;
import com.itsoluation.vavisa.darhaa.model.address.address.areaAndCity.AreaAndCities;
import com.itsoluation.vavisa.darhaa.model.orders.OrdersData;

import java.util.ArrayList;

import io.reactivex.Observable;
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

    @FormUrlEncoded
    @POST("index.php?route=restapi/address/addAddress")
    Observable<Status> addAddress(@Field("user_id") Integer user_id,@Field("firstname") String firstname,
                                  @Field("address_1") String address_1,@Field("city") String city,@Field("country_id") String country_id,
                                  @Field("postcode") String postcode,@Field("zone_id") String zone_id,@Field("title") String title,
                                  @Field("default") String default_);

    @FormUrlEncoded
    @POST("index.php?route=restapi/address/editAddress")
    Observable<Status> editAddress(@Field("user_id") Integer user_id,@Field("address_id") String address_id,@Field("firstname") String firstname,
                                  @Field("address_1") String address_1,@Field("city") String city,@Field("country_id") String country_id,
                                  @Field("postcode") String postcode,@Field("zone_id") String zone_id,@Field("title") String title,
                                  @Field("default") String default_);

    @POST("index.php?route=restapi/common/getCountries")
    Observable<ArrayList<Countries>> getCountries();

    @GET("index.php?route=restapi/common/getAreaCities")
    Observable<AreaAndCities> getAreaAndCities(@Query("country_id") String country_id, @Query("country_code") String country_code);

    @FormUrlEncoded
    @POST("index.php?route=restapi/address/deleteAddress")
    Observable<Status> deleteAddress(@Field("user_id") String user_id, @Field("address_id") String address_id);

    @GET("index.php?route=restapi/address/getAddress")
    Observable<AddressDetails> getAddress(@Query("user_id") String user_id, @Query("address_id") String address_id);

    @FormUrlEncoded
    @POST("index.php?route=restapi/profile/edit")
    Observable<Status> editProfile(@Field("firstname") String firstname, @Field("email") String email,
                                   @Field("telephone") String telephone, @Field("user_id") String user_id);

    @GET("index.php?route=restapi/order")
    Observable<OrdersData> getOrders(@Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("index.php?route=restapi/product/addToWishlist")
    Observable<Status> addFavorte(@Field("product_id") String product_id, @Field("user_id") Integer user_id);

    @FormUrlEncoded
    @POST("index.php?route=restapi/product/deleteWishlist")
    Observable<Status> removeFavorte(@Field("product_id") String product_id, @Field("user_id") Integer user_id);


    @FormUrlEncoded
    @POST("index.php?route=restapi/password")
    Observable<Status> changePassword(@Field("password") String password, @Field("confirm") String confirm,
                                      @Field("user_id") Integer user_id,@Field("oldpassword") String oldpassword);

    @FormUrlEncoded
    @POST("index.php?route=restapi/cart/add")
    Observable<Status> addToCart(@Field("password") String password, @Field("confirm") String confirm,
                                      @Field("user_id") Integer user_id,@Field("oldpassword") String oldpassword);

    @GET("index.php?route=restapi/cart")
    Observable<CartData> viewCart(@Query("user_id") String user_id, @Query("device_id") String device_id);

    @FormUrlEncoded
    @POST("index.php?route=restapi/cart/remove")
    Observable<Status> deleteCart(@Query("cart_id") String cart_id,@Query("user_id") String user_id, @Query("device_id") String device_id);
}
