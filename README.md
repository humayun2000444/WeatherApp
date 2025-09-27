# Weather Pro 🌤️

A comprehensive Android weather application that provides real-time weather information, forecasts, and weather alerts with a beautiful, intuitive interface.

## Features

### Core Weather Information
- **Current Weather**: Real-time temperature, weather conditions, and "feels like" temperature
- **Location Services**: Automatic location detection or manual city search
- **Detailed Weather Metrics**:
  - UV Index
  - Humidity levels
  - Wind speed and direction
  - Atmospheric pressure
  - Visibility
  - Sunrise and sunset times

### Forecasting
- **24-Hour Forecast**: Hourly weather predictions for the next 24 hours
- **10-Day Forecast**: Extended daily weather forecasts
- **Weather Alerts**: Real-time weather warnings and advisories

### User Experience
- **Pull-to-Refresh**: Easy data refreshing with swipe gesture
- **Smart Caching**: Offline data availability with intelligent caching system
- **Material Design**: Modern, clean UI following Material Design principles
- **Location Search**: Search for weather by city name or use current location

## Technical Stack

### Platform & Tools
- **Language**: Java
- **Platform**: Android (API 24+)
- **Build System**: Gradle with Kotlin DSL
- **Target SDK**: 34
- **Minimum SDK**: 24 (Android 7.0)

### Dependencies
- **UI Components**:
  - Material Design Components (1.12.0)
  - RecyclerView (1.3.2)
  - CardView (1.0.0)
  - SwipeRefreshLayout (1.1.0)
  - ConstraintLayout (2.1.4)

- **Networking & Data**:
  - OkHttp (4.9.3) for API calls
  - Gson (2.10.1) for JSON parsing
  - WeatherAPI for weather data

- **Location Services**:
  - Google Play Services Location (21.3.0)
  - Android Location APIs

### Architecture
- **Pattern**: Single Activity with multiple adapters
- **Caching**: Custom WeatherCache utility for offline support
- **Permissions**: Runtime location permission handling
- **Network Security**: Custom network security configuration

## Installation

### Prerequisites
- Android Studio 4.0 or higher
- Android SDK with API level 24 or higher
- Valid WeatherAPI key

### Setup
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd WeatherApp
   ```

2. Open the project in Android Studio

3. Get your API key from [WeatherAPI](https://www.weatherapi.com/)

4. Add your API key in `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "WEATHER_API_KEY", "\"YOUR_API_KEY_HERE\"")
   ```

5. Build and run the application

## Permissions

The app requires the following permissions:
- `INTERNET` - For fetching weather data
- `ACCESS_FINE_LOCATION` - For precise location detection
- `ACCESS_COARSE_LOCATION` - For approximate location detection
- `ACCESS_NETWORK_STATE` - For network connectivity checks
- `WAKE_LOCK` - For background data synchronization

## API Integration

This app uses the [WeatherAPI](https://www.weatherapi.com/) service which provides:
- Current weather conditions
- Weather forecasts up to 10 days
- Weather alerts and warnings
- Air quality information
- Astronomy data (sunrise/sunset)

## Project Structure

```
app/src/main/
├── java/com/example/weatherapp/
│   ├── MainActivity.java           # Main application activity
│   ├── WeatherApplication.java     # Application class
│   ├── adapter/
│   │   ├── HourlyForecastAdapter.java    # Hourly forecast RecyclerView adapter
│   │   ├── DailyForecastAdapter.java     # Daily forecast RecyclerView adapter
│   │   └── WeatherAlertsAdapter.java     # Weather alerts RecyclerView adapter
│   └── utils/
│       └── WeatherCache.java       # Weather data caching utility
├── res/
│   ├── layout/                     # XML layout files
│   ├── drawable/                   # Vector icons and backgrounds
│   ├── values/                     # Colors, strings, and styles
│   └── xml/                        # Network security config
└── AndroidManifest.xml             # App permissions and configuration
```

## Key Features Implementation

### Smart Caching System
- Automatic caching of weather data to reduce API calls
- Offline data availability for recently searched locations
- Cache invalidation based on data freshness

### Location Services
- Automatic location detection using GPS
- Fallback to network-based location
- Manual city search with geocoding support

### Responsive UI
- Adaptive layouts for different screen sizes
- Smooth animations and transitions
- Error states with helpful messages
- Loading indicators for better UX

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## License

This project is open source and available under the [MIT License](LICENSE).

## Acknowledgments

- Weather data provided by [WeatherAPI](https://www.weatherapi.com/)
- Icons and design inspired by Material Design principles
- Location services powered by Google Play Services

---

**Weather Pro** - Your reliable companion for weather information 🌦️