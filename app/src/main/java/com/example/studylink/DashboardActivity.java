package com.example.studylink;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class DashboardActivity extends AppCompatActivity {

    private LinearLayout navHome, navNotif, navProfil;
    private ImageView btnLogout;
    private LinearLayout menuExamLayout, menuCalendarLayout,
            menuForumLayout, menuCoursesLayout,
            menuAssignmentLayout, menuScheduleLayout;

    private TextView txtUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // =========================
        // INIT DB
        // =========================


        // =========================
        // FIND VIEW
        // =========================
        txtUserName = findViewById(R.id.txtUsername);


        navHome = findViewById(R.id.navHome);
        navNotif = findViewById(R.id.navNotif);
        navProfil = findViewById(R.id.navProfil);
        btnLogout = findViewById(R.id.btnLogout);

        menuExamLayout = findViewById(R.id.menuExamLayout);
        menuForumLayout = findViewById(R.id.menuForumLayout);
        menuCoursesLayout = findViewById(R.id.menuCourses);
        menuCalendarLayout = findViewById(R.id.menuCalendarLayout);
        menuAssignmentLayout = findViewById(R.id.menuAssignmentLayout);
        menuScheduleLayout = findViewById(R.id.menuScheduleLayout);

        LinearLayout menuRow1 = findViewById(R.id.menuRow1);
        menuRow1.bringToFront();

        // =========================
        // LOAD USER FIRST TIME
        // =========================


        // =========================
        // NAVIGATION
        // =========================
        navHome.setOnClickListener(v ->
                Toast.makeText(this, "Home diklik", Toast.LENGTH_SHORT).show()
        );

        navProfil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );

        navNotif.setOnClickListener(v ->
                Toast.makeText(this, "Notifikasi diklik", Toast.LENGTH_SHORT).show()
        );

        btnLogout.setOnClickListener(v -> {

            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
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
        menuAssignmentLayout.setOnClickListener(v ->
                startActivity(new Intent(this, AssignmentActivity.class))
        );
        menuScheduleLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ScheduleActivity.class))
        );
    }
}


    // =========================
    // REFRESH SETIAP BALIK KE DASHBOARD
    // =========================


    // =========================
    // AMBIL USER DARI SQLITE
    // =========================

