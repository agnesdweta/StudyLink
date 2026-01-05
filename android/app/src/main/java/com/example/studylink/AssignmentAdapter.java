package com.example.studylink;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    public interface OnActionListener {
        void onEdit(AssignmentEntity assignment, int position);

        void onDelete(AssignmentEntity assignment, int position);

        void onUpload(AssignmentEntity assignment);
        void onDeleteImage(AssignmentEntity assignment, int position);
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

        // 🔹 Tampilkan foto jika ada
        if (assignment.getLocalImagePath() != null && !assignment.getLocalImagePath().isEmpty()) {
            // tampilkan gambar lokal sementara
            holder.imgPhoto.setImageURI(Uri.parse(assignment.getLocalImagePath()));
        } else if (assignment.getImage() != null && !assignment.getImage().isEmpty()) {
            // tampilkan gambar dari server
            Glide.with(holder.itemView.getContext())
                    .load("http://10.0.2.2:3000/uploads/" + assignment.getImage())
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .error(android.R.drawable.ic_delete)
                    .into(holder.imgPhoto);
        } else {
            holder.imgPhoto.setImageResource(android.R.drawable.ic_menu_report_image);
        }
        // 🔹 Klik tombol delete di gambar untuk hapus
        holder.btnDeleteImage.setOnClickListener(v -> {
            if (listener != null && assignment.getImage() != null && !assignment.getImage().isEmpty()) {
                listener.onDeleteImage(assignment, holder.getAdapterPosition());
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null)
                listener.onEdit(assignment, holder.getAdapterPosition());
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null)
                listener.onDelete(assignment, holder.getAdapterPosition());
        });
        holder.btnUpload.setOnClickListener(v -> {
            if (listener != null)
                listener.onUpload(assignment); // 🔹 Panggil listener Upload
        });
        holder.btnDeleteImage.setOnClickListener(v -> listener.onDeleteImage(assignment, position));
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    // 🔹 Tambahkan method update list
    public void updateList(List<AssignmentEntity> newList) {
        this.assignments = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCourse, txtDeadline;
        Button btnEdit, btnDelete, btnUpload;
        ImageView imgPhoto;
        ImageButton btnDeleteImage;// 🔹 ImageView untuk foto

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtAssignmentTitle);
            txtCourse = itemView.findViewById(R.id.txtAssignmentCourse);
            txtDeadline = itemView.findViewById(R.id.txtAssignmentDeadline);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpload = itemView.findViewById(R.id.btnUploadAssignment);
            btnDeleteImage = itemView.findViewById(R.id.btnDeleteImage);
            imgPhoto = itemView.findViewById(R.id.imgAssignment); // pastikan ada di layout
        }
    }
}