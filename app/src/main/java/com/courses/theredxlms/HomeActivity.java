package com.courses.theredxlms;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Setup for Top Bar and Profile Icon ---
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> openProfileFragment());

        // --- Setup for Bottom Navigation ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // --- Load the default fragment ---
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * This listener handles clicks on the Bottom Navigation Bar items.
     */
    /**
     * This listener handles clicks on the Bottom Navigation Bar items.
     */
    private final NavigationBarView.OnItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();
                Fragment selectedFragment = null; // Declare fragment variable

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_courses) {
                    selectedFragment = new CoursesFragment();

                    // ======================= THE FINAL, GUARANTEED FIX =======================
                    // ADD THIS BLOCK TO HANDLE THE DISCUSSION BUTTON CLICK
                } else if (itemId == R.id.nav_discussion) {
                    selectedFragment = new DiscussionFragment();
                    // =========================================================================

                } else if (itemId == R.id.nav_quiz) {
                    startActivity(new Intent(HomeActivity.this, QuizActivity.class));
                    return false; // Don't select the item, we are leaving the activity
                }
                // You can add more 'else if' blocks here for other buttons like nav_my_courses

                // If a fragment was selected, replace the container with it
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true; // A fragment was loaded, so the item is selected
                }

                return false;
            };


    /**
     * This method opens the Profile screen.
     */
    private void openProfileFragment() {
        Fragment profileFragment = new ProfileFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
