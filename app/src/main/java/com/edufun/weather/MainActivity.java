package com.edufun.weather;

import static com.edufun.weather.MyUtils.MyFun.longToTime;
import static com.edufun.weather.WebService.MyRetrofit.APPID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.edufun.weather.MyUtils.MyFun;
import com.edufun.weather.WebService.MyRetrofit;
import com.edufun.weather.databinding.ActivityMainBinding;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Context context;
    Activity activity;
    SharedPreferences shp ;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        activity = this;
        context = this;

        shp = getSharedPreferences("city",MODE_PRIVATE);
        String saveCity = shp.getString("cityName","jaipur");
        if (MyFun.checkInternet(context)){
            getWeatherDetails(saveCity);
        }else {
            MyFun.customDialog(context, "No Internet",R.drawable.no_cloud);
        }

        binding.tvTodayDate.setText(MyFun.todayDate());

        binding.btnSearch.setOnClickListener(v -> {
            String city = binding.etSearchCity.getText().toString();
            if(!city.equalsIgnoreCase("")){
                if (MyFun.checkInternet(context)){
                    getWeatherDetails(city);
                }else {
                    MyFun.customDialog(context,"No Internet",R.drawable.no_cloud);
                }
            }else binding.etSearchCity.setError("Enter City");
        });

        //https://openweathermap.org/img/wn/10d@2x.png

    }

    private void getWeatherDetails(String city){
        Dialog dialog = MyFun.loadingDialog(context,"Processing");
        dialog.show();

        Call<JsonObject> call = MyRetrofit.apiInstance().getApi().weather(city,APPID);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    dialog.dismiss();
                    String responseBody = response.body().toString();
                    try {
                        JSONObject bodyObject = new JSONObject(responseBody);

                        String coord = bodyObject.getString("coord");
                        JSONObject coordObj = new JSONObject(coord);
                        String lat = coordObj.getString("lat");
                        String lon = coordObj.getString("lon");

                        String weather = bodyObject.getString("weather");
                        JSONArray weatherArray = bodyObject.getJSONArray("weather");
                        for (int i =0; i< weatherArray.length(); i++){
                            JSONObject weatherObj = weatherArray.getJSONObject(i);
                            String id = weatherObj.getString("id");
                            String main = weatherObj.getString("main");
                            String description = weatherObj.getString("description");
                            String icon = weatherObj.getString("icon");
                            Uri uri = Uri.parse("https://openweathermap.org/img/wn/"+icon+"@2x.png");
                            Picasso.get().load(uri).into(binding.image);
                            binding.tvDesc.setText(description);
                            binding.tvMainWeather.setText(main);
                            binding.image.setVisibility(View.VISIBLE);
                            if (main.equalsIgnoreCase("Thunderstorm")){
                                //binding.imgBg.setImageResource(R.drawable.thunderstorm);
                                binding.main.setBackgroundResource(R.drawable.thunderstorm);
                            } else if (main.equalsIgnoreCase("Drizzle")) {
                                //binding.imgBg.setImageResource(R.drawable.drizzle);
                                binding.main.setBackgroundResource(R.drawable.drizzle);
                            }else if (main.equalsIgnoreCase("Rain")) {
                                //binding.imgBg.setImageResource(R.drawable.rain);
                                binding.main.setBackgroundResource(R.drawable.rain);
                            } else if (main.equalsIgnoreCase("Snow")) {
                                //binding.imgBg.setImageResource(R.drawable.snow);
                                binding.main.setBackgroundResource(R.drawable.snow);
                            }else if (main.equalsIgnoreCase("Clear")) {
                                //binding.imgBg.setVisibility(View.GONE);
                                //binding.imgBg.setImageResource(R.drawable.clear);
                                binding.main.setBackgroundResource(R.drawable.clear);
                            }else if (main.equalsIgnoreCase("Clouds")) {
                                //binding.imgBg.setImageResource(R.drawable.clouds);
                                binding.main.setBackgroundResource(R.drawable.clouds);
                            }
                        }

                        String base = bodyObject.getString("base");

                        String main = bodyObject.getString("main");
                        JSONObject mainObj = new JSONObject(main);
                        String temp = mainObj.getString("temp");
                        String feels_like = mainObj.getString("feels_like");
                        String temp_min = mainObj.getString("temp_min");
                        String temp_max = mainObj.getString("temp_max");
                        String pressure = mainObj.getString("pressure");
                        String humidity = mainObj.getString("humidity");
                        String sea_level = mainObj.getString("sea_level");
                        String ground_level = mainObj.getString("grnd_level");

                        String visibility = bodyObject.getString("visibility");

                        String wind = bodyObject.getString("wind");
                        JSONObject windObj = new JSONObject(wind);
                        String speed = windObj.getString("speed");
                        String deg = windObj.getString("deg");
                        String gust = windObj.getString("gust");

                        String clouds = bodyObject.getString("clouds");
                        JSONObject cloudsObj = new JSONObject(clouds);
                        String all = cloudsObj.getString("all");

                        String dt = bodyObject.getString("dt");

                        String sys = bodyObject.getString("sys");
                        JSONObject sysObj = new JSONObject(sys);
                        String country = sysObj.getString("country");
                        String sunrise = sysObj.getString("sunrise");
                        String sunset = sysObj.getString("sunset");

                        String timezone = bodyObject.getString("timezone");
                        String id = bodyObject.getString("id");
                        String name = bodyObject.getString("name");
                        String cod = bodyObject.getString("cod");

                        binding.tvTemp.setText(MyFun.ktoC(temp)+" \u00b0C");
                        binding.tvFell.setText(MyFun.ktoCInt(temp_min)+" ~ "+ MyFun.ktoCInt(temp_max)+" \u00B0C" +" Fells like "+MyFun.ktoCInt(feels_like)+" \u00B0C");
                        binding.tvFellLike.setText(MyFun.ktoCInt(feels_like)+" \u00b0C");
                        binding.tvSunRaise.setText(longToTime(Long.valueOf(sunrise))[1]);
                        binding.tvSunSet.setText(longToTime(Long.valueOf(sunset))[1]);
                        binding.tvVisibility.setText(MyFun.mToKm(Double.valueOf(visibility)));
                        binding.tvHumidity.setText(humidity+" %");
                        binding.tvPressure.setText(pressure+" hPa");
                        binding.tvWind.setText(speed+" M/s");
                        binding.tvSeaLevel.setText(sea_level+" hPa");
                        binding.tvState.setText(city);
                        int dur = Integer.parseInt(sunset )- Integer.parseInt(sunrise);
                        String m = MyFun.longToTime(Long.valueOf(dur))[1];
                        binding.tvDuration.setText(m+" ");


                        editor = shp.edit();
                        editor.putString("cityName",city);
                        editor.apply();

                    } catch (JSONException e) {
                        MyFun.customDialog(context,e.getMessage(),R.drawable.clouds_logo);
                    }

                }else {
                    dialog.dismiss();
                    MyFun.customDialog(context,"Invalid City",R.drawable.clouds_logo);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                MyFun.customDialog(context,t.getMessage(),R.drawable.clouds_logo);
            }
        });
    }
















}