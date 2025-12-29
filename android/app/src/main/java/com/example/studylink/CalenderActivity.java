package com.example.studylink;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity
        implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        initWidgets();
        selectedDate = Calendar.getInstance();
        setMonthView();ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(
                    CalenderActivity.this,
                    DashboardActivity.class
            );
            startActivity(intent);
            finish();
        });
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate());
        ArrayList<String> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter =
                new CalendarAdapter(daysInMonth, this);

        calendarRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 7)
        );
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray() {
        ArrayList<String> days = new ArrayList<>();

        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < 42; i++) {
            if (i < firstDayOfWeek || i >= daysInMonth + firstDayOfWeek) {
                days.add("");
            } else {
                days.add(String.valueOf(i - firstDayOfWeek + 1));
            }
        }
        return days;
    }

    private String monthYearFromDate() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return sdf.format(selectedDate.getTime());
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    public void previousMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, -1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, 1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.isEmpty()) {
            Toast.makeText(
                    this,
                    "Selected date: " + dayText + " " + monthYearFromDate(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
