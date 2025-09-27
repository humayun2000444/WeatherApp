package com.example.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private JsonArray hourlyData;

    public HourlyForecastAdapter(JsonArray hourlyData) {
        this.hourlyData = hourlyData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_forecast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JsonObject hourData = hourlyData.get(position).getAsJsonObject();
        String time = hourData.get("time").getAsString();
        String temp = hourData.get("temp_c").getAsString();
        String condition = hourData.getAsJsonObject("condition").get("text").getAsString();

        String formattedTime = formatTime(time, position);
        holder.timeTextView.setText(formattedTime);
        holder.tempTextView.setText(Math.round(Double.parseDouble(temp)) + "°");
        holder.conditionTextView.setText(condition);

        setWeatherIcon(holder.weatherIconImageView, condition);
    }

    private String formatTime(String dateTimeString, int position) {
        try {
            if (position == 0) return "Now";

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateTimeString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateTimeString.substring(dateTimeString.length() - 5);
        }
    }

    private void setWeatherIcon(ImageView imageView, String condition) {
        String conditionLower = condition.toLowerCase();
        if (conditionLower.contains("sunny") || conditionLower.contains("clear")) {
            imageView.setImageResource(R.drawable.weather_sunny_cute);
        } else {
            imageView.setImageResource(R.drawable.weather_sunny_cute);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(hourlyData.size(), 24);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView tempTextView;
        public TextView conditionTextView;
        public ImageView weatherIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            conditionTextView = itemView.findViewById(R.id.conditionTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
        }
    }
}
