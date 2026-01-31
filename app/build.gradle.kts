// ============================================================
// Daily Soul Journal - App Module Build Script
// ============================================================
// Configures the Android app module: SDK versions, dependencies,
// compile options, and build variants.
// ============================================================

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.dailysoul.journal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dailysoul.journal"
        minSdk = 29                          // Android 10+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.4"
        testInstrumentationRunner = "androidx.test.ext.junit.runners.AndroidJUnit4"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Enable ViewBinding for type-safe XML layout access
    viewBinding {
        enable = true
    }
}

dependencies {
    // AndroidX Core & Lifecycle (MVVM ViewModel, LiveData)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-owner-ktx:2.7.0")

    // AppCompat for backward-compatible UI components
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design components (Buttons, CardView, etc.)
    implementation("com.google.android.material:material:1.11.0")

    // RecyclerView for scrollable journal history list
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Room ORM for offline SQLite persistence
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Gson for JSON serialization (export feature)
    implementation("com.google.code.gson:gson:2.10.1")

    // Unit tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}
