plugins {
    alias(libs.plugins.android.application)

    // You should apply the google-services plugin here, not in settings.gradle.kts
    id("com.google.gms.google-services")
}

android {
    namespace = "com.courses.theredxlms"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.courses.theredxlms"
        minSdk = 24
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout) // Assuming this is defined in your libs.versions.toml

    // CORRECT WAY TO ADD FIREBASE BOM AND DEPENDENCIES
    // Use the BoM to manage versions for all Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    // Add dependencies for Firebase products you want to use
    // NOTICE: No versions are specified here; the BoM handles it.
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    // Glide for Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // annotationProcessor("com.github.bumptech.glide:compiler:4.12.0") // 'kapt' is needed for Kotlin

    // ======================= THE FIX =======================
    // Add these two lines for the AndroidX Navigation Component
    val nav_version = "2.7.7" // Use the latest stable version
    implementation("androidx.navigation:navigation-fragment:${nav_version}")
    implementation("androidx.navigation:navigation-ui:${nav_version}")

    // =============================================================





    // Default testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

