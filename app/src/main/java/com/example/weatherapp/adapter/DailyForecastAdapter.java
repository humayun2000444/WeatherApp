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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder> {

    private JsonArray dailyData;

    public DailyForecastAdapter(JsonArray dailyData) {
        this.dailyData = dailyData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_forecast_item_cute, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JsonObject dayData = dailyData.get(position).getAsJsonObject();
        JsonObject day = dayData.getAsJsonObject("day");

        String date = dayData.get("date").getAsString();
        String maxTemp = day.get("maxtemp_c").getAsString();
        String minTemp = day.get("mintemp_c").getAsString();
        String condition = day.getAsJsonObject("condition").get("text").getAsString();
        String chanceOfRain = day.get("daily_chance_of_rain").getAsString();

        String dayName = formatDate(date, position);
        holder.dayTextView.setText(dayName);
        holder.highTempTextView.setText(Math.round(Double.parseDouble(maxTemp)) + "°");
        holder.lowTempTextView.setText(Math.round(Double.parseDouble(minTemp)) + "°");
        holder.precipitationText.setText(chanceOfRain + "%");

        setWeatherIcon(holder.weatherIconImageView, condition);
    }

    private String formatDate(String dateString, int position) {
        try {
            if (position == 0) return "Today";
            if (position == 1) return "Tomorrow";

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return "Day " + (position + 1);
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
        return Math.min(dailyData.size(), 10);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dayTextView;
        public TextView highTempTextView;
        public TextView lowTempTextView;
        public TextView precipitationText;
        public ImageView weatherIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            highTempTextView = itemView.findViewById(R.id.highTempTextView);
            lowTempTextView = itemView.findViewById(R.id.lowTempTextView);
            precipitationText = itemView.findViewById(R.id.precipitationText);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
        }
    }
}