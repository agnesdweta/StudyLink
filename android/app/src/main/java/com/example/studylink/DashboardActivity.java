package com.example.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studylink.util.TokenManager;


public class DashboardActivity extends AppCompatActivity {

    private LinearLayout navHome, navCamera, navProfil;
    private ImageView btnLogout;
    private EditText edtSearch;
    private LinearLayout menuExamLayout, menuCalendarLayout,
            menuForumLayout, menuCoursesLayout,
            menuAssignmentLayout, menuScheduleLayout;

    private TextView txtUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtUserName = findViewById(R.id.txtUsername);
        TokenManager tokenManager = new TokenManager(this);
        String username = tokenManager.getUsername();
        txtUserName.setText("Hallo, " + username);
        navHome = findViewById(R.id.navHome);
        navCamera = findViewById(R.id.navCamera);
        navProfil = findViewById(R.id.navProfil);
        btnLogout = findViewById(R.id.btnLogout);
        edtSearch = findViewById(R.id.edtSearch);

        Handler handler = new Handler();
        final Runnable[] searchRunnable = new Runnable[1];

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = edtSearch.getText().toString().trim();

            if (keyword.isEmpty()) {
                Toast.makeText(this, "Masukkan kata pencarian", Toast.LENGTH_SHORT).show();
                return true;
            }

            Intent intent = new Intent(DashboardActivity.this, SearchResultActivity.class);
            intent.putExtra("keyword", keyword);
            startActivity(intent);

            return true;
        });

        menuExamLayout = findViewById(R.id.menuExamLayout);
        menuForumLayout = findViewById(R.id.menuForumLayout);
        menuCoursesLayout = findViewById(R.id.menuCourses);
        menuCalendarLayout = findViewById(R.id.menuCalendarLayout);
        menuAssignmentLayout = findViewById(R.id.menuAssignmentLayout);
        menuScheduleLayout = findViewById(R.id.menuScheduleLayout);

        LinearLayout menuRow1 = findViewById(R.id.menuRow1);

        navProfil.setClickable(true);
        navProfil.setFocusable(true);

        navHome.setOnClickListener(v ->
                Toast.makeText(this, "Home diklik", Toast.LENGTH_SHORT).show()
        );

        navProfil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );

        navCamera.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, CameraActivity.class))
        );

        btnLogout.setOnClickListener(v -> {

            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        menuCalendarLayout.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarActivity.class))
        );
        menuExamLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ExamActivity.class))
        );
        menuForumLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ForumActivity.class))
        );
        menuCoursesLayout.setOnClickListener(v ->
                startActivity(new Intent(this, CourseActivity.class))
        );
        menuAssignmentLayout.setOnClickListener(v ->
                startActivity(new Intent(this, AssignmentActivity.class))
        );
        menuScheduleLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ScheduleActivity.class))
        );
    }
}


