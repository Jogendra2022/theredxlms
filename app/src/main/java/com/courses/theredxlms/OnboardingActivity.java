package com.courses.theredxlms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;
// ... (imports)
import com.google.firebase.auth.FirebaseAuth; // <-- ADD THIS IMPORT
import com.google.firebase.auth.FirebaseUser; // <-- ADD THIS IMPORT

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private ViewPager2 viewPager;
    private MaterialButton nextButton;
    private TabLayout tabLayout;
    private FirebaseAuth mAuth; // <-- ADD THIS LINE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mAuth = FirebaseAuth.getInstance(); // <-- ADD THIS LINE to initialize Firebase Auth

        // ... (the rest of your findViewById calls)
// ...


        // Find the views by their IDs from the XML layout
        viewPager = findViewById(R.id.viewPager);
        nextButton = findViewById(R.id.nextButton);
        tabLayout = findViewById(R.id.tabLayout);

        // This method creates the slides and sets up the adapter
        setupOnboardingItems();

        // This is the crucial line that gives the slides to the ViewPager
        viewPager.setAdapter(onboardingAdapter);

        // This connects the dots (TabLayout) to the ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // This callback is necessary for the dots to appear and update
        }).attach();

        // Set up the "Next" button logic
        // Set up the "Next" button logic
        nextButton.setOnClickListener(v -> {
            // If it's not the last slide, go to the next one
            if (viewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                // This is the last slide, so check if the user is logged in
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is already logged in, go to HomeActivity
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    // User is not logged in, go to LoginActivity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                finish(); // Close the onboarding screen so the user can't go back to it
            }
        });


        // This listener updates the button text on the last slide
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == onboardingAdapter.getItemCount() - 1) {
                    nextButton.setText("Get Started");
                } else {
                    nextButton.setText("Next");
                }
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        // Slide 1
        OnboardingItem item1 = new OnboardingItem();
        item1.setTitle("Interactive Learning Anytime");
        // IMPORTANT: Replace with your actual image from the res/drawable folder
        item1.setImage(R.drawable.slide_1_learning);

        // Slide 2
        OnboardingItem item2 = new OnboardingItem();
        item2.setTitle("AI-Powered Courses");
        item2.setImage(R.drawable.slide_2_ai); // Replace with your image

        // Slide 3
        OnboardingItem item3 = new OnboardingItem();
        item3.setTitle("Earn Certificates & Build Career");
        item3.setImage(R.drawable.slide_3_certificate); // Replace with your image

        onboardingItems.add(item1);
        onboardingItems.add(item2);
        onboardingItems.add(item3);

        // Create the adapter with the list of items
        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }
}
