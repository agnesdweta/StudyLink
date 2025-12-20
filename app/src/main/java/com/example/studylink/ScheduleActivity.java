package com.example.studylink;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView rvSchedule;
    ScheduleAdapter adapter;
    List<Schedule> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish(); // kembali ke Dashboard
        });


        rvSchedule = findViewById(R.id.rvSchedule);

        scheduleList = new ArrayList<>();
        scheduleList.add(new Schedule("Mobile Programming", "Monday, 10 Feb 2025", "08:00 - 10:00"));
        scheduleList.add(new Schedule("Database System", "Tuesday, 11 Feb 2025", "10:00 - 12:00"));
        scheduleList.add(new Schedule("UI/UX Design", "Wednesday, 12 Feb 2025", "13:00 - 15:00"));

        adapter = new ScheduleAdapter(scheduleList, schedule -> {
            Toast.makeText(
                    ScheduleActivity.this,
                    "Klik: " + schedule.getTitle(),
                    Toast.LENGTH_SHORT
            ).show();
        });
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(adapter);
    }
}
