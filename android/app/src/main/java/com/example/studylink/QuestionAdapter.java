package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> list;
    private OnQuestionClickListener listener; // listener klik soal

    public interface OnQuestionClickListener {
        void onClick(int position);
    }

    public void setOnQuestionClickListener(OnQuestionClickListener listener) {
        this.listener = listener;
    }

    public QuestionAdapter(List<Question> list) {
        this.list = (list != null) ? list : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = list.get(position);
        holder.tvQuestion.setText((position + 1) + ". " + q.getText());

        // set klik listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addQuestion(Question q) {
        list.add(q);
        notifyItemInserted(list.size() - 1);
    }

    // optional: hapus soal
    public void removeQuestion(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
        }
    }
}
