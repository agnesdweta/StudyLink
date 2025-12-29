package com.example.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamActivity extends AppCompatActivity {
    RecyclerView rvExam;
    ExamAdapter adapter;
    List<Exam> examList;

    ApiService api;

    private Button btnStartExam;
    private ImageView btnBack; // tombol back di toolbar (jika ada)
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        rvExam = findViewById(R.id.rvExam);
        btnStartExam = findViewById(R.id.btnStartExam);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        examList = new ArrayList<>();
        adapter = new ExamAdapter(examList);

        rvExam.setLayoutManager(new LinearLayoutManager(this));
        rvExam.setAdapter(adapter);

        api = RetrofitClient.getService();
        loadExams();

        // klik tombol Ikuti Ujian
        btnStartExam.setOnClickListener(v -> {
            if (!examList.isEmpty()) {
                int examId = examList.get(0).getId();
                new androidx.appcompat.app.AlertDialog.Builder(ExamActivity.this)
                    .setTitle("Konfirmasi Ujian")
                    .setMessage("Apakah Anda yakin ingin memulai ujian?")
                    .setCancelable(false)
                    .setPositiveButton("Mulai", (dialog, which) -> {
                        // ambil exam pertama
                        Intent intent = new Intent(ExamActivity.this, StartExamActivity.class);
                        intent.putExtra("exam_id", examId);
                        startActivity(intent);
                    })
                        .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                        .show();

                        } else {
                            Toast.makeText(ExamActivity.this, "Tidak ada ujian tersedia", Toast.LENGTH_SHORT).show();
                        }
                    });
        // ===== BUTTON BACK =====
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
    private void loadExams() {
        progressBar.setVisibility(View.VISIBLE);
        btnStartExam.setEnabled(false);

        api.getExams().enqueue(new Callback<List<Exam>>() {
            @Override
            public void onResponse(Call<List<Exam>> call, Response<List<Exam>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    examList.clear();
                    examList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    btnStartExam.setEnabled(!examList.isEmpty());
                } else {
                    Toast.makeText(ExamActivity.this, "Belum ada ujian tersedia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Exam>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ExamActivity.this,
                        "Gagal load exam", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

