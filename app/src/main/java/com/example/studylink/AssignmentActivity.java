package com.example.studylink;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignmentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;

    RecyclerView rvAssignment;
    FloatingActionButton btnAdd;
    AssignmentAdapter adapter;

    List<AssignmentEntity> assignments = new ArrayList<>();
    AppDatabase db;

    // Assignment yang sedang di-upload gambarnya
    private AssignmentEntity uploadAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvAssignment = findViewById(R.id.rvAssignment);
        btnAdd = findViewById(R.id.btnAdd);

        rvAssignment.setLayoutManager(new LinearLayoutManager(this));
        db = AppDatabase.getInstance(this);

        adapter = new AssignmentAdapter(assignments, new AssignmentAdapter.OnActionListener() {
            @Override
            public void onEdit(AssignmentEntity assignment, int position) {
                showDialog(assignment, position);
            }

            @Override
            public void onDelete(AssignmentEntity assignment, int position) {
                db.assignmentDao().delete(assignment);
                assignments.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onUpload(AssignmentEntity assignment) {
                uploadAssignment = assignment;  // simpan assignment yang akan diupload
                openImageChooser();             // buka galeri
            }
        });
        rvAssignment.setAdapter(adapter);

        // Ambil data dari API atau Room
        loadAssignments();

        btnAdd.setOnClickListener(v -> showDialog(null, -1));
    }

    // =======================
    // 🔹 LOAD DATA DARI API
    // =======================
    private void loadAssignments() {

        String token = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", "");

        ApiService apiService = RetrofitClient.getService();
        apiService.getAssignments("Bearer " + token)
                .enqueue(new Callback<List<Assignment>>() {
                    @Override
                    public void onResponse(Call<List<Assignment>> call,
                                           Response<List<Assignment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            saveToRoom(response.body());
                        } else {
                            loadFromRoom();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Assignment>> call, Throwable t) {
                        loadFromRoom(); // Offline fallback
                    }
                });
    }

    // =======================
    // 🔹 SIMPAN API → ROOM
    // =======================
    private void saveToRoom(List<Assignment> apiData) {
        db.assignmentDao().deleteAll();

        int id = 1;
        for (Assignment a : apiData) {
            AssignmentEntity e = new AssignmentEntity(
                    id++,
                    a.getTitle(),
                    a.getCourse(),
                    a.getDeadline()
            );
            db.assignmentDao().insert(e);
        }

        loadFromRoom();
    }

    // =======================
    // 🔹 LOAD DARI ROOM
    // =======================
    private void loadFromRoom() {
        assignments.clear();
        assignments.addAll(db.assignmentDao().getAll());
        adapter.notifyDataSetChanged();
    }

    // =======================
    // 🔹 DIALOG ADD / EDIT
    // =======================
    private void showDialog(AssignmentEntity assignment, int position) {

        View view = getLayoutInflater()
                .inflate(R.layout.dialog_assignment, null);

        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etCourse = view.findViewById(R.id.etCourse);
        EditText etDeadline = view.findViewById(R.id.etDeadline);

        if (assignment != null) {
            etTitle.setText(assignment.getTitle());
            etCourse.setText(assignment.getCourse());
            etDeadline.setText(assignment.getDeadline());
        }

        new AlertDialog.Builder(this)
                .setTitle(assignment == null ? "Tambah Assignment" : "Edit Assignment")
                .setView(view)
                .setPositiveButton("Simpan", (d, w) -> {
                    if (assignment == null) {
                        AssignmentEntity e = new AssignmentEntity(
                                assignments.size() + 1,
                                etTitle.getText().toString(),
                                etCourse.getText().toString(),
                                etDeadline.getText().toString()
                        );
                        db.assignmentDao().insert(e);
                        assignments.add(e);
                        adapter.notifyItemInserted(assignments.size() - 1);
                    } else {
                        assignment.setTitle(etTitle.getText().toString());
                        assignment.setCourse(etCourse.getText().toString());
                        assignment.setDeadline(etDeadline.getText().toString());

                        db.assignmentDao().update(assignment);
                        adapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // =======================
    // 🔹 OPEN GALERI UNTUK PILIH GAMBAR
    // =======================
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // =======================
    // 🔹 HANDLE HASIL PILIH GAMBAR
    // =======================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null && uploadAssignment != null) {
                // Lakukan upload ke server (kamu bisa pakai Retrofit Multipart)
                Toast.makeText(this, "File dipilih: " + imageUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
                // TODO: uploadImageToServer(imageUri, uploadAssignment);
            }
        }
    }
}
