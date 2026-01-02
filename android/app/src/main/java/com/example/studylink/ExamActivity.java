package com.example.studylink;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamActivity extends AppCompatActivity {

    private RecyclerView rvExam;
    private FloatingActionButton btnAddExam;
    private ExamAdapter adapter;
    private List<ExamEntity> examList;

    // Room
    private AppDatabase db;
    private ExamDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        rvExam = findViewById(R.id.rvExam);
        btnAddExam = findViewById(R.id.btnAddExam);

        db = AppDatabase.getInstance(this);
        dao = db.examDao();

        examList = new ArrayList<>();
        adapter = new ExamAdapter(this, examList, new ExamAdapter.OnExamActionListener() {
            @Override
            public void onEdit(ExamEntity exam) {
                showEditDialog(exam);
            }

            @Override
            public void onDelete(ExamEntity exam) {
                deleteExam(exam);
            }

            @Override
            public void onStart(ExamEntity exam) {
                Intent intent = new Intent(ExamActivity.this, StartExamActivity.class);
                intent.putExtra("exam_id", exam.getId());
                startActivity(intent);
            }
        });

        rvExam.setLayoutManager(new LinearLayoutManager(this));
        rvExam.setAdapter(adapter);

        // Tombol kembali (jika ada)
        if (findViewById(R.id.btnBack) != null)
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // FAB tambah exam
        if (btnAddExam != null)
            btnAddExam.setOnClickListener(v -> showAddDialog());

        loadExams();
    }

    // ================== LOAD EXAMS ==================
    private void loadExams() {
        new Thread(() -> {
            examList.clear();
            examList.addAll(dao.getAll());
            runOnUiThread(() -> adapter.updateList(examList));
            syncFromApi(); // ambil dari API
        }).start();
    }

    // ================== ADD EXAM ==================
    private void showAddDialog() {
        EditText etTitle = new EditText(this);
        EditText etCourse = new EditText(this);
        EditText etDate = new EditText(this);
        EditText etTime = new EditText(this);

        etTitle.setHint("Judul Exam");
        etCourse.setHint("Mata Kuliah");
        etDate.setHint("Pilih Tanggal");
        etTime.setHint("HH:mm - HH:mm");
        etTime.setFocusable(false);
        etTime.setClickable(true);
        etTime.setOnClickListener(v -> {
                    Calendar calendar = Calendar.getInstance();
            // TimePicker pertama: jam mulai
            new TimePickerDialog(this, (view, hourStart, minuteStart) -> {
                String start = String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart);

                // TimePicker kedua: jam selesai
                new TimePickerDialog(this, (view1, hourEnd, minuteEnd) -> {
                    String end = String.format(Locale.getDefault(), "%02d:%02d", hourEnd, minuteEnd);

                    // set text di EditText sebagai range
                    etTime.setText(start + " - " + end);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        etDate.setFocusable(false);
        etDate.setClickable(true);

        etDate.setOnClickListener(v -> {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePicker = new DatePickerDialog(this, (view, y, m, d) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(y, m, d);

                // Format tanggal lengkap: Hari, dd MMM yyyy
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                String formattedDate = sdf.format(selectedDate.getTime());

                etDate.setText(formattedDate);
            }, year, month, day);

            datePicker.show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        layout.addView(etTitle);
        layout.addView(etCourse);
        layout.addView(etDate);
        layout.addView(etTime);

        new AlertDialog.Builder(this)
                .setTitle("Tambah Exam")
                .setView(layout)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String course = etCourse.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    String time = etTime.getText().toString().trim();

                    if (title.isEmpty() || course.isEmpty() || date.isEmpty() || time.isEmpty()) {
                        Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ExamEntity exam = new ExamEntity(System.currentTimeMillis(), title, course, date, time);

                    // Simpan ke Room
                    new Thread(() -> dao.insert(exam)).start();

                    // Simpan ke API
                    ApiService api = RetrofitClient.getService();
                    api.createExam(exam).enqueue(new Callback<ExamEntity>() {
                        @Override
                        public void onResponse(Call<ExamEntity> call, Response<ExamEntity> response) {
                            runOnUiThread(() -> {
                                examList.add(exam);
                                adapter.updateList(examList);
                            });
                        }
                        @Override
                        public void onFailure(Call<ExamEntity> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // ================== EDIT EXAM ==================
    private void showEditDialog(ExamEntity exam) {
        EditText etTitle = new EditText(this);
        EditText etCourse = new EditText(this);
        EditText etDate = new EditText(this);
        EditText etTime = new EditText(this);

        etTitle.setText(exam.getTitle());
        etCourse.setText(exam.getCourse());

        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // format lama
            Date parsedDate = originalFormat.parse(exam.getDate());
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
            etDate.setText(displayFormat.format(parsedDate));
        } catch (Exception e) {
            etDate.setText(exam.getDate()); // fallback jika parsing gagal
        }

        etTime.setText(exam.getTime());
        etTime.setHint("HH:mm - HH:mm");
        etTime.setFocusable(false);
        etTime.setClickable(true);

        etTime.setOnClickListener(v -> {
                    Calendar calendar = Calendar.getInstance();
            // TimePicker pertama: jam mulai
            new TimePickerDialog(this, (view, hourStart, minuteStart) -> {
                String start = String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart);

                // TimePicker kedua: jam selesai
                new TimePickerDialog(this, (view1, hourEnd, minuteEnd) -> {
                    String end = String.format(Locale.getDefault(), "%02d:%02d", hourEnd, minuteEnd);

                    // set range di EditText
                    etTime.setText(start + " - " + end);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        etDate.setFocusable(false);
        etDate.setClickable(true);
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                Date date = sdf.parse(etDate.getText().toString());
                calendar.setTime(date);
            } catch (Exception ignored) {}

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePicker = new DatePickerDialog(this, (view, y, m, d) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(y, m, d);

                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                        String formattedDate = sdf.format(selectedDate.getTime());
                        etDate.setText(formattedDate);
                    }, year, month, day);
            datePicker.show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        layout.addView(etTitle);
        layout.addView(etCourse);
        layout.addView(etDate);
        layout.addView(etTime);

        new AlertDialog.Builder(this)
                .setTitle("Edit Exam")
                .setView(layout)
                .setPositiveButton("Update", (dialog, which) -> {
                    exam.setTitle(etTitle.getText().toString().trim());
                    exam.setCourse(etCourse.getText().toString().trim());
                            try {
                                SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                                Date date = displayFormat.parse(etDate.getText().toString());
                                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                exam.setDate(originalFormat.format(date));
                            } catch (Exception e) {
                                exam.setDate(etDate.getText().toString());
                            }
                    exam.setTime(etTime.getText().toString().trim());

                    // Update Room
                    new Thread(() -> dao.update(exam)).start();

                    // Update API
                    ApiService api = RetrofitClient.getService();
                    api.updateExam(exam.getId(), exam).enqueue(new Callback<ExamEntity>() {
                        @Override
                        public void onResponse(Call<ExamEntity> call, Response<ExamEntity> response) {
                            runOnUiThread(() -> adapter.updateList(examList));
                        }
                        @Override
                        public void onFailure(Call<ExamEntity> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // ================== DELETE EXAM ==================
    private void deleteExam(ExamEntity exam) {
        // Delete Room
        new Thread(() -> dao.delete(exam)).start();

        // Delete API
        ApiService api = RetrofitClient.getService();
        api.deleteExam(exam.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                runOnUiThread(() -> {
                    examList.remove(exam);
                    adapter.updateList(examList);
                });
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // ================== SYNC DARI API KE ROOM ==================
    private void syncFromApi() {
        ApiService api = RetrofitClient.getService();
        api.getExams().enqueue(new Callback<List<ExamEntity>>() {
            @Override
            public void onResponse(Call<List<ExamEntity>> call, Response<List<ExamEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        dao.deleteAll(); // Hapus semua data lama di Room
                        for (ExamEntity e : response.body()) dao.insert(e);

                        runOnUiThread(() -> {
                            examList.clear();
                            examList.addAll(response.body());
                            adapter.updateList(examList);
                        });
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<List<ExamEntity>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
