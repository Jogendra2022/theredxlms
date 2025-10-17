package com.courses.theredxlms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseApprovalAdapter extends RecyclerView.Adapter<CourseApprovalAdapter.ViewHolder> {

    private final List<Course> pendingCourses;
    private final OnApproveClickListener listener;

    public interface OnApproveClickListener {
        void onApproveClick(Course course);
    }

    public CourseApprovalAdapter(List<Course> pendingCourses, OnApproveClickListener listener) {
        this.pendingCourses = pendingCourses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = pendingCourses.get(position);
        holder.bind(course, listener);
    }

    @Override
    public int getItemCount() {
        return pendingCourses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle;
        Button approveButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.tv_course_title_approval);
            approveButton = itemView.findViewById(R.id.btn_approve_course);
        }

        void bind(final Course course, final OnApproveClickListener listener) {
            courseTitle.setText(course.getTitle());
            approveButton.setOnClickListener(v -> listener.onApproveClick(course));
        }
    }
}
