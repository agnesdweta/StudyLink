package com.example.studylink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.studylink.db.AppDatabase;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Context context;
    private List<UserEntity> users;
    private OnPhotoClickListener photoClickListener;
    private AppDatabase db;

    public interface OnPhotoClickListener {
        void onPhotoClick(UserEntity user, int position);
    }

    public ProfileAdapter(Context context, List<UserEntity> users, AppDatabase db, OnPhotoClickListener listener) {
        this.context = context;
        this.users = users;
        this.photoClickListener = listener;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_profile_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserEntity user = users.get(position);

        holder.tvName.setText(user.getFirstName() + " " + user.getLastName());
        holder.tvEmail.setText(user.getEmail());

        Glide.with(context).clear(holder.ivPhoto);

        String photoPath = user.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            String fullUrl = photoPath.startsWith("http")
                    ? photoPath
                    : "http://10.0.2.2:3000/uploads/" + photoPath;

            Glide.with(context)
                    .load(fullUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.default_avatar);
        }

        holder.ivPhoto.setOnClickListener(v -> {
            if (photoClickListener != null) {
                photoClickListener.onPhotoClick(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUser(UserEntity updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == updatedUser.getId()) {
                users.set(i, updatedUser);
                notifyItemChanged(i);
                return;
            }
        }
        // kalau user belum ada, tambahkan
        users.add(updatedUser);
        notifyItemInserted(users.size() - 1);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvName, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivProfilePhotoItem);
            tvName = itemView.findViewById(R.id.tvNameItem);
            tvEmail = itemView.findViewById(R.id.tvEmailItem);
        }
    }
}
