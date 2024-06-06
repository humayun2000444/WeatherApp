package com.example.weatherapp;

import com.example.weatherapp.adapter.HourlyForecastAdapter;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private static final String API_KEY = "6e5ceb0762ce41abbb381928240506";
    private EditText cityInput;
    private Button getWeatherButton;
    private TextView weatherResult;
    private TextView locationResult;
    private RecyclerView hourlyForecastRecyclerView;
    private HourlyForecastAdapter hourlyForecastAdapter;
    private OkHttpClient client;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        weatherResult = findViewById(R.id.weatherResult);
        locationResult = findViewById(R.id.locationResult);
        hourlyForecastRecyclerView = findViewById(R.id.hourlyForecastRecyclerView);
        client = new OkHttpClient();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeather(city);
                } else {
                    weatherResult.setText("Please enter a city name.");
                }
            }
        });

        // Set up RecyclerView
        hourlyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Check location permissions and get the location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndWeather();
        }
    }

    private void getLocationAndWeather() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String locationQuery = location.getLatitude() + "," + location.getLongitude();
                            getWeather(locationQuery);
                            getAddressFromLocation(location);
                        } else {
                            weatherResult.setText("Unable to retrieve location. Please enter a city name.");
                        }
                    }
                });
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
                        locationResult.setText(addressText);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationResult.setText("Unable to determine current location.");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    locationResult.setText("Unable to determine current location.");
                }
            });
        }
    }

    private void getWeather(String query) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + "&q=" + query + "&hours=24";
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
                        weatherResult.setText("Failed to get data. Please check your internet connection and try again.");
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
                            weatherResult.setText("Failed to get data. HTTP code: " + response.code());
                        }
                    });
                    return;
                }

                String responseData = response.body().string();
                Log.d("WeatherApp", "Response Data: " + responseData);

                JsonObject json = JsonParser.parseString(responseData).getAsJsonObject();
                JsonObject current = json.getAsJsonObject("current");
                JsonObject forecast = json.getAsJsonObject("forecast");
                JsonObject forecastDay = forecast.getAsJsonArray("forecastday").get(0).getAsJsonObject();
                final JsonArray hourly = forecastDay.getAsJsonArray("hour");

                final String tempC = current.get("temp_c").getAsString();
                final String condition = current.getAsJsonObject("condition").get("text").getAsString();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherResult.setText("Temperature: " + tempC + "°C\nCondition: " + condition);
                        displayHourlyForecast(hourly);
                    }
                });
            }
        });
    }

    private void displayHourlyForecast(JsonArray hourly) {
        hourlyForecastAdapter = new HourlyForecastAdapter(hourly);
        hourlyForecastRecyclerView.setAdapter(hourlyForecastAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndWeather();
            } else {
                weatherResult.setText("Location permission denied. Please enter a city name.");
            }
        }
    }
}