package com.example.studylink;

import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
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
        View toolbar = findViewById(R.id.toolbarInclude);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        RecyclerView rv = findViewById(R.id.rvCourses);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(CourseEntity course) { showEditDialog(course); }
            @Override
            public void onDeleteClick(CourseEntity course) { deleteCourse(course.getId()); }
            @Override
            public void onMessageClick(CourseEntity course) { /* Opsional */ }
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btnAddCourse).setOnClickListener(v -> showAddDialog());

        observeRoom();
        syncFromApi();
    }

    private void observeRoom() {
        dao.getAllCoursesLive().observe(this, courses -> {
            adapter.submitList(new ArrayList<>(courses));
        });
    }

    private void syncFromApi() {
        api.getCourses("Bearer " + MyToken.get(this))
                .enqueue(new Callback<List<Course>>() {
                    @Override
                    public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            new Thread(() -> {
                                for(Course c : response.body()){
                                    dao.insert(new CourseEntity(c.getId(), c.getName(), c.getDescription(), c.getTime(), c.getInstructor()));
                                }
                            }).start();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Course>> call, Throwable t) {
                        Toast.makeText(CourseActivity.this, "Gagal sinkron API", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Course");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32,16,32,16);

        EditText etName = new EditText(this); etName.setHint("Nama Course"); layout.addView(etName);
        EditText etDesc = new EditText(this); etDesc.setHint("Deskripsi"); layout.addView(etDesc);
        EditText etTime = new EditText(this); etTime.setHint("Waktu"); layout.addView(etTime);
        EditText etInstructor = new EditText(this); etInstructor.setHint("Instruktur"); layout.addView(etInstructor);

        builder.setView(layout);
        builder.setPositiveButton("Simpan", (d,w) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String instructor = etInstructor.getText().toString().trim();

            if(name.isEmpty() || desc.isEmpty() || time.isEmpty() || instructor.isEmpty()){
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            addCourse(name, desc, time, instructor);
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void addCourse(String name, String desc, String time, String instructor){
        Course course = new Course(0, name, desc, time, instructor);
        api.addCourse("Bearer " + MyToken.get(this), course)
                .enqueue(new Callback<Course>() {
                    @Override
                    public void onResponse(Call<Course> call, Response<Course> response) {
                        if(response.isSuccessful() && response.body() != null){
                            Course c = response.body();
                            new Thread(() -> dao.insert(new CourseEntity(c.getId(), c.getName(), c.getDescription(), c.getTime(), c.getInstructor()))).start();
                        }
                    }
                    @Override
                    public void onFailure(Call<Course> call, Throwable t) { t.printStackTrace(); }
                });
    }

    private void showEditDialog(CourseEntity c){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Course");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32,16,32,16);

        EditText etName = new EditText(this); etName.setText(c.getName()); layout.addView(etName);
        EditText etDesc = new EditText(this); etDesc.setText(c.getDescription()); layout.addView(etDesc);
        EditText etTime = new EditText(this); etTime.setText(c.getTime()); layout.addView(etTime);
        EditText etInstructor = new EditText(this); etInstructor.setText(c.getInstructor()); layout.addView(etInstructor);

        builder.setView(layout);
        builder.setPositiveButton("Update", (d,w) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String instructor = etInstructor.getText().toString().trim();

            if(name.isEmpty() || desc.isEmpty() || time.isEmpty() || instructor.isEmpty()){
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            updateCourse(c.getId(), name, desc, time, instructor);
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void updateCourse(int id, String name, String desc, String time, String instructor){
        Course course = new Course(id, name, desc, time, instructor);
        api.updateCourse("Bearer " + MyToken.get(this), id, course)
                .enqueue(new Callback<Course>() {
                    @Override
                    public void onResponse(Call<Course> call, Response<Course> response) {
                        if(response.isSuccessful() && response.body() != null){
                            Course c = response.body();
                            new Thread(() -> dao.update(new CourseEntity(c.getId(), c.getName(), c.getDescription(), c.getTime(), c.getInstructor()))).start();
                        }
                    }
                    @Override
                    public void onFailure(Call<Course> call, Throwable t) { t.printStackTrace(); }
                });
    }

    private void deleteCourse(int id){
        new Thread(() -> dao.deleteById(id)).start();
        api.deleteCourse("Bearer " + MyToken.get(this), id)
                .enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> call, Response<Void> response) {}
                    @Override public void onFailure(Call<Void> call, Throwable t) { t.printStackTrace(); }
                });
    }
}
