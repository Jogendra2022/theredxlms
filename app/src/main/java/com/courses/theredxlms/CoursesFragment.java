package com.courses.theredxlms;

import android.os.Bundle;
import android.util.Log;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query; // Import Query

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment implements CoursesAdapter.OnCourseClickListener {

    private static final String TAG = "CoursesFragment";
    private RecyclerView recyclerView;
    private CoursesAdapter adapter;
    private List<Course> courseList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_courses);
        db = FirebaseFirestore.getInstance();
        courseList = new ArrayList<>();

        // The listener 'this' refers to the CoursesFragment itself, because we implement the interface
        adapter = new CoursesAdapter(courseList, this);
        recyclerView.setAdapter(adapter);

        fetchCourses();

        return view;
    }

    private void fetchCourses() {
        db.collection("courses")
                .whereEqualTo("approved", true) // Only show approved courses
                .orderBy("title", Query.Direction.ASCENDING) // Order by title
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        courseList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Course course = document.toObject(Course.class);
                            course.setId(document.getId());
                            courseList.add(course);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching courses: ", task.getException());
                    }
                });
    }

    /**
     * This method is called when a course item is clicked.
     * It's part of the OnCourseClickListener interface.
     */
    @Override
    public void onCourseClick(Course course) {
        // For now, just show a Toast.
        // LATER, we will navigate to the Course Detail page from here.
        Toast.makeText(getContext(), "Clicked on: " + course.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
