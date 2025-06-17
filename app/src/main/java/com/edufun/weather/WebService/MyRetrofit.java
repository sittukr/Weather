package com.edufun.weather.WebService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyRetrofit {

    public static String APPID = "0e29648800c341f7aaa418254bb8bfda";
    public static String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    public static MyRetrofit MyInstance;
    public static Retrofit retrofit;

    public MyRetrofit(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(7000, TimeUnit.MILLISECONDS)
                .readTimeout(7000, TimeUnit.MILLISECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }


    public static synchronized MyRetrofit apiInstance(){
        if (MyInstance==null){
            MyInstance= new MyRetrofit();
        }
        return MyInstance;
    }

    public MyApi getApi(){
        return retrofit.create(MyApi.class);
    }
}
