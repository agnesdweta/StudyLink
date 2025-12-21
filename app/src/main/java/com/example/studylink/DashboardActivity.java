package com.example.studylink;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studylink.db.DBHelper;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout navHome, navNotif, navProfil;
    private ImageView btnLogout;
    private LinearLayout menuExamLayout, menuCalendarLayout,
            menuForumLayout, menuCoursesLayout,
            menuAssignmentLayout, menuScheduleLayout;

    private TextView txtUserName;
    private DBHelper dbHelper; // 🔥 jadikan global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // =========================
        // INIT DB
        // =========================
        dbHelper = new DBHelper(this);

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
        loadUserName();

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
            dbHelper.deleteUser();
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

    // =========================
    // REFRESH SETIAP BALIK KE DASHBOARD
    // =========================
    @Override
    protected void onResume() {
        super.onResume();
        loadUserName();
    }

    // =========================
    // AMBIL USER DARI SQLITE
    // =========================
    private void loadUserName() {
        Cursor cursor = dbHelper.getUser();
        if (cursor != null && cursor.moveToFirst()) {
            txtUserName.setText(
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
            cursor.close();
        } else {
            txtUserName.setText("User");
        }
    }
}
