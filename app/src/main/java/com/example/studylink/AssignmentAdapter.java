package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Assignment assignment);
    }

    private List<Assignment> assignments;
    private OnItemClickListener listener;

    public AssignmentAdapter(List<Assignment> assignments, OnItemClickListener listener) {
        this.assignments = assignments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        holder.txtTitle.setText(assignment.getTitle());
        holder.txtCourse.setText(assignment.getCourse());
        holder.txtDeadline.setText("Deadline: " + assignment.getDeadline());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(assignment);
        });
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCourse, txtDeadline;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtAssignmentTitle);
            txtCourse = itemView.findViewById(R.id.txtAssignmentCourse);
            txtDeadline = itemView.findViewById(R.id.txtAssignmentDeadline);
        }
    }
}
