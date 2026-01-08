package com.example.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtKeyword, txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.recyclerView);
        txtKeyword = findViewById(R.id.txtKeyword);
        txtEmpty = findViewById(R.id.txtEmpty);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        String keyword = getIntent().getStringExtra("keyword");
        txtKeyword.setText("Hasil pencarian: " + keyword);

        AppDatabase db = AppDatabase.getInstance(this);

        List<SearchResult> results = new ArrayList<>();

        // Ambil semua hasil lintas tabel
        results.addAll(db.searchAllDao().searchExam(keyword));
        results.addAll(db.searchAllDao().searchCourse(keyword));
        results.addAll(db.searchAllDao().searchForum(keyword));
        results.addAll(db.searchAllDao().searchAssignment(keyword));
        results.addAll(db.searchAllDao().searchSchedule(keyword));

        // Tangani hasil kosong
        if(results.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
            txtEmpty.setText("Tidak ada hasil ditemukan.");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            SearchAllAdapter adapter = new SearchAllAdapter(results, item -> {
                // Klik item → buka detail sesuai tipe
                switch(item.type){
                    case "Exam":
                        startActivity(new Intent(this, ExamActivity.class)
                                .putExtra("examId", item.itemId));
                        break;
                    case "Course":
                        startActivity(new Intent(this, CourseActivity.class)
                                .putExtra("courseId", item.itemId));
                        break;
                    case "Forum":
                        startActivity(new Intent(this, ForumActivity.class)
                                .putExtra("forumId", item.itemId));
                        break;
                    case "Assignment":
                        startActivity(new Intent(this, AssignmentActivity.class)
                                .putExtra("assignmentId", item.itemId));
                        break;
                    case "Schedule":
                        startActivity(new Intent(this, ScheduleActivity.class)
                                .putExtra("scheduleId", item.itemId));
                        break;
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }
}
