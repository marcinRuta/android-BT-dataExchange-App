package com.example.application;
import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import okhttp3.OkHttpClient;

import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {
    private static Retrofit retrofit = null;
    private static final String urlAddress= "https://reqres.in";
    static Retrofit getClient() {


        OkHttpClient client = new OkHttpClient();


        retrofit = new Retrofit.Builder()
                .baseUrl(urlAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }

}

