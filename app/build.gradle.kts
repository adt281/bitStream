plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.musicstreamproject2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.musicstreamproject2"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    //TODO: WRITE THIS HERE TO MAKE BINDING POSSIBLE 
    buildFeatures {
        viewBinding = true
    }

}
dependencies {
    // Existing dependencies
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.7.1")
    implementation("androidx.media3:media3-ui:1.7.1")
    implementation("androidx.media3:media3-ui-compose:1.7.1")

    // 🔸 ADD THESE FOR MUSIC SERVICE:
    // Media3 Session for background playback and media controls
    implementation("androidx.media3:media3-session:1.7.1")

    // Media3 Common utilities
    implementation("androidx.media3:media3-common:1.7.1")

    // Media Compat for MediaSession support (legacy support)
    implementation("androidx.media:media:1.7.0")


    // 🔸 OPTIONAL BUT RECOMMENDED:
    // Work Manager for background tasks
    implementation("androidx.work:work-runtime:2.9.0")

    // Room Database for offline music cache/playlist storage
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Lifecycle components for better activity/service management
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")

    // Networking (if streaming from URLs)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // RecyclerView (for playlists/song lists)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Fragment support
    implementation("androidx.fragment:fragment:1.6.2")

    // 🔸 PERMISSIONS HELPER (for runtime permissions)
    implementation("com.karumi:dexter:6.2.3")

    // Existing dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}