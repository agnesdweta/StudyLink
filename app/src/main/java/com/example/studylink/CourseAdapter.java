package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<CourseEntity> list = new ArrayList<>();
    private OnItemClickListener listener;

    public CourseAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CourseEntity> courseEntities) {
        list.clear();
        if (courseEntities != null) {
            list.addAll(courseEntities);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onEditClick(CourseEntity course);
        void onDeleteClick(CourseEntity course);
        void onMessageClick(CourseEntity course);
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
        CourseEntity c = list.get(position);

        holder.txtName.setText(c.getName());
        holder.txtTime.setText(c.getTime());
        holder.txtDesc.setText(c.getDescription());
        holder.txtInstructor.setText("Dosen: " + c.getInstructor());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(c));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(c));
        holder.btnMessage.setOnClickListener(v -> listener.onMessageClick(c));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtTime, txtDesc, txtInstructor;
        Button btnEdit, btnDelete, btnMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCourseName);
            txtTime = itemView.findViewById(R.id.txtCourseTime);
            txtDesc = itemView.findViewById(R.id.txtCourseDesc);
            txtInstructor = itemView.findViewById(R.id.txtCourseInstructor);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnMessage = itemView.findViewById(R.id.btnMessage);
        }
    }
}
