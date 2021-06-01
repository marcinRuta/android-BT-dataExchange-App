package com.example.application;
import com.example.application.DTO.DataExchange;
import com.example.application.DTO.DataSent;
import com.example.application.DTO.LogData;
import com.example.application.DTO.RegData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIInterface {
    @Headers({"accept: text/json","Content-Type: application/json"})
    @POST("login")
    Call logUser(@Body LogData logData);

    @Headers({"accept: text/json","Content-Type: application/json"})
    @POST("register")
    Call registerUser(@Body RegData RegData);

    @Headers({"accept: text/json","Content-Type: application/json"})
    @POST("send-message")
    Call postMessage(@Body DataSent dataSent,
                     @Header("nazwa") String username,
                     @Header("haslo") String password);
}
