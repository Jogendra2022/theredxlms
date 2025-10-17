package com.courses.theredxlms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast; // Import Toast for placeholder actions
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton; // Correct import

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // ======================= THE FINAL FIX =======================
        // This code correctly finds the MaterialButtons from your XML layout
        // and assigns the click listeners. This will make the buttons work.

        MaterialButton btnUserManagement = findViewById(R.id.btn_user_management);
        MaterialButton btnCourseApproval = findViewById(R.id.btn_course_approval);
        MaterialButton btnRevenueSummary = findViewById(R.id.btn_revenue_summary);
        MaterialButton btnPushNotifications = findViewById(R.id.btn_push_notifications);
        MaterialButton btnInstructorAnalytics = findViewById(R.id.btn_instructor_analytics);
        MaterialButton btnAddNewCourse = findViewById(R.id.btn_add_new_course);

        // --- Set Click Listeners for EACH button ---

        btnUserManagement.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, UserManagementActivity.class));
        });

        btnCourseApproval.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, CourseApprovalActivity.class));
        });

        btnRevenueSummary.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, RevenueSummaryActivity.class));
        });

        btnPushNotifications.setOnClickListener(v -> {
            // This will now launch the PushNotificationsActivity
            startActivity(new Intent(AdminDashboardActivity.this, PushNotificationsActivity.class));
        });

        btnInstructorAnalytics.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, InstructorAnalyticsActivity.class));
        });

        btnAddNewCourse.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AddCourseActivity.class));
        });

        // =============================================================
    }
}
