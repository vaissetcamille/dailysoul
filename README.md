# 📓 Daily Soul Journal – Android App

A calm, introspective daily journaling app built with native Android (Kotlin).
Write, reflect, and grow — one entry at a time, entirely offline.

---

## 🏗️ Project Structure

```
DailySoulJournal/
├── app/
│   ├── src/main/
│   │   ├── java/com/dailysoul/journal/
│   │   │   ├── DailySoulApp.kt              ← Application entry point
│   │   │   ├── data/
│   │   │   │   ├── model/
│   │   │   │   │   ├── JournalEntry.kt      ← Room @Entity (DB row)
│   │   │   │   │   └── Mood.kt              ← Mood enum (5 emotions)
│   │   │   │   ├── db/
│   │   │   │   │   ├── JournalDao.kt        ← Room DAO (SQL queries)
│   │   │   │   │   └── JournalDatabase.kt   ← Room Database singleton
│   │   │   │   └── repository/
│   │   │   │       └── JournalRepository.kt ← Data layer bridge
│   │   │   ├── ui/
│   │   │   │   ├── activity/
│   │   │   │   │   ├── MainActivity.kt      ← Home / Today screen
│   │   │   │   │   ├── MoodActivity.kt      ← Mood selection
│   │   │   │   │   ├── HistoryActivity.kt   ← Journal history list
│   │   │   │   │   ├── ReadActivity.kt      ← Single entry reader
│   │   │   │   │   └── SettingsActivity.kt  ← App settings
│   │   │   │   ├── adapter/
│   │   │   │   │   └── HistoryAdapter.kt    ← RecyclerView adapter
│   │   │   │   └── viewmodel/
│   │   │   │       ├── MainViewModel.kt     ← Home screen state
│   │   │   │       ├── MoodViewModel.kt     ← Mood selection state
│   │   │   │       ├── HistoryViewModel.kt  ← History list state
│   │   │   │       ├── ReadViewModel.kt     ← Reader state
│   │   │   │       └── SettingsViewModel.kt ← Settings state
│   │   │   └── util/
│   │   │       ├── AppPrefs.kt              ← SharedPreferences wrapper
│   │   │       ├── DateUtils.kt             ← Date formatting helpers
│   │   │       ├── DailyPrompts.kt          ← 30 curated prompts
│   │   │       ├── ExportUtils.kt           ← Plain-text export
│   │   │       ├── ReminderManager.kt       ← Alarm scheduling
│   │   │       ├── AlarmReceiver.kt         ← Notification posting
│   │   │       └── BootReceiver.kt          ← Re-arms alarm on reboot
│   │   └── res/
│   │       ├── layout/                      ← XML layouts (5 activities + 1 item)
│   │       ├── drawable/                    ← Buttons, icons, shapes
│   │       ├── values/                      ← Strings, colors, themes
│   │       └── values-night/                ← Dark mode color overrides
│   └── build.gradle.kts                     ← App module dependencies
├── build.gradle.kts                         ← Root build script
├── settings.gradle.kts                      ← Project settings
└── README.md                                ← This file
```

---

## 🏛️ Architecture

**MVVM (Model – View – ViewModel)**

| Layer        | Role                                                                 |
|--------------|----------------------------------------------------------------------|
| **Model**    | `JournalEntry`, `Mood` – data classes + Room entities                |
| **View**     | Activities + XML layouts – observe LiveData, update UI               |
| **ViewModel**| Holds UI state (LiveData), calls Repository, survives config changes|
| **Repository**| Single source of truth – bridges DAO ↔ ViewModel                   |

---

## 📲 Offline Support

- **Room ORM** persists every entry to a local SQLite database.
- Auto-save fires every **2 seconds** of inactivity while typing, plus a forced save on `onPause()`.
- No network calls anywhere – the app is 100% functional offline.
- Google Play backup is enabled (database + SharedPreferences) so data survives reinstalls on the same account.

---

## 🔔 Daily Reminder

- Uses `AlarmManager` with `RTC_WAKEUP` for a repeating daily alarm.
- `BootReceiver` re-arms the alarm after device reboot.
- Default time: **8:00 PM**. Configurable via `AppPrefs`.
- Requires `POST_NOTIFICATIONS` permission on Android 13+.

---

## 🎨 Theming

- **Light theme** (default): warm cream palette with terracotta accents.
- **Dark theme**: deep warm browns. Applied programmatically via `setTheme()` + `recreate()`.
- Colors centralized in `res/values/colors.xml` and `res/values-night/colors.xml`.

---

## 📦 Publishing to Google Play Store

1. **Generate a signing key** (if you don't have one):
   ```
   keytool -genkey -v -keystore dailysoul-release.jks -alias dailysoul -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configure signing** in `app/build.gradle.kts`:
   ```kotlin
   android {
       signingConfigs {
           release {
               storeFile file("../../dailysoul-release.jks")
               storePassword "your_store_password"
               keyAlias "dailysoul"
               keyPassword "your_key_password"
           }
       }
       buildTypes {
           release { signingConfig signingConfigs.release }
       }
   }
   ```

3. **Build AAB**: `./gradlew :app:bundleRelease`
   Output: `app/build/outputs/bundle/release/app-release.aab`

4. **Upload** to [Google Play Console](https://play.google.com/console/) → Create a new app → Upload the `.aab`.

5. **Minimum requirements checklist**:
   - ✅ App icon (launcher icon in `drawable/`)
   - ✅ ProGuard rules (`proguard-rules.pro`)
   - ✅ Minimal permissions (only what's needed)
   - ✅ Target SDK 34
   - ✅ Min SDK 29 (Android 10+)

---

## ⚙️ Requirements

- Android Studio **Flamingo** or later
- Kotlin **1.9+**
- Android SDK **34**
- Gradle **8.2+**

---

## 📝 License

This project is provided as-is for personal use and learning.
