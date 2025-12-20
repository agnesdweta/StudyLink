package com.example.studylink;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CoursesActivity extends AppCompatActivity {

    RecyclerView rvCourses;
    CourseAdapter adapter;
    List<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish(); // kembali ke Dashboard
        });

        rvCourses = findViewById(R.id.rvCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        // DATA DUMMY
        courses = new ArrayList<>();
        courses.add(new Course("Pemrograman Java", "3 SKS"));
        courses.add(new Course("Mobile Programming", "3 SKS"));
        courses.add(new Course("Basis Data", "3 SKS"));
        courses.add(new Course("Rekayasa Perangkat Lunak", "4 SKS"));

        adapter = new CourseAdapter(courses);
        rvCourses.setAdapter(adapter);
    }
}
