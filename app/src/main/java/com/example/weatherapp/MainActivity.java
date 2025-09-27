package com.example.weatherapp;

import com.example.weatherapp.adapter.HourlyForecastAdapter;
import com.example.weatherapp.adapter.DailyForecastAdapter;
import com.example.weatherapp.adapter.WeatherAlertsAdapter;
import com.example.weatherapp.utils.WeatherCache;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String API_KEY = BuildConfig.WEATHER_API_KEY;

    private TextInputEditText cityInput;
    private ImageView getWeatherButton;
    private FloatingActionButton fabCurrentLocation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgressIndicator progressIndicator;

    private MaterialCardView currentWeatherCard;
    private MaterialCardView hourlyForecastCard;
    private MaterialCardView dailyForecastCard;
    private MaterialCardView weatherAlertsCard;
    private TextView currentLocationText;
    private TextView lastUpdatedText;
    private TextView temperatureText;
    private TextView conditionText;
    private TextView feelsLikeText;
    private TextView highLowText;
    private TextView errorMessage;
    private ImageView weatherIcon;
    private ImageView favoriteIcon;

    private RecyclerView hourlyForecastRecyclerView;
    private RecyclerView dailyForecastRecyclerView;
    private RecyclerView weatherAlertsRecyclerView;
    private HourlyForecastAdapter hourlyForecastAdapter;
    private DailyForecastAdapter dailyForecastAdapter;
    private WeatherAlertsAdapter weatherAlertsAdapter;
    private OkHttpClient client;
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherCache weatherCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();

        client = new OkHttpClient();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weatherCache = new WeatherCache(this);

        checkLocationPermissionAndGetWeather();
    }

    private void initializeViews() {
        cityInput = findViewById(R.id.cityInput);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        fabCurrentLocation = findViewById(R.id.fabCurrentLocation);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressIndicator = findViewById(R.id.progressIndicator);

        currentWeatherCard = findViewById(R.id.currentWeatherCard);
        hourlyForecastCard = findViewById(R.id.hourlyForecastCard);
        dailyForecastCard = findViewById(R.id.dailyForecastCard);
        weatherAlertsCard = findViewById(R.id.weatherAlertsCard);

        setupWeatherDetailCards();
        currentLocationText = findViewById(R.id.currentLocationText);
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        temperatureText = findViewById(R.id.temperatureText);
        conditionText = findViewById(R.id.conditionText);
        feelsLikeText = findViewById(R.id.feelsLikeText);
        highLowText = findViewById(R.id.highLowText);
        errorMessage = findViewById(R.id.errorMessage);
        weatherIcon = findViewById(R.id.weatherIcon);
        favoriteIcon = findViewById(R.id.favoriteIcon);

        hourlyForecastRecyclerView = findViewById(R.id.hourlyForecastRecyclerView);
        dailyForecastRecyclerView = findViewById(R.id.dailyForecastRecyclerView);
        weatherAlertsRecyclerView = findViewById(R.id.weatherAlertsRecyclerView);
    }

    private void setupClickListeners() {
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeather(city);
                } else {
                    showError("Please enter a city name.");
                }
            }
        });

        fabCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermissionAndGetWeather();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeatherData();
            }
        });

        cityInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeather(city);
                }
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        hourlyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dailyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherAlertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupWeatherDetailCards() {
        // Setup UV Index Card
        ImageView uvIcon = findViewById(R.id.uvIndexCard).findViewById(R.id.detailIcon);
        TextView uvLabel = findViewById(R.id.uvIndexCard).findViewById(R.id.detailLabel);
        uvIcon.setImageResource(R.drawable.ic_uv_index);
        uvLabel.setText("UV Index");

        // Setup Humidity Card
        ImageView humidityIcon = findViewById(R.id.humidityCard).findViewById(R.id.detailIcon);
        TextView humidityLabel = findViewById(R.id.humidityCard).findViewById(R.id.detailLabel);
        humidityIcon.setImageResource(R.drawable.ic_humidity);
        humidityLabel.setText("Humidity");

        // Setup Wind Card
        ImageView windIcon = findViewById(R.id.windCard).findViewById(R.id.detailIcon);
        TextView windLabel = findViewById(R.id.windCard).findViewById(R.id.detailLabel);
        windIcon.setImageResource(R.drawable.ic_wind);
        windLabel.setText("Wind");

        // Setup Visibility Card
        ImageView visibilityIcon = findViewById(R.id.visibilityCard).findViewById(R.id.detailIcon);
        TextView visibilityLabel = findViewById(R.id.visibilityCard).findViewById(R.id.detailLabel);
        visibilityIcon.setImageResource(R.drawable.ic_visibility);
        visibilityLabel.setText("Visibility");

        // Setup Sunrise Card
        ImageView sunriseIcon = findViewById(R.id.sunriseCard).findViewById(R.id.detailIcon);
        TextView sunriseLabel = findViewById(R.id.sunriseCard).findViewById(R.id.detailLabel);
        sunriseIcon.setImageResource(R.drawable.ic_sunrise);
        sunriseLabel.setText("Sunrise");

        // Setup Sunset Card
        ImageView sunsetIcon = findViewById(R.id.sunsetCard).findViewById(R.id.detailIcon);
        TextView sunsetLabel = findViewById(R.id.sunsetCard).findViewById(R.id.detailLabel);
        sunsetIcon.setImageResource(R.drawable.ic_sunset);
        sunsetLabel.setText("Sunset");
    }

    private void checkLocationPermissionAndGetWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndWeather();
        }
    }

    private void getLocationAndWeather() {
        showLoading();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String locationQuery = location.getLatitude() + "," + location.getLongitude();
                            getWeather(locationQuery);
                            getAddressFromLocation(location);
                        } else {
                            hideLoading();
                            showError("Unable to retrieve location. Please enter a city name.");
                        }
                    }
                });
    }

    private void refreshWeatherData() {
        String currentCity = cityInput.getText().toString().trim();
        if (!currentCity.isEmpty()) {
            getWeather(currentCity);
        } else {
            getLocationAndWeather();
        }
    }

    private void showLoading() {
        progressIndicator.setVisibility(View.VISIBLE);
        currentWeatherCard.setVisibility(View.GONE);
        hourlyForecastCard.setVisibility(View.GONE);
        dailyForecastCard.setVisibility(View.GONE);
        weatherAlertsCard.setVisibility(View.GONE);
        findViewById(R.id.errorContainer).setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressIndicator.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showError(String message) {
        hideLoading();
        errorMessage.setText(message);
        findViewById(R.id.errorContainer).setVisibility(View.VISIBLE);
        currentWeatherCard.setVisibility(View.GONE);
        hourlyForecastCard.setVisibility(View.GONE);
        dailyForecastCard.setVisibility(View.GONE);
        weatherAlertsCard.setVisibility(View.GONE);
    }

    private void showWeatherData() {
        hideLoading();
        findViewById(R.id.errorContainer).setVisibility(View.GONE);
        currentWeatherCard.setVisibility(View.VISIBLE);
        hourlyForecastCard.setVisibility(View.VISIBLE);
        dailyForecastCard.setVisibility(View.VISIBLE);
        updateLastUpdatedTime();
    }

    private void updateLastUpdatedTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        String currentTime = sdf.format(new java.util.Date());
        lastUpdatedText.setText("Updated at " + currentTime);
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                final String addressText = "Current Location: " + address.getLocality() + ", " + address.getCountryName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentLocationText.setText("📍 " + addressText.replace("Current Location: ", ""));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentLocationText.setText("📍 Unable to determine current location.");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentLocationText.setText("📍 Unable to determine current location.");
                }
            });
        }
    }

    private void getWeather(String query) {
        // Check cache first
        JsonObject cachedData = weatherCache.getCachedWeatherData(query);
        if (cachedData != null) {
            Log.d("WeatherApp", "Using cached data for: " + query);
            processCachedWeatherData(cachedData);
            return;
        }

        showLoading();
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + "&q=" + query + "&days=10&aqi=yes&alerts=yes";
        Log.d("WeatherApp", "Request URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("WeatherApp", "Failed to make request: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError("Failed to get weather data. Please check your internet connection and try again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("WeatherApp", "Unexpected code: " + response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showError("Failed to get weather data. Please try again.");
                        }
                    });
                    return;
                }

                String responseData = response.body().string();
                Log.d("WeatherApp", "Response Data: " + responseData);

                try {
                    JsonObject json = JsonParser.parseString(responseData).getAsJsonObject();

                    // Cache the response data
                    weatherCache.saveWeatherData(query, responseData);
                    JsonObject current = json.getAsJsonObject("current");
                    JsonObject location = json.getAsJsonObject("location");
                    JsonObject forecast = json.getAsJsonObject("forecast");
                    JsonArray alerts = json.has("alerts") && json.getAsJsonObject("alerts").has("alert") ?
                        json.getAsJsonObject("alerts").getAsJsonArray("alert") : null;
                    JsonArray forecastDays = forecast.getAsJsonArray("forecastday");
                    JsonObject todayForecast = forecastDays.get(0).getAsJsonObject();
                    final JsonArray hourly = todayForecast.getAsJsonArray("hour");
                    final JsonArray daily = forecastDays;

                    final String tempC = current.get("temp_c").getAsString();
                    final String feelsLikeC = current.get("feelslike_c").getAsString();
                    final String condition = current.getAsJsonObject("condition").get("text").getAsString();
                    final String humidity = current.get("humidity").getAsString();
                    final String windKph = current.get("wind_kph").getAsString();
                    final String pressureMb = current.get("pressure_mb").getAsString();
                    final String visibilityKm = current.get("vis_km").getAsString();
                    final String uvIndex = current.get("uv").getAsString();
                    final String locationName = location.get("name").getAsString();
                    final String country = location.get("country").getAsString();

                    // Today's high and low
                    JsonObject todayDay = todayForecast.getAsJsonObject("day");
                    final String maxTemp = todayDay.get("maxtemp_c").getAsString();
                    final String minTemp = todayDay.get("mintemp_c").getAsString();

                    // Astronomy data
                    JsonObject astro = todayForecast.getAsJsonObject("astro");
                    final String sunrise = astro.get("sunrise").getAsString();
                    final String sunset = astro.get("sunset").getAsString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateWeatherUI(tempC, feelsLikeC, condition, locationName, country, maxTemp, minTemp);
                            updateWeatherDetails(humidity, windKph, pressureMb, visibilityKm, uvIndex, sunrise, sunset);
                            displayHourlyForecast(hourly);
                            displayDailyForecast(daily);
                            displayWeatherAlerts(alerts);
                            showWeatherData();
                        }
                    });
                } catch (Exception e) {
                    Log.e("WeatherApp", "Error parsing weather data: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showError("Error processing weather data. Please try again.");
                        }
                    });
                }
            }
        });
    }

    private void processCachedWeatherData(JsonObject json) {
        try {
            JsonObject current = json.getAsJsonObject("current");
            JsonObject location = json.getAsJsonObject("location");
            JsonObject forecast = json.getAsJsonObject("forecast");
            JsonArray alerts = json.has("alerts") && json.getAsJsonObject("alerts").has("alert") ?
                json.getAsJsonObject("alerts").getAsJsonArray("alert") : null;
            JsonArray forecastDays = forecast.getAsJsonArray("forecastday");
            JsonObject todayForecast = forecastDays.get(0).getAsJsonObject();
            JsonArray hourly = todayForecast.getAsJsonArray("hour");

            String tempC = current.get("temp_c").getAsString();
            String feelsLikeC = current.get("feelslike_c").getAsString();
            String condition = current.getAsJsonObject("condition").get("text").getAsString();
            String humidity = current.get("humidity").getAsString();
            String windKph = current.get("wind_kph").getAsString();
            String pressureMb = current.get("pressure_mb").getAsString();
            String visibilityKm = current.get("vis_km").getAsString();
            String uvIndex = current.get("uv").getAsString();
            String locationName = location.get("name").getAsString();
            String country = location.get("country").getAsString();

            // Today's high and low
            JsonObject todayDay = todayForecast.getAsJsonObject("day");
            String maxTemp = todayDay.get("maxtemp_c").getAsString();
            String minTemp = todayDay.get("mintemp_c").getAsString();

            // Astronomy data
            JsonObject astro = todayForecast.getAsJsonObject("astro");
            String sunrise = astro.get("sunrise").getAsString();
            String sunset = astro.get("sunset").getAsString();

            updateWeatherUI(tempC, feelsLikeC, condition, locationName, country, maxTemp, minTemp);
            updateWeatherDetails(humidity, windKph, pressureMb, visibilityKm, uvIndex, sunrise, sunset);
            displayHourlyForecast(hourly);
            displayDailyForecast(forecastDays);
            displayWeatherAlerts(alerts);
            showWeatherData();
        } catch (Exception e) {
            Log.e("WeatherApp", "Error processing cached data: " + e.getMessage());
            showError("Error processing cached data. Refreshing...");
            weatherCache.clearCache();
        }
    }

    private void updateWeatherUI(String tempC, String feelsLikeC, String condition, String locationName, String country, String maxTemp, String minTemp) {
        temperatureText.setText(Math.round(Double.parseDouble(tempC)) + "°");
        conditionText.setText(condition);
        feelsLikeText.setText("Feels like " + Math.round(Double.parseDouble(feelsLikeC)) + "°");
        currentLocationText.setText("📍 " + locationName + ", " + country);
        highLowText.setText("H:" + Math.round(Double.parseDouble(maxTemp)) + "° L:" + Math.round(Double.parseDouble(minTemp)) + "°");

        updateWeatherIcon(condition);
    }

    private void updateWeatherDetails(String humidity, String windKph, String pressureMb, String visibilityKm, String uvIndex, String sunrise, String sunset) {
        // Update UV Index
        TextView uvValue = findViewById(R.id.uvIndexCard).findViewById(R.id.detailValue);
        uvValue.setText(uvIndex);

        // Update Humidity
        TextView humidityValue = findViewById(R.id.humidityCard).findViewById(R.id.detailValue);
        humidityValue.setText(humidity + "%");

        // Update Wind
        TextView windValue = findViewById(R.id.windCard).findViewById(R.id.detailValue);
        windValue.setText(Math.round(Double.parseDouble(windKph)) + " km/h");

        // Update Visibility
        TextView visibilityValue = findViewById(R.id.visibilityCard).findViewById(R.id.detailValue);
        visibilityValue.setText(visibilityKm + " km");

        // Update Sunrise
        TextView sunriseValue = findViewById(R.id.sunriseCard).findViewById(R.id.detailValue);
        sunriseValue.setText(sunrise);

        // Update Sunset
        TextView sunsetValue = findViewById(R.id.sunsetCard).findViewById(R.id.detailValue);
        sunsetValue.setText(sunset);
    }

    private void displayDailyForecast(JsonArray daily) {
        dailyForecastAdapter = new DailyForecastAdapter(daily);
        dailyForecastRecyclerView.setAdapter(dailyForecastAdapter);
    }

    private void updateWeatherIcon(String condition) {
        String conditionLower = condition.toLowerCase();
        if (conditionLower.contains("sunny") || conditionLower.contains("clear")) {
            weatherIcon.setImageResource(R.drawable.weather_sunny_cute);
        } else {
            weatherIcon.setImageResource(R.drawable.weather_sunny_cute);
        }
    }

    private void displayHourlyForecast(JsonArray hourly) {
        hourlyForecastAdapter = new HourlyForecastAdapter(hourly);
        hourlyForecastRecyclerView.setAdapter(hourlyForecastAdapter);
    }

    private void displayWeatherAlerts(JsonArray alerts) {
        if (alerts != null && alerts.size() > 0) {
            weatherAlertsAdapter = new WeatherAlertsAdapter(alerts);
            weatherAlertsRecyclerView.setAdapter(weatherAlertsAdapter);
            weatherAlertsCard.setVisibility(View.VISIBLE);
        } else {
            weatherAlertsCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndWeather();
            } else {
                showError("Location permission denied. Please enter a city name.");
            }
        }
    }
}