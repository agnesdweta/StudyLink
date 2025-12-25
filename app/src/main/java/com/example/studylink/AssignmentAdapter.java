package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    public interface OnActionListener {
        void onEdit(AssignmentEntity assignment, int position);
        void onDelete(AssignmentEntity assignment, int position);
    }

    private List<AssignmentEntity> assignments;
    private OnActionListener listener;

    public AssignmentAdapter(List<AssignmentEntity> assignments, OnActionListener listener) {
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
        AssignmentEntity assignment = assignments.get(position);

        holder.txtTitle.setText(assignment.getTitle());
        holder.txtCourse.setText(assignment.getCourse());
        holder.txtDeadline.setText("Deadline: " + assignment.getDeadline());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null)
                listener.onEdit(assignment, holder.getAdapterPosition());
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null)
                listener.onDelete(assignment,holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCourse, txtDeadline;
        Button btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtAssignmentTitle);
            txtCourse = itemView.findViewById(R.id.txtAssignmentCourse);
            txtDeadline = itemView.findViewById(R.id.txtAssignmentDeadline);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
