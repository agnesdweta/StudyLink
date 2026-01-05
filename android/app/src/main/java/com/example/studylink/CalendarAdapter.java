package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<String> daysOfMonth;
    private final List<CalendarEntity> events;
    private final OnItemListener onItemListener;
    private int currentYear;
    private int currentMonth;

    public CalendarAdapter(ArrayList<String> daysOfMonth,
                           List<CalendarEntity> events,
                           int year,
                           int month,
                           OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.events = events != null ? events : new ArrayList<>();
        this.currentYear = year;
        this.currentMonth = month;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calender_cel, parent, false);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.height = parent.getMeasuredHeight() / 6;
            view.setLayoutParams(params);
        }

        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);
        holder.dayOfMonth.setText(day);

        if (!day.isEmpty()) {
            int dayInt = Integer.parseInt(day);
            boolean hasEvent = false;
            for (CalendarEntity e : events) {
                String[] parts = e.getDate().split("-");
                int eventYear = Integer.parseInt(parts[0]);
                int eventMonth = Integer.parseInt(parts[1]);
                int eventDay = Integer.parseInt(parts[2]);
                if (eventYear == currentYear &&
                        eventMonth == currentMonth &&
                        eventDay == dayInt) {
                    hasEvent = true;
                    break;
                }
            }

            if (hasEvent) {
                holder.dayOfMonth.setBackground(
                        holder.itemView.getContext().getDrawable(R.drawable.event_bg)
                );
            }
        }
    }
    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public void updateData(ArrayList<String> newDays,
                           List<CalendarEntity> newEvents,
                           int year,
                           int month) {
        daysOfMonth.clear();
        daysOfMonth.addAll(newDays);

        events.clear();
        events.addAll(newEvents);
        this.currentYear = year;
        this.currentMonth = month;

        notifyDataSetChanged();
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dayOfMonth;
        OnItemListener onItemListener;

        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
