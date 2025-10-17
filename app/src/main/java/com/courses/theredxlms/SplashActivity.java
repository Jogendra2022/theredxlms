package com.courses.theredxlms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // This finds the Action Bar and tells it to hide the logo/icon.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
        }
        // Make sure you have a layout file named activity_splash.xml

        // Use a Handler to delay the start of the next activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start OnboardingActivity
            Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            startActivity(intent);

            // Close the splash activity so the user can't go back to it
            finish();
        }, SPLASH_DELAY);
    }
}
