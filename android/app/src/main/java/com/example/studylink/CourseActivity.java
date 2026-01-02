package com.example.studylink;

import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseActivity extends AppCompatActivity {

    private CourseDao dao;
    private ApiService api;
    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        dao = AppDatabase.getInstance(this).courseDao();
        api = RetrofitClient.getService();

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvCourses);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CourseAdapter(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(CourseEntity course) {
                showEditDialog(course);
            }

            @Override
            public void onDeleteClick(CourseEntity course) {
                deleteCourse(course.getId());
            }

            @Override
            public void onMessageClick(CourseEntity course) {
            }
        });

        rv.setAdapter(adapter);

        findViewById(R.id.btnAddCourse).setOnClickListener(v -> showAddDialog());

        observeRoom();
        syncFromApi();
    }

    // ================= ROOM OBSERVER =================
    private void observeRoom() {
        dao.getAllCoursesLive().observe(this, courses ->
                adapter.submitList(new ArrayList<>(courses))
        );
    }

    // ================= SYNC API → ROOM =================
    private void syncFromApi() {
        api.getCourses()
                .enqueue(new Callback<List<Course>>() {
                    @Override
                    public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new Thread(() -> {
                                dao.deleteAll(); // ⬅️ cegah duplikat
                                for (Course c : response.body()) {
                                    dao.insert(new CourseEntity(
                                            c.getId(),
                                            c.getName(),
                                            c.getDescription(),
                                            c.getTime(),
                                            c.getInstructor()
                                    ));
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Course>> call, Throwable t) {
                        t.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(
                                        CourseActivity.this,
                                        "Gagal sync course",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                });
    }


    // ================= ADD COURSE =================
    private void showAddDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Tambah Course");

        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(32, 16, 32, 16);

        EditText etName = new EditText(this);
        etName.setHint("Nama");
        EditText etDesc = new EditText(this);
        etDesc.setHint("Deskripsi");
        EditText etTime = new EditText(this);
        etTime.setHint("Waktu");
        EditText etInst = new EditText(this);
        etInst.setHint("Instruktur");

        l.addView(etName);
        l.addView(etDesc);
        l.addView(etTime);
        l.addView(etInst);

        b.setView(l);
        b.setPositiveButton("Simpan", (d, w) -> {
            addCourse(
                    etName.getText().toString().trim(),
                    etDesc.getText().toString().trim(),
                    etTime.getText().toString().trim(),
                    etInst.getText().toString().trim()
            );
        });
        b.setNegativeButton("Batal", null);
        b.show();
    }

    private void addCourse(String name, String desc, String time, String inst) {
        if (name.isEmpty() || desc.isEmpty() || time.isEmpty() || inst.isEmpty()) {
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course(0, name, desc, time, inst);

        // ⬇️ TANPA Bearer Token
        api.addCourse(course)
                .enqueue(new Callback<Course>() {
                    @Override
                    public void onResponse(Call<Course> call, Response<Course> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Course c = response.body();

                            new Thread(() -> {
                                dao.insert(new CourseEntity(
                                        c.getId(),
                                        c.getName(),
                                        c.getDescription(),
                                        c.getTime(),
                                        c.getInstructor()
                                ));
                            }).start();
                        } else {
                            Toast.makeText(
                                    CourseActivity.this,
                                    "Gagal menambah course",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Course> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(
                                CourseActivity.this,
                                "Koneksi ke server gagal",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // ================= EDIT COURSE =================
    private void showEditDialog(CourseEntity c) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Edit Course");

        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(32, 16, 32, 16);

        EditText etName = new EditText(this);
        etName.setText(c.getName());
        EditText etDesc = new EditText(this);
        etDesc.setText(c.getDescription());
        EditText etTime = new EditText(this);
        etTime.setText(c.getTime());
        EditText etInst = new EditText(this);
        etInst.setText(c.getInstructor());

        l.addView(etName);
        l.addView(etDesc);
        l.addView(etTime);
        l.addView(etInst);

        b.setView(l);
        b.setPositiveButton("Update", (d, w) ->
                updateCourse(
                        c.getId(),
                        etName.getText().toString().trim(),
                        etDesc.getText().toString().trim(),
                        etTime.getText().toString().trim(),
                        etInst.getText().toString().trim()
                )
        );
        b.setNegativeButton("Batal", null);
        b.show();
    }

    private void updateCourse(long id, String name, String desc, String time, String inst) {
        Course course = new Course(id, name, desc, time, inst);

        api.updateCourse(id, course).enqueue(new Callback<Course>() {
            @Override
            public void onResponse(Call<Course> call, Response<Course> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Course c = response.body();
                    new Thread(() ->
                            dao.update(new CourseEntity(
                                    c.getId(),
                                    c.getName(),
                                    c.getDescription(),
                                    c.getTime(),
                                    c.getInstructor()
                            ))
                    ).start();
                }
            }

            @Override
            public void onFailure(Call<Course> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    // ================= DELETE COURSE =================
    private void deleteCourse(long id) {
        new Thread(() -> dao.deleteById(id)).start(); // hapus dulu

        api.deleteCourse(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}