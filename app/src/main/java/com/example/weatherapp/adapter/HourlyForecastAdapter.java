package com.example.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

        holder.timeTextView.setText(time);
        holder.tempTextView.setText(temp + "°C");
        holder.conditionTextView.setText(condition);
    }

    @Override
    public int getItemCount() {
        return hourlyData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView tempTextView;
        public TextView conditionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            conditionTextView = itemView.findViewById(R.id.conditionTextView);
        }
    }
}
