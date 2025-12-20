package com.example.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout navHome, navNotif, navProfil;
    private ImageView btnLogout;
    private LinearLayout menuExamLayout;
    private LinearLayout menuCalendarLayout;
    private LinearLayout menuForumLayout;
    private LinearLayout menuCoursesLayout;
    private LinearLayout menuScheduleLayout;
    private LinearLayout menuAssignmentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        navHome = findViewById(R.id.navHome);
        navNotif = findViewById(R.id.navNotif);
        navProfil = findViewById(R.id.navProfil);
        btnLogout = findViewById(R.id.btnLogout);
        menuExamLayout = findViewById(R.id.menuExamLayout);
        menuForumLayout = findViewById(R.id.menuForumLayout);
        menuCoursesLayout = findViewById(R.id.menuCourses);
        menuCalendarLayout = findViewById(R.id.menuCalendarLayout);
        menuScheduleLayout = findViewById(R.id.menuScheduleLayout);
        menuAssignmentLayout = findViewById(R.id.menuAssignmentLayout);

        navHome.setOnClickListener(v ->
                Toast.makeText(this, "Home diklik", Toast.LENGTH_SHORT).show()
        );

        navProfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        navNotif.setOnClickListener(v ->
                Toast.makeText(this, "Notifikasi diklik", Toast.LENGTH_SHORT).show()
        );

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        menuCalendarLayout.setOnClickListener(v ->
                startActivity(new Intent(this, CalenderActivity.class))
        );

        menuExamLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ExamActivity.class))
        );

        menuForumLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ForumActivity.class))
        );

        menuCoursesLayout.setOnClickListener(v ->
                startActivity(new Intent(this, CoursesActivity.class))
        );
        menuScheduleLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ScheduleActivity.class))
        );
        menuAssignmentLayout.setOnClickListener(v ->
                startActivity(new Intent(this, AssignmentActivity.class))
        );
    }
}
