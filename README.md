# TravelMate

TravelMate is an Android app for exploring landmarks. Take a photo or pick one from your gallery, and the app tries to identify the place and tell you a story about it. You can read the story, listen to it, and find it again in your history.

The server lives in [Tour-Guide-App-Backend](https://github.com/Ivan-here/Tour-Guide-App-Backend). You'll need it running to recognize photos and generate stories. Saved story text stays on the phone.

## Getting started

You'll need Android Studio, JDK 17 or newer, Android SDK 36, and a device or emulator running Android 7.0 (API 24) or newer.

1. Clone this repo and the [backend](https://github.com/Ivan-here/Tour-Guide-App-Backend).
2. Follow the backend README to start the server on port 8000.
3. Open this project in Android Studio and let Gradle sync.
4. Run `app` on an emulator. Allow camera access to take a photo, or use the gallery button.

The debug build connects to `http://10.0.2.2:8000` by default. That's how the Android emulator reaches the server on your computer. You can check the server from your computer at `http://localhost:8000/health`.

For a physical phone, connect it to the same network as your computer and build with your computer's LAN address:

```powershell
.\gradlew.bat assembleDebug -PBACKEND_BASE_URL=http://192.168.1.50:8000
```

Replace that address with your own. The server needs to listen on `0.0.0.0`, and your firewall needs to allow port 8000. You can also put `BACKEND_BASE_URL` in your user-level Gradle properties. Rebuild whenever you change it. Debug builds allow HTTP for local testing; release builds need an HTTPS server.

The OpenAI key goes in the backend's environment, never in this app.

## Building and testing

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

On macOS or Linux, use `./gradlew` instead. Set up the SDK through Android Studio, `ANDROID_HOME`, or an untracked `local.properties` file. The debug APK ends up in `app/build/outputs/apk/debug/app-debug.apk`.

The Android unit test is still the generated sample, so try the actual flow on a device too: take a photo, pick a gallery image, generate a story, play and stop narration, and open a saved story. Also try a photo with no recognizable landmark and see what happens when the server is offline. The play button waits until both the story and the speech engine are ready.

## Where things are

The Kotlin code is under `app/src/main/java/com/example/tourguideapp/`, with XML layouts in `app/src/main/res/layout/`.

- `MainActivity.kt` handles the camera and gallery picker. Gallery photos are resized before upload.
- `Backend.kt` sends recognition and story requests.
- `ReloadActivity.kt` waits for recognition and handles unknown landmarks.
- `ResultActivity.kt` displays, narrates, and saves stories.
- `StoryDatabaseHelper.kt` stores history in SQLite.
- `SettingsActivity.kt`, `Prefs.kt`, and `ThemeManager.kt` handle appearance and accessibility settings.

Settings include dark mode, a colorblind theme, an accessible font, and text size.

## Still to do

Story requests currently use `folklore`, `casual`, and `medium` rather than letting you choose. Measurement and download settings are placeholders. History saves the story text, but its photos are kept in cache and may disappear if Android clears it.

Photos go to the backend and OpenAI for recognition. The backend gets information from Wikipedia and asks OpenAI to write the story, so identifications and story details can be wrong. The backend is set up for local development and doesn't have authentication or rate limiting yet.
