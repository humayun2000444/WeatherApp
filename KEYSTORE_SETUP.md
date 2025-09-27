# Keystore Setup for Play Store Release

## Creating a Release Keystore

### Step 1: Generate Keystore using Android Studio
1. Open Android Studio
2. Go to **Build** → **Generate Signed Bundle / APK**
3. Select **Android App Bundle** (recommended) or **APK**
4. Click **Create new...**
5. Fill in the following information:

```
Key store path: D:\WeatherApp\keystore\weather-pro-release.jks
Password: [Choose a strong password]
Key alias: weather-pro-key
Key password: [Choose a strong password - can be same as keystore]
Validity (years): 25
Certificate:
  First and Last Name: [Your Name]
  Organizational Unit: [Your Organization]
  Organization: [Your Organization]
  City or Locality: [Your City]
  State or Province: [Your State]
  Country Code (XX): [Your Country Code]
```

### Step 2: Alternative - Command Line Generation
```bash
# Navigate to your project directory
cd D:\WeatherApp

# Create keystore directory
mkdir keystore

# Generate keystore (replace values with your information)
keytool -genkey -v -keystore keystore/weather-pro-release.jks -alias weather-pro-key -keyalg RSA -keysize 2048 -validity 9125
```

### Step 3: Update build.gradle.kts
After creating your keystore, update the commented lines in `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../keystore/weather-pro-release.jks")
        storePassword = "your_store_password"
        keyAlias = "weather-pro-key"
        keyPassword = "your_key_password"
    }
}

buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        signingConfig = signingConfigs.getByName("release")
    }
}
```

### Step 4: Secure Storage of Credentials
For security, create a `keystore.properties` file in the project root:

```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=weather-pro-key
storeFile=keystore/weather-pro-release.jks
```

Then update build.gradle.kts to use this file:

```kotlin
// Load keystore properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
}
```

### Step 5: Build Release APK/Bundle
```bash
# For Android App Bundle (recommended for Play Store)
./gradlew bundleRelease

# For APK
./gradlew assembleRelease
```

### Step 6: Locate Your Release Files
- **App Bundle**: `app/build/outputs/bundle/release/app-release.aab`
- **APK**: `app/build/outputs/apk/release/app-release.apk`

## Important Security Notes

1. **Keep your keystore file safe** - You'll need it for all future updates
2. **Backup your keystore** - Store it in multiple secure locations
3. **Never commit keystore.properties to version control** - Add it to `.gitignore`
4. **Use strong passwords** - Minimum 8 characters with mixed case, numbers, symbols
5. **Record your credentials securely** - Store passwords in a password manager

## File Structure After Setup
```
WeatherApp/
├── keystore/
│   └── weather-pro-release.jks
├── keystore.properties (add to .gitignore)
├── app/
│   └── build.gradle.kts (updated with signing config)
└── .gitignore (should include keystore.properties)
```

## Next Steps
1. Create your keystore using the steps above
2. Update the build.gradle.kts file with your keystore information
3. Build your release bundle/APK
4. Test the release build on a device
5. Upload to Google Play Console