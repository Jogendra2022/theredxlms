package com.courses.theredxlms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate; // ADD THIS IMPORT
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch; // ADD THIS IMPORT
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // --- Find all the views from the layout ---
        MaterialToolbar toolbar = view.findViewById(R.id.profile_toolbar);

        // Main category buttons
        MaterialButton btnMyCourses = view.findViewById(R.id.btn_my_courses);
        MaterialButton btnCertificates = view.findViewById(R.id.btn_certificates);
        MaterialButton btnSettings = view.findViewById(R.id.btn_settings);
        MaterialButton btnReferEarn = view.findViewById(R.id.btn_refer_earn);
        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);

        // Hidden option containers
        LinearLayout myCoursesOptions = view.findViewById(R.id.my_courses_options);
        LinearLayout certificatesOptions = view.findViewById(R.id.certificates_options);
        LinearLayout settingsOptions = view.findViewById(R.id.settings_options);

        // Sub-options
        TextView btnOngoing = view.findViewById(R.id.btn_ongoing);
        TextView btnCompleted = view.findViewById(R.id.btn_completed);

        // *** NEW: FIND SETTINGS SUB-OPTIONS ***
        TextView btnNotifications = view.findViewById(R.id.btn_notifications);
        MaterialSwitch switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        TextView btnViewDownload = view.findViewById(R.id.btn_view_download_certificates);
        btnViewDownload.setOnClickListener(v -> {
            // TODO: Add logic to fetch and display certificates for viewing/downloading
            Toast.makeText(getContext(), "Opening certificates...", Toast.LENGTH_SHORT).show();
        });// <-- NEW


        // --- Set up the click listeners ---

        // Back button on the toolbar
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Togglable buttons
        btnMyCourses.setOnClickListener(v -> toggleVisibility(myCoursesOptions));
        btnCertificates.setOnClickListener(v -> toggleVisibility(certificatesOptions));
        btnSettings.setOnClickListener(v -> toggleVisibility(settingsOptions));

        // Sub-option listeners
        btnOngoing.setOnClickListener(v -> Toast.makeText(getContext(), "Showing Ongoing Courses...", Toast.LENGTH_SHORT).show());
        btnCompleted.setOnClickListener(v -> Toast.makeText(getContext(), "Showing Completed Courses...", Toast.LENGTH_SHORT).show());

        // *** NEW: CLICK LISTENERS FOR SETTINGS OPTIONS ***
        btnNotifications.setOnClickListener(v -> {
            // TODO: Open the device's notification settings for the app
            Toast.makeText(getContext(), "Opening notification settings...", Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Apply Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(getContext(), "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Apply Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(getContext(), "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
            }
        });


        // --- Click Listeners for Refer and Logout ---
        btnReferEarn.setOnClickListener(v -> {
            // TODO: Add logic to share referral code
            Toast.makeText(getContext(), "Share your referral code!", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            // 1. Sign the user out of Firebase
            mAuth.signOut();

            // 2. Show a confirmation message
            Toast.makeText(getContext(), "You have been logged out.", Toast.LENGTH_SHORT).show();

            // 3. Create an intent to go back to the MainActivity (login screen)
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            // 4. Set flags to clear the activity stack. This prevents the user
            //    from pressing the "back" button and returning to the home screen.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // 5. Start the MainActivity
            startActivity(intent);

            // 6. Finish the current activity (HomeActivity)
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        TextView viewDownloadButton = view.findViewById(R.id.btn_view_download_certificates);
        viewDownloadButton.setOnClickListener(v -> {
            // Create an Intent to start the CertificateActivity
            Intent intent = new Intent(getActivity(), CertificateActivity.class);
            startActivity(intent);
        });

    }

    /**
     * A helper method to cleanly show/hide a view.
     * @param view The view to toggle.
     */
    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
