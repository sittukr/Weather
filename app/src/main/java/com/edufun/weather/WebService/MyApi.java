package com.edufun.weather.WebService;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MyApi {


    @GET("weather")
    Call<JsonObject> weather(
            @Query("q") String state,
            @Query("appid") String appId
    );
}
