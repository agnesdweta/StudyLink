package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<Course> courses;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.txtCourseName.setText(course.getName());
        holder.txtSks.setText(course.getSks());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtCourseName, txtSks;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCourseName = itemView.findViewById(R.id.txtCourseName);
            txtSks = itemView.findViewById(R.id.txtSks);
        }
    }
}
