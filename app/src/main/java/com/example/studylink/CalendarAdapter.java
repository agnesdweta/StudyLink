package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarAdapter
        extends RecyclerView.Adapter<CalenderViewHolder> {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<String> daysOfMonth,
                           OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalenderViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater =
                LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(
                R.layout.calender_cel, parent, false);

        // Tinggi item kalender (6 baris)
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = parent.getMeasuredHeight() / 6;

        return new CalenderViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CalenderViewHolder holder, int position) {

        String day = daysOfMonth.get(position);
        holder.dayOfMonth.setText(day);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
