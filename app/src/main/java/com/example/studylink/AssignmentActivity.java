package com.example.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AssignmentActivity extends AppCompatActivity {

    Button btnSubmitAssignment;
    RecyclerView rvAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        btnSubmitAssignment = findViewById(R.id.btnSubmitAssignment);
        rvAssignment = findViewById(R.id.rvAssignment);

        // RecyclerView setup
        rvAssignment.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> assignmentList = new ArrayList<>();
        assignmentList.add("Soal Integral");
        assignmentList.add("Latihan Turunan");
        assignmentList.add("Tugas Limit");

        rvAssignment.setAdapter(new AssignmentAdapter(assignmentList));

        // Button Submit → pindah Activity
        btnSubmitAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(AssignmentActivity.this, SubmitAssignmentActivity.class);
            startActivity(intent);
        });
    }
}
