package com.courses.theredxlms;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseApprovalActivity extends AppCompatActivity implements CourseApprovalAdapter.OnApproveClickListener {

    private static final String TAG = "CourseApprovalActivity";
    private RecyclerView recyclerView;
    private CourseApprovalAdapter adapter;
    private List<Course> pendingCoursesList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assuming your layout file is named activity_course_approval.xml
        // and contains a RecyclerView with the id recycler_view_approval
        setContentView(R.layout.activity_course_approval);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_approval); // Make sure this ID exists in your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingCoursesList = new ArrayList<>();
        adapter = new CourseApprovalAdapter(pendingCoursesList, this);
        recyclerView.setAdapter(adapter);

        fetchPendingCourses();
    }

    private void fetchPendingCourses() {
        db.collection("courses")
                .whereEqualTo("approved", false) // The key query to get unapproved courses
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pendingCoursesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Course course = document.toObject(Course.class);
                            course.setId(document.getId()); // Store the document ID
                            pendingCoursesList.add(course);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching pending courses: ", task.getException());
                        Toast.makeText(this, "Failed to fetch courses for approval.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onApproveClick(Course course) {
        if (course.getId() == null) {
            Toast.makeText(this, "Cannot approve course: Missing ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the 'approved' field to true in Firestore
        db.collection("courses").document(course.getId())
                .update("approved", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, course.getTitle() + " has been approved.", Toast.LENGTH_SHORT).show();
                    // Re-fetch the list to remove the approved item
                    fetchPendingCourses();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to approve course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating document", e);
                });
    }
}
