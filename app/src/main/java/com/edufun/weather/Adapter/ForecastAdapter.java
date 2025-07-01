package com.edufun.weather.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edufun.weather.Models.ForecastModel;
import com.edufun.weather.MyUtils.MyFun;
import com.edufun.weather.R;
import com.edufun.weather.databinding.ActivityMainBinding;
import com.edufun.weather.databinding.WeatherItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.MyViewHolder> {

    Context context;
    ArrayList<ForecastModel> list;
    ActivityMainBinding home_binding;

    public ForecastAdapter(Context context, ArrayList<ForecastModel> list,ActivityMainBinding binding) {
        this.context = context;
        this.list = list;
        this.home_binding = binding;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        WeatherItemBinding binding = WeatherItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ForecastModel model = list.get(position);
        holder.binding.tvTime.setText(MyFun.timeformat(Long.parseLong(model.getDt()))[1]);
        holder.binding.tvDesc.setText(MyFun.ktoCInt(model.getTemp())+"\u00b0C");
        Uri uri = Uri.parse("https://openweathermap.org/img/wn/"+model.getIcon()+"@2x.png");
        Picasso.get().load(uri).into(holder.binding.imgIcon);

        if (MyFun.currentDateFormat().equalsIgnoreCase(MyFun.timeformat(Long.parseLong(model.getDt()))[0])){
            holder.binding.mainLy.setBackgroundResource(R.drawable.rounded_stock_green);
        } else if (MyFun.customDateFormat(1).equalsIgnoreCase(MyFun.timeformat(Long.parseLong(model.getDt()))[0])) {
            holder.binding.mainLy.setBackgroundResource(R.drawable.rounded_stock_pink);
        }else if (MyFun.customDateFormat(2).equalsIgnoreCase(MyFun.timeformat(Long.parseLong(model.getDt()))[0])) {
            holder.binding.mainLy.setBackgroundResource(R.drawable.rounded_stock_red);
        }else if (MyFun.customDateFormat(3).equalsIgnoreCase(MyFun.timeformat(Long.parseLong(model.getDt()))[0])) {
            holder.binding.mainLy.setBackgroundResource(R.drawable.rounded_stock_orange);
        }else if (MyFun.customDateFormat(4).equalsIgnoreCase(MyFun.timeformat(Long.parseLong(model.getDt()))[0])) {
            holder.binding.mainLy.setBackgroundResource(R.drawable.rounded_stock_pink_lite);
        }else if (MyFun.customDateFormat(5).equalsIgnoreCase(MyFun.timeformat(Long.parseLong(model.getDt()))[0])) {
            holder.binding.mainLy.setBackgroundResource(R.drawable.rounded_stock_red_orange);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        WeatherItemBinding binding;
        public MyViewHolder(WeatherItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
