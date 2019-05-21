package com.itsoluations.vavisa.darhaa.web_service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itsoluations.vavisa.darhaa.common.Common;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller2 {

    final static String BASE_URL = "http://173.231.196.229/~hvavisa/darhaa_test/";
    final static String API_TOKEN = "16aac5b8216f1134770e665a8d  ";
    final static String CONTENT_TYPE = "application/x-www-form-urlencoded";
    // static String USER_ACCESS_TOKEN = Common.userAccessToken;
    static String LANGUAGE = "en";
    private ApiInterface apiInterface;

    public Controller2(final String userAccess) {
        try {
            if (Common.isArabic)
                LANGUAGE = "ar";
            else
                LANGUAGE = "en";

            OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request original_request = chain.request();

                    Request.Builder builder = original_request.newBuilder()
                            .addHeader("api-token", API_TOKEN)
                            .addHeader("Content-Type", CONTENT_TYPE)
                            .addHeader("language", LANGUAGE)
                            .addHeader("useraccesstoken", userAccess);

                    Request newRequest = builder.build();


                    return chain.proceed(newRequest);
                }
            }).build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();

            apiInterface = retrofit.create(ApiInterface.class);

        } catch (Exception e) {
            Log.i("errrr", e.getMessage());
        }
    }

    public ApiInterface getAPI() {
        return apiInterface;
    }
}
