package com.example.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExamActivity extends AppCompatActivity {

    private Button btnStartExam;
    private ImageView btnBack; // tombol back di toolbar (jika ada)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        btnStartExam = findViewById(R.id.btnStartExam);

        // jika toolbar_exam punya tombol back
        btnBack = findViewById(R.id.btnBack);

        // klik tombol Ikuti Ujian
        btnStartExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExamActivity.this,
                        "Ujian dimulai",
                        Toast.LENGTH_SHORT).show();

                // contoh pindah ke halaman soal
                // Intent intent = new Intent(ExamActivity.this, StartExamActivity.class);
                // startActivity(intent);
            }
        });

        // tombol back toolbar
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // kembali ke dashboard
                }
            });
        }
    }
}
