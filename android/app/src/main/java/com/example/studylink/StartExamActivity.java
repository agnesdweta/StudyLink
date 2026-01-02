package com.example.studylink;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StartExamActivity extends AppCompatActivity {

    private RecyclerView rvQuestion;
    private Button btnBack, btnAddQuestion;
    private List<Question> questionList;
    private QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exam);

        rvQuestion = findViewById(R.id.rvQuestion);
        btnBack = findViewById(R.id.btnBack);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);

        // RecyclerView setup
        rvQuestion.setLayoutManager(new LinearLayoutManager(this));
        questionList = new ArrayList<>();
        adapter = new QuestionAdapter(questionList);
        rvQuestion.setAdapter(adapter);

        // klik soal untuk edit
        adapter.setOnQuestionClickListener(position -> editQuestionDialog(position));

        // tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // tombol tambah soal
        btnAddQuestion.setOnClickListener(v -> addQuestionDialog());
    }

    private void addQuestionDialog() {
        EditText etQuestion = new EditText(this);
        etQuestion.setHint("Tulis soal di sini");

        new AlertDialog.Builder(this)
                .setTitle("Tambah Soal")
                .setView(etQuestion)
                .setPositiveButton("Simpan", (d, w) -> {
                    String text = etQuestion.getText().toString().trim();
                    if (!text.isEmpty()) {
                        Question q = new Question(text);
                        questionList.add(q);        // simpan ke list
                        adapter.notifyItemInserted(questionList.size() - 1);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void editQuestionDialog(int position) {
        Question q = questionList.get(position);
        EditText etQuestion = new EditText(this);
        etQuestion.setText(q.getText());

        new AlertDialog.Builder(this)
                .setTitle("Edit Soal")
                .setView(etQuestion)
                .setPositiveButton("Simpan", (d, w) -> {
                    String text = etQuestion.getText().toString().trim();
                    if (!text.isEmpty()) {
                        q.setText(text);
                        adapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
