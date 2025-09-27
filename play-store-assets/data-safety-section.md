# Google Play Data Safety Section

## Data Safety Declaration for Weather Pro

### Data Collection Summary
**Does your app collect or share any of the required user data types?**
- ✅ YES - Location data is collected

### Location Data Details

**Is this data collected or shared?**
- ✅ Collected
- ❌ Shared with third parties

**Is this data processed ephemerally?**
- ✅ YES - Location data is used immediately for weather requests and not stored

**Is data collection required or optional?**
- ❌ Optional - Users can manually enter city names instead

**Why is this user data collected?**
- ✅ App functionality - To provide location-based weather information

### Data Security Practices

**Is data encrypted in transit?**
- ✅ YES - All network communications use HTTPS

**Can users request data deletion?**
- ✅ YES - No persistent data is stored, cache is automatically cleared

**Are you following the Families Policy requirements?**
- ✅ YES - App is appropriate for all ages

**Have you completed a security assessment?**
- ✅ YES - Standard security practices implemented

### Detailed Responses for Play Console

#### Location Section:
1. **Approximate location** ✅
   - Collected: Yes
   - Shared: No
   - Required: No
   - Purpose: App functionality

2. **Precise location** ✅
   - Collected: Yes
   - Shared: No
   - Required: No
   - Purpose: App functionality

#### Data Usage and Handling:
- **Data is encrypted in transit**: Yes
- **Users can request data deletion**: Yes (no data stored persistently)
- **Data is required for the app to function**: No (optional)
- **Data retention policy**: Not applicable (ephemeral processing)

#### Third-party Data Sharing:
- **WeatherAPI.com**: Location coordinates shared to retrieve weather data
  - Data type: Location (precise)
  - Purpose: App functionality
  - User choice: Required for location-based weather

### Additional Information for Reviewers

**Why does your app need location access?**
"Weather Pro uses location data to provide current weather information for the user's area. This enables automatic weather updates without requiring manual city input. Location data is processed ephemerally - it's used immediately to fetch weather data and is not stored persistently on the device or our servers."

**How do you protect user data?**
"All network communications use HTTPS encryption. Location data is only used for immediate weather API requests and is not stored permanently. The app includes offline caching of weather data (not location data) to improve performance and reduce network usage."

**Third-party data sharing details:**
"Location coordinates are shared with WeatherAPI.com solely to retrieve weather information. No other personal data is collected or shared. Users can opt out of location sharing by manually entering city names instead."