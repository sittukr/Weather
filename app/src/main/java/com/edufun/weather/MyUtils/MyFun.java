package com.edufun.weather.MyUtils;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.RESTRICTIONS_SERVICE;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.edufun.weather.R;
import com.edufun.weather.databinding.LoadingBinding;
import com.edufun.weather.databinding.ShowDialogBinding;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MyFun {


    public static boolean checkInternet(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info!=null){
            if (info.getType() == ConnectivityManager.TYPE_MOBILE || info.getType() == ConnectivityManager.TYPE_WIFI){
                return true;
            }
        }
        return false;
    }

    public static String mToKm(Double m){
        if (m>=1000) {
            Double d = m / 1000;
            String km = String.format("%.1f", d );
            String kMeter = km+" Km";
            return kMeter;
        }else {
            return m+" M";
        }
    }

    public static String todayDate(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            String date = now.format(formatter);
            return  date;
        }
        return null;
    }

    public static String[] longToTime(Long time){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dateTime = Instant.ofEpochSecond(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy  hh:mm a");
            String formattedDate = dateTime.format(formatter);
            String[] date = formattedDate.split("  ");
            return date;
        }
        return null;

    }
    public static String[] timeformat(Long time){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dateTime = Instant.ofEpochSecond(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM  hh a");
            String formattedDate = dateTime.format(formatter);
            String[] s = formattedDate.split("  ");

            return s;
        }
        return null;
    }
    public static String currentDateFormat(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
            String date = now.format(formatter);
            return  date;
        }
        return null;
    }

    public static String customDateFormat(int i){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate tomorrow = LocalDate.now().plusDays(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
            String date = tomorrow.format(formatter);
            return  date;
        }
        return null;
    }

    public static String ktoCInt(String k){
        return  Integer.toString((int) (Double.parseDouble(k)-273.15));
    }

    public static String ktoC(String k){
        Double temp =  Double.parseDouble(k)-273.15;
        return String.format("%.1f",temp);
    }

    public static void customDialog(Context context,String msg, int img){
        Dialog dialog = new Dialog(context);
        ShowDialogBinding binding = ShowDialogBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_bg);
        dialog.show();
        binding.image.setImageResource(img);
        binding.tvMsg.setText(msg);
    }

    @SuppressLint("ResourceType")
    public static Dialog loadingDialog(Context context, String msg){
        Dialog dialog = new Dialog(context);
        LoadingBinding binding = LoadingBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_bg);
        dialog.show();
        Picasso.get().load(R.raw.loading_cloud).into(binding.image);
        binding.tvMsg.setText(msg);

        return dialog;
    }


}
