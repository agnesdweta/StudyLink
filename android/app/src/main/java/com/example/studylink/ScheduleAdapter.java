package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Schedule schedule);
    }

    private List<Schedule> list;
    private final OnItemClickListener listener;

    public ScheduleAdapter(List<Schedule> list, OnItemClickListener listener) {
        this.list = (list != null) ? list : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule s = list.get(position);

        holder.tvTitle.setText(s.getTitle());
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = originalFormat.parse(s.getDate());
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
            holder.tvDate.setText(displayFormat.format(parsedDate));
        } catch (Exception e) {
            holder.tvDate.setText(s.getDate()); // fallback
        }

        holder.tvTime.setText(s.getTime());

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(list.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // 🔥 METHOD PENTING untuk update data
    public void updateData(List<Schedule> newList) {
        this.list = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate  = itemView.findViewById(R.id.tvDate);
            tvTime  = itemView.findViewById(R.id.tvTime);
        }
    }
}
