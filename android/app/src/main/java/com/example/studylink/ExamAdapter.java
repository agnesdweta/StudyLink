package com.example.studylink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    private List<ExamEntity> examList;
    private OnExamActionListener listener;
    private Context context;

    public interface OnExamActionListener {
        void onEdit(ExamEntity exam);
        void onDelete(ExamEntity exam);
        void onStart(ExamEntity exam);
    }

    public ExamAdapter(Context context, List<ExamEntity> examList, OnExamActionListener listener) {
        this.context = context;
        this.examList = examList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamEntity exam = examList.get(position);

        // Pastikan semua TextView sudah di-bind
        holder.txtTitle.setText(exam.getTitle());
        holder.txtCourse.setText(exam.getCourse());

        // Format tanggal ke "Hari, dd MMM yyyy"
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = originalFormat.parse(exam.getDate());
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
            holder.txtDate.setText(displayFormat.format(parsedDate));
        } catch (Exception e) {
            holder.txtDate.setText(exam.getDate());
        }
        holder.txtTime.setText(exam.getTime());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(exam));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(exam));
        holder.btnStart.setOnClickListener(v -> listener.onStart(exam));
    }

    @Override
    public int getItemCount() {
        return examList == null ? 0 : examList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCourse, txtDate, txtTime;
        Button btnEdit, btnDelete, btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtExamTitle);
            txtCourse = itemView.findViewById(R.id.txtCourse);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            btnEdit = itemView.findViewById(R.id.btnEditExam);
            btnDelete = itemView.findViewById(R.id.btnDeleteExam);
            btnStart = itemView.findViewById(R.id.btnStartExam);
        }
    }

    public void updateList(List<ExamEntity> newList){
        examList = newList;
        notifyDataSetChanged();
    }
}
