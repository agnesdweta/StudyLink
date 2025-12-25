package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    public interface OnActionListener {
        void onEdit(ExamEntity exam, int position);
        void onDelete(ExamEntity exam, int position);
    }

    private List<ExamEntity> exams;
    private OnActionListener listener;

    public ExamAdapter(List<ExamEntity> exams, OnActionListener listener) {
        this.exams = exams;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamEntity exam = exams.get(position);

        holder.txtTitle.setText(exam.getExamType());
        holder.txtCourse.setText(exam.getCourse());
        holder.txtDate.setText("Tanggal: " + exam.getExamDate());

        holder.btnEdit.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onEdit(exam, holder.getAdapterPosition());
                });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null)
                listener.onDelete(exam, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCourse, txtDate;
        Button btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtExamTitle);
            txtCourse = itemView.findViewById(R.id.txtExamCourse);
            txtDate = itemView.findViewById(R.id.txtExamDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
