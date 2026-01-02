package com.example.studylink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    public interface ForumClickListener {
        // ===== ForumClickListener =====
        void onDelete(ForumEntity forum, int position);

        void onUpdate(ForumEntity forum, int position, String newText);
    }

    private List<ForumEntity> forums;
    private ForumClickListener listener;

    public ForumAdapter(List<ForumEntity> forums, ForumClickListener listener) {
        this.forums = forums;
        this.listener = listener;
    }

    public void setForums(List<ForumEntity> newForums) {
        forums.clear();
        forums.addAll(newForums);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forum, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForumEntity forum = forums.get(position);
        holder.tvUser.setText(forum.getUser());
        holder.etContent.setText(forum.getContent());
        holder.tvCreatedAt.setText(forum.getCreatedAt());

        holder.etContent.setEnabled(false);
        holder.btnEdit.setText("Edit");

        holder.btnEdit.setOnClickListener(v -> {
            if(!holder.etContent.isEnabled()){
                holder.etContent.setEnabled(true);
                holder.etContent.requestFocus();
                holder.btnEdit.setText("Simpan");

                InputMethodManager imm = (InputMethodManager) holder.itemView.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) imm.showSoftInput(holder.etContent, InputMethodManager.SHOW_IMPLICIT);
            } else {
                String newText = holder.etContent.getText().toString().trim();
                if(newText.isEmpty()){
                    Toast.makeText(holder.itemView.getContext(), "Komentar kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }
                holder.etContent.setEnabled(false);
                holder.btnEdit.setText("Edit");

                if(listener != null){
                    listener.onUpdate(forum, position, newText);
                }
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if(listener != null){
                listener.onDelete(forum, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvCreatedAt;
        EditText etContent;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            etContent = itemView.findViewById(R.id.etContent);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
