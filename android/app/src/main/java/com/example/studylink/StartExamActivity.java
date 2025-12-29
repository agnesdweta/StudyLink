package com.example.studylink;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartExamActivity extends AppCompatActivity {

    private TextView txtSoal;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exam);

        txtSoal = findViewById(R.id.txtSoal);

        api = RetrofitClient.getService();

        int examId = getIntent().getIntExtra("exam_id", -1);

        if (examId != -1) {
            loadExamQuestions(examId);
        }
    }

    private void loadExamQuestions(int examId) {
        api.getExamQuestions(examId).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtSoal.setText(response.body().get(0).getQuestion()); // contoh ambil soal pertama
                }
            }
            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                txtSoal.setText("Gagal load soal");
            }
        });
    }
}
