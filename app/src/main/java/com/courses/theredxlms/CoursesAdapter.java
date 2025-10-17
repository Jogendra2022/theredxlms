package com.courses.theredxlms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // You'll need to add this dependency
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {

    private final List<Course> courseList;
    private final OnCourseClickListener listener;

    // Interface for click events
    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CoursesAdapter(List<Course> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_vertical, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course, listener);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    // ViewHolder class
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, instructor, rating, ratingCount, price;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.iv_course_thumbnail);
            title = itemView.findViewById(R.id.tv_course_title);
            instructor = itemView.findViewById(R.id.tv_instructor_name);
            rating = itemView.findViewById(R.id.tv_course_rating);
            ratingCount = itemView.findViewById(R.id.tv_rating_count);
            price = itemView.findViewById(R.id.tv_course_price);
        }

        public void bind(final Course course, final OnCourseClickListener listener) {
            // Set text fields
            title.setText(course.getTitle());
            instructor.setText(course.getInstructorName());
            price.setText("₹" + String.format("%.2f", course.getPrice())); // Format price
            rating.setText(String.valueOf(course.getRating()));
            ratingCount.setText("(" + course.getRatingCount() + ")");

            // Use Glide to load the image from a URL
            Glide.with(itemView.getContext())
                    .load(course.getThumbnailUrl()) // <-- FIX: Changed from getVideoUrl() to getThumbnailUrl()
                    .placeholder(R.drawable.ic_launcher_background) // A default image while loading
                    .error(R.drawable.ic_launcher_background)      // A default image if loading fails
                    .into(thumbnail);

            // Set the click listener for the entire item
            itemView.setOnClickListener(v -> listener.onCourseClick(course));
        }

    }
}
