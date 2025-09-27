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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherAlertsAdapter extends RecyclerView.Adapter<WeatherAlertsAdapter.AlertViewHolder> {

    private JsonArray alertsArray;

    public WeatherAlertsAdapter(JsonArray alertsArray) {
        this.alertsArray = alertsArray;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_alert_item, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        JsonObject alert = alertsArray.get(position).getAsJsonObject();

        String event = alert.get("event").getAsString();
        String severity = alert.get("severity").getAsString();
        String description = alert.get("desc").getAsString();
        String expires = alert.get("expires").getAsString();

        holder.alertEvent.setText(event);
        holder.alertSeverity.setText(severity.toUpperCase());
        holder.alertDescription.setText(description);

        // Format the expiry time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            Date expiryDate = inputFormat.parse(expires);
            if (expiryDate != null) {
                holder.alertTiming.setText("Until " + outputFormat.format(expiryDate));
            } else {
                holder.alertTiming.setText("Until " + expires);
            }
        } catch (ParseException e) {
            holder.alertTiming.setText("Until " + expires);
        }
    }

    @Override
    public int getItemCount() {
        return alertsArray != null ? alertsArray.size() : 0;
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView alertEvent, alertSeverity, alertDescription, alertTiming;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            alertEvent = itemView.findViewById(R.id.alertEvent);
            alertSeverity = itemView.findViewById(R.id.alertSeverity);
            alertDescription = itemView.findViewById(R.id.alertDescription);
            alertTiming = itemView.findViewById(R.id.alertTiming);
        }
    }
}