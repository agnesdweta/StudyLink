package com.example.studylink;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AssignmentActivity extends AppCompatActivity {

    RecyclerView rvAssignment;
    AssignmentAdapter adapter;
    List<Assignment> assignments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish(); // kembali ke Dashboard
        });

        rvAssignment = findViewById(R.id.rvAssignment);
        rvAssignment.setLayoutManager(new LinearLayoutManager(this));

        assignments = new ArrayList<>();
        assignments.add(new Assignment(
                "Tugas PBO",
                "Pemrograman Berorientasi Objek",
                "20 Des 2025"
        ));
        assignments.add(new Assignment(
                "Tugas Android",
                "Mobile Programming",
                "22 Des 2025"
        ));


        AssignmentAdapter adapter = new AssignmentAdapter(assignments, assignment -> {
            Toast.makeText(this,
                    "Klik: " + assignment.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });
        rvAssignment.setLayoutManager(new LinearLayoutManager(this));
        rvAssignment.setAdapter(adapter);
    }
}
