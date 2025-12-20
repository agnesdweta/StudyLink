package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Exam exam);
    }

    private List<Exam> exams;
    private OnItemClickListener listener;

    public ExamAdapter(List<Exam> exams, OnItemClickListener listener) {
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
        Exam exam = exams.get(position);

        holder.txtTitle.setText(exam.getTitle());
        holder.txtCourse.setText(exam.getCourse());
        holder.txtDate.setText("Tanggal: " + exam.getDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(exam));
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCourse, txtDate;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtExamTitle);
            txtCourse = itemView.findViewById(R.id.txtExamCourse);
            txtDate = itemView.findViewById(R.id.txtExamDate);
        }
    }
}
