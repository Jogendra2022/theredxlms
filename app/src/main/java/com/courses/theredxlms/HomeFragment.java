package com.courses.theredxlms;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This fragment represents the home screen of the app.
 * Its UI is defined in the layout file: R.layout.fragment_home
 */
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to the ImageView and the layout to hide/show
        ImageView categoryHeaderImage = view.findViewById(R.id.category_header_image);
        LinearLayout hiddenLayout = view.findViewById(R.id.hidden_categories_layout);

        // Attach the robust OnTouchListener DIRECTLY to the ImageView
        categoryHeaderImage.setOnTouchListener(new View.OnTouchListener() {
            private long pressStartTime;
            private float startX, startY;
            private final int MAX_CLICK_DURATION = 200;
            private final int MAX_CLICK_DISTANCE = 15;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // REMOVED THE v.onTouchEvent(event) LINE THAT WAS HERE

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressStartTime = System.currentTimeMillis();
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        float distance = (float) Math.sqrt(Math.pow(event.getX() - startX, 2) + Math.pow(event.getY() - startY, 2));
                        if (pressDuration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE) {
                            // This is a confirmed click. Perform the action.
                            boolean isVisible = hiddenLayout.getVisibility() == View.VISIBLE;
                            hiddenLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                        }
                        break;
                }
                // Return true to indicate we've consumed the touch event and no other view should handle it.
                return true;
            }
        });


        // --- Click listeners for individual categories remain unchanged ---
        view.findViewById(R.id.category_ai).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Artificial Intelligence Clicked", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.category_programming).setOnClickListener(v -> {
            Toast.makeText(getContext(), "All Programmings Clicked", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.category_cybersecurity).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cybersecurity Clicked", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.category_designs).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Designs Clicked", Toast.LENGTH_SHORT).show();
        });
        view.findViewById(R.id.powers_of_ai_banner).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PowerOfAiActivity.class);
            startActivity(intent);
        });
    }
}
