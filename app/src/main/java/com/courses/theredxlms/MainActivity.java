package com.courses.theredxlms;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // <-- IMPORT THIS
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration; // <-- IMPORT THIS
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet; // <-- IMPORT THIS
import java.util.Set;      // <-- IMPORT THIS

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView profileIcon;
    // NavController is now declared locally in onCreate, so we can remove it from here.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Find all the views
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileIcon = findViewById(R.id.toolbar_profile_icon);
        Toolbar toolbar = findViewById(R.id.home_toolbar); // Find the toolbar

        // 2. Set the Toolbar as the official ActionBar for this Activity
        setSupportActionBar(toolbar);

        // 3. Find the NavHostFragment and get its NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 4. Define all your main screens as top-level destinations
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_courses, R.id.nav_discussion, R.id.nav_my_courses, R.id.nav_quiz
            ).build();

            // ======================= THE FINAL, GUARANTEED FIX =======================
            // THE ORDER OF THESE TWO LINES MATTERS.
            // Connect the ActionBar FIRST, then the BottomNavigationView.

            // 5. THIS LINE MUST BE FIRST: Connect the ActionBar (Toolbar) to the NavController.
            // This updates the title and shows the "Up" button.
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // 6. THIS LINE MUST BE SECOND: Connect the BottomNavigationView to the NavController.
            // This is what makes your buttons navigate.
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            // =========================================================================
        }

        // Load the user's profile image into the toolbar
        loadUserProfileImage();
    }



    /**
     * Fetches the current user's data from Firestore and loads their profile image
     * into the toolbar icon using the Glide library.
     */
    private void loadUserProfileImage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 1. Check if a user is logged in
        if (currentUser == null) {
            Log.e(TAG, "loadUserProfileImage: FAILED. No user is currently logged in.");
            // Set a default icon if no user is logged in
            if (profileIcon != null) {
                profileIcon.setImageResource(R.drawable.ic_profile);
            }
            return;
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "loadUserProfileImage: Attempting to load image for user ID: " + userId);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString("profileImageUrl");

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Log.d(TAG, "loadUserProfileImage: SUCCESS. Found image URL: " + imageUrl);

                    // Use Glide to load the image
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileIcon);

                } else {
                    Log.w(TAG, "loadUserProfileImage: 'profileImageUrl' field is null or empty.");
                    profileIcon.setImageResource(R.drawable.ic_profile);
                }
            } else {
                Log.w(TAG, "loadUserProfileImage: User document does not exist for ID: " + userId);
                profileIcon.setImageResource(R.drawable.ic_profile); // Set default if document not found
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "loadUserProfileImage: FAILED. Error fetching Firestore document.", e);
            profileIcon.setImageResource(R.drawable.ic_profile); // Also set default icon on failure
        });
    }
}
