package com.example.weatherapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherCache {
    private static final String PREF_NAME = "weather_cache";
    private static final String KEY_WEATHER_DATA = "weather_data";
    private static final String KEY_LAST_UPDATE = "last_update";
    private static final String KEY_LOCATION = "location";
    private static final long CACHE_EXPIRY_TIME = 10 * 60 * 1000; // 10 minutes

    private SharedPreferences sharedPreferences;

    public WeatherCache(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveWeatherData(String location, String weatherJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WEATHER_DATA, weatherJson);
        editor.putString(KEY_LOCATION, location);
        editor.putLong(KEY_LAST_UPDATE, System.currentTimeMillis());
        editor.apply();
    }

    public JsonObject getCachedWeatherData(String location) {
        String cachedLocation = sharedPreferences.getString(KEY_LOCATION, "");
        long lastUpdate = sharedPreferences.getLong(KEY_LAST_UPDATE, 0);
        String weatherData = sharedPreferences.getString(KEY_WEATHER_DATA, "");

        // Check if cache is valid
        if (!cachedLocation.equalsIgnoreCase(location) ||
            weatherData.isEmpty() ||
            System.currentTimeMillis() - lastUpdate > CACHE_EXPIRY_TIME) {
            return null;
        }

        try {
            return JsonParser.parseString(weatherData).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasCachedData(String location) {
        return getCachedWeatherData(location) != null;
    }

    public void clearCache() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}