package com.example.application;
import com.example.application.DTO.DataExchange;
import com.example.application.DTO.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @POST("log")
    Call logUser(@Body User user);
    @POST("register")
    Call registerUser(@Body User user);
    @POST("message")
    Call postMessage(@Body DataExchange dataSent);
}
