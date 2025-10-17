package com.courses.theredxlms;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PowerOfAiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_of_ai);

        // Add a title and back button to the top bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Powers of AI");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Handles the action bar's back button press
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Closes this screen and goes back
        return true;
    }
}
