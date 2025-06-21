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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edufun.weather.Adapter.ForecastAdapter;
import com.edufun.weather.Models.ForecastModel;
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
import java.util.ArrayList;
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
    String sunrise,sunset;

    ArrayList<ForecastModel> list;
    ForecastAdapter adapter;

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

                        currentAirPollution(lat,lon);
                        getForecast(lat,lon);

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
                         sunrise = sysObj.getString("sunrise");
                         sunset = sysObj.getString("sunset");

                        String timezone = bodyObject.getString("timezone");
                        String id = bodyObject.getString("id");
                        String name = bodyObject.getString("name");
                        String cod = bodyObject.getString("cod");

                        binding.tvTemp.setText(MyFun.ktoC(temp)+" \u00b0C");
                        binding.tvFell.setText(" Fells like "+MyFun.ktoCInt(feels_like)+" \u00B0C");
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


    private void currentAirPollution(String lat,String lon){
        Call<JsonObject> call = MyRetrofit.apiInstance().getApi().currentAirPollution(lat,lon,APPID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    String body = response.body().toString();
                    try {
                        JSONObject bodyObj = new JSONObject(body);

                        JSONObject coordObj = bodyObj.getJSONObject("coord");
                        String latitude = coordObj.getString("lat");
                        String longitude = coordObj.getString("lon");

                        JSONArray listArray = bodyObj.getJSONArray("list");
                        for (int i = 0; i<listArray.length(); i++){
                            JSONObject listObj =listArray.getJSONObject(i);

                            JSONObject dataObj = listObj.getJSONObject("main");
                            String aqi = dataObj.getString("aqi");

                            JSONObject componentsObj =listObj.getJSONObject("components");
                            String co = componentsObj.getString("co");
                            String no = componentsObj.getString("no");
                            String no2 = componentsObj.getString("no2");
                            String o3 = componentsObj.getString("o3");
                            String so2 = componentsObj.getString("so2");
                            String pm2_5 = componentsObj.getString("pm2_5");
                            String pm10 = componentsObj.getString("pm10");
                            String nh3 = componentsObj.getString("nh3");

                            binding.tvCO.setText(co);
                            binding.tvNO2.setText(no2);
                            binding.tvO3.setText(o3);
                            binding.tvSO2.setText(so2);
                            binding.tvPM25.setText(pm2_5);
                            binding.tvPM10.setText(pm10);

                            String status = "";
                            if (aqi.equalsIgnoreCase("1")){
                                status = "Good";
                                binding.pollutionStatus.setTextColor(getResources().getColor(R.color.green));
                            } else if (aqi.equalsIgnoreCase("2")) {
                                status = "Fair";
                                binding.pollutionStatus.setTextColor(getResources().getColor(R.color.orange));
                            }else if (aqi.equalsIgnoreCase("3")){
                                status = "Moderate";
                                binding.pollutionStatus.setTextColor(getResources().getColor(R.color.red_orange));
                            } else if (aqi.equalsIgnoreCase("4")) {
                                status = "Poor";
                                binding.pollutionStatus.setTextColor(getResources().getColor(R.color.red));
                            } else if (aqi.equalsIgnoreCase("5")) {
                                status = "Very Poor";
                                binding.pollutionStatus.setTextColor(getResources().getColor(R.color.red));
                            }
                            binding.pollutionStatus.setText(status);


                            String dt = listObj.getString("dt");
                        }


                    } catch (JSONException e) {
                        MyFun.customDialog(context,e.getMessage(),R.drawable.clouds_logo);
                    }
                }else {
                    MyFun.customDialog(context,"Something went wrong",R.drawable.clouds_logo);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                MyFun.customDialog(context,t.getMessage(),R.drawable.clouds_logo);
            }
        });
    }

    private void getForecast(String lat ,String lon){
        Call<JsonObject> call = MyRetrofit.apiInstance().getApi().forecast(lat,lon,APPID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    String body = response.body().toString();
                    list = new ArrayList<>();
                    try {
                        JSONObject responseObj = new JSONObject(body);
                        String cnt = responseObj.getString("cnt");

                        JSONObject cityObj = responseObj.getJSONObject("city");
                        String population = cityObj.getString("population");

                        JSONArray listArray = responseObj.getJSONArray("list");
                        for (int i = 0; i<listArray.length(); i++){
                            JSONObject listObj = listArray.getJSONObject(i);
                            String dt = listObj.getString("dt");
                            String dt_text = listObj.getString("dt_txt");
                            String pop = listObj.getString("pop");

                            JSONObject mainObj = listObj.getJSONObject("main");
                            String temp = mainObj.getString("temp");
                            String feels_like = mainObj.getString("feels_like");
                            String pressure = mainObj.getString("pressure");
                            String sea_level = mainObj.getString("sea_level");
                            String ground_level = mainObj.getString("grnd_level");
                            String humidity = mainObj.getString("humidity");
                            String temp_kf = mainObj.getString("temp_kf");

                            JSONArray weatherArray = listObj.getJSONArray("weather");
                            int l = weatherArray.length();
                            String main="",description="",icon="";
                            for (int j =0; j<weatherArray.length(); j++ ){
                                JSONObject weatherObj = weatherArray.getJSONObject(j);
                                 main = weatherObj.getString("main");
                                 description = weatherObj.getString("description");
                                 icon = weatherObj.getString("icon");
                            }
                            JSONObject cloudObj = listObj.getJSONObject("clouds");
                            String cloudiness = cloudObj.getString("all");

                            JSONObject windObj = listObj.getJSONObject("wind");
                            String speed = windObj.getString("speed");
                            String deg = windObj.getString("deg");
                            String gust = windObj.getString("gust");

                            String rain_volume="",snow_volume="";
                            if (listObj.has("rain") && !listObj.isNull("rain")) {
                                JSONObject rainObj = listObj.getJSONObject("rain");
                                 rain_volume = rainObj.getString("3h");
                            }
                            if (listObj.has("snow") && !listObj.isNull("snow")) {
                                JSONObject snowObj = listObj.getJSONObject("snow");
                                 snow_volume = snowObj.getString("3h");
                                // Toast.makeText(MainActivity.this, rain_volume, Toast.LENGTH_SHORT).show();
                            }

                            JSONObject sysObj = listObj.getJSONObject("sys");
                            // n= night, d= day
                            String shift = sysObj.getString("pod");

                            ForecastModel model = new ForecastModel();
                            model.setPopulation(population);
                            model.setDt(dt);
                            model.setDt_text(dt_text);
                            model.setPop(pop);

                            model.setTemp(temp);
                            model.setFeels_like(feels_like);
                            model.setPressure(pressure);
                            model.setSea_level(sea_level);
                            model.setGround_level(ground_level);
                            model.setHumidity(humidity);
                            model.setTemp_kf(temp_kf);

                            model.setMain(main);
                            model.setDescription(description);
                            model.setIcon(icon);

                            model.setCloudiness(cloudiness);

                            model.setSpeed(speed);
                            model.setDeg(deg);
                            model.setGust(gust);

                            model.setRain_volume(rain_volume);
                            model.setSnow_volume(snow_volume);

                            list.add(model);

                            adapter = new ForecastAdapter(context,list,binding);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false);
                            binding.recyclerview.setLayoutManager(linearLayoutManager);
                            binding.recyclerview.setAdapter(adapter);

                            binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    int firstVisibility = linearLayoutManager.findFirstVisibleItemPosition();
                                    int lastVisibility = linearLayoutManager.findLastVisibleItemPosition();
//                                    if (firstVisibility >= 0 && firstVisibility < list.size()) {
//                                        String current_date = MyFun.currentDateFormat();
//                                        String first_date = MyFun.timeformat(Long.parseLong(list.get(firstVisibility).getDt()))[0];
//                                        String last_date = MyFun.timeformat(Long.parseLong(list.get(lastVisibility).getDt()))[0];
//                                        if (current_date.equalsIgnoreCase(first_date)){
//                                            binding.tvToday.setText("Today");
//                                        }else {
//                                            if (last_date.equalsIgnoreCase(MyFun.customDateFormat(1))){
//                                                binding.tvToday.setText("Tomorrow");
//                                            }else {
//                                                binding.tvToday.setText(first_date);
//                                            }
//                                        }
//                                        if(lastVisibility >=0 && lastVisibility<list.size()){
//                                            if (first_date.equalsIgnoreCase(last_date)){
//                                                if (last_date.equalsIgnoreCase(MyFun.customDateFormat(1))){
//                                                    binding.tvNextDay.setText("Tomorrow");
//                                                }else {
//                                                    binding.tvNextDay.setText("Next 5 days >");
//                                                }
//                                            }else {
//                                                if (last_date.equalsIgnoreCase(MyFun.customDateFormat(1))){
//                                                    binding.tvNextDay.setText("Tomorrow");
//                                                }else {
//                                                    binding.tvNextDay.setText("Next 5 days >");
//                                                }
//                                            }
//                                        }
//                                    }
                                    if (firstVisibility >= 0 && firstVisibility < list.size() ){
                                        String current_date = MyFun.currentDateFormat();
                                        String first_date = Objects.requireNonNull(MyFun.timeformat(Long.parseLong(list.get(firstVisibility).getDt())))[0];
                                        String last_date = Objects.requireNonNull(MyFun.timeformat(Long.parseLong(list.get(lastVisibility).getDt())))[0];
                                        if (current_date.equalsIgnoreCase(first_date)){
                                            binding.tvToday.setText("Today");
                                            binding.tvToday.setTextColor(getResources().getColor(R.color.green));
                                        } else if (MyFun.customDateFormat(1).equalsIgnoreCase(first_date)) {
                                            binding.tvToday.setText("Tomorrow");
                                            binding.tvToday.setTextColor(getResources().getColor(R.color.pink));
                                        } else if (MyFun.customDateFormat(2).equalsIgnoreCase(first_date)) {
                                            binding.tvToday.setText(first_date);
                                            binding.tvToday.setTextColor(getResources().getColor(R.color.red));
                                        } else if (MyFun.customDateFormat(3).equalsIgnoreCase(first_date)) {
                                            binding.tvToday.setText(first_date);
                                            binding.tvToday.setTextColor(getResources().getColor(R.color.orange));
                                        } else if (MyFun.customDateFormat(4).equalsIgnoreCase(first_date)) {
                                            binding.tvToday.setText(first_date);
                                            binding.tvToday.setTextColor(getResources().getColor(R.color.pink_lite));
                                        } else if (MyFun.customDateFormat(5).equalsIgnoreCase(first_date)) {
                                            binding.tvToday.setText(first_date);
                                            binding.tvToday.setTextColor(getResources().getColor(R.color.red_orange));
                                        }
                                        if (lastVisibility >= 0 && lastVisibility < list.size()) {
                                        } if (first_date.equalsIgnoreCase(last_date)) {
                                            binding.tvNextDay.setText("Next 5 Days >");
                                            binding.tvNextDay.setTextColor(getResources().getColor(R.color.grey));
                                        } else if (MyFun.customDateFormat(1).equalsIgnoreCase(last_date)) {
                                            binding.tvNextDay.setText("Tomorrow");
                                            binding.tvNextDay.setTextColor(getResources().getColor(R.color.pink));
                                        } else if (MyFun.customDateFormat(2).equalsIgnoreCase(last_date)) {
                                            binding.tvNextDay.setText(last_date);
                                            binding.tvNextDay.setTextColor(getResources().getColor(R.color.red));
                                        } else if (MyFun.customDateFormat(3).equalsIgnoreCase(last_date)) {
                                            binding.tvNextDay.setText(last_date);
                                            binding.tvNextDay.setTextColor(getResources().getColor(R.color.orange));
                                        } else if (MyFun.customDateFormat(4).equalsIgnoreCase(last_date)) {
                                            binding.tvNextDay.setText(last_date);
                                            binding.tvNextDay.setTextColor(getResources().getColor(R.color.pink_lite));
                                        } else if (MyFun.customDateFormat(5).equalsIgnoreCase(last_date)) {
                                            binding.tvNextDay.setText(last_date);
                                            binding.tvNextDay.setTextColor(getResources().getColor(R.color.red_orange));
                                        }

                                    }
                                }
                            });

                        }



                    } catch (JSONException e) {
                        MyFun.customDialog(context,e.getMessage(),R.drawable.clouds_logo);
                    }
                }else {
                    MyFun.customDialog(context,"Something went wrong",R.drawable.clouds_logo);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                MyFun.customDialog(context,t.getMessage(),R.drawable.clouds_logo);
            }
        });
    }












}