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

import okhttp3.MultipartBody;
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
                // 🔥 DELETE API
                ApiService api = RetrofitClient.getService();
                api.deleteAssignment(assignment.getId())
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                loadAssignments(); // refresh API → Room
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(
                                        AssignmentActivity.this,
                                        "Gagal delete ke server",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
            }

            @Override
            public void onUpload(AssignmentEntity assignment) {
                uploadAssignment = assignment;
                openImageChooser();
            }
            @Override
            public void onDeleteImage(AssignmentEntity assignment, int position) {
                deleteImage(assignment, position); // panggil method hapus gambar
            }
        });

        rvAssignment.setAdapter(adapter);

        loadAssignments();

        btnAdd.setOnClickListener(v -> showDialog(null, -1));
    }

    private void openImageChooser() {
        // Buat intent pilih file
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");                  // hanya file gambar
        intent.addCategory(Intent.CATEGORY_OPENABLE); // harus bisa diakses
        startActivityForResult(
                Intent.createChooser(intent, "Pilih file gambar"),
                PICK_IMAGE_REQUEST
        );
    }



    // =======================
    // 🔹 LOAD API → ROOM
    // =======================
    private void loadAssignments() {
        ApiService api = RetrofitClient.getService();
        api.getAssignments()
                .enqueue(new Callback<List<Assignment>>() {
                    @Override
                    public void onResponse(Call<List<Assignment>> call,
                                           Response<List<Assignment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            saveToRoom(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Assignment>> call, Throwable t) {
                        loadFromRoom();
                    }
                });
    }

    private void saveToRoom(List<Assignment> apiData) {
        new Thread(() -> {
            db.assignmentDao().deleteAll(); // 🔥 PENTING: cegah dobel

            for (Assignment a : apiData) {
                db.assignmentDao().insert(new AssignmentEntity(
                        a.getId(),
                        a.getTitle(),
                        a.getCourse(),
                        a.getDeadline(),
                        a.getImage()
                ));
            }

            runOnUiThread(this::loadFromRoom);
        }).start();
    }

    private void loadFromRoom() {
        new Thread(() -> {
            List<AssignmentEntity> data = db.assignmentDao().getAll();

            runOnUiThread(() -> {
                assignments.clear();
                assignments.addAll(data);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    // =======================
    // 🔹 ADD / EDIT
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

                    ApiService api = RetrofitClient.getService();

                    if (assignment == null) {
                        // ===== ADD (API SAJA) =====
                        api.createAssignment(new Assignment(
                                System.currentTimeMillis(),
                                etTitle.getText().toString(),
                                etCourse.getText().toString(),
                                etDeadline.getText().toString(),
                                null
                        )).enqueue(new Callback<Assignment>() {
                            @Override
                            public void onResponse(Call<Assignment> call,
                                                   Response<Assignment> response) {
                                loadAssignments();
                            }

                            @Override
                            public void onFailure(Call<Assignment> call, Throwable t) {
                                Toast.makeText(
                                        AssignmentActivity.this,
                                        "Gagal tambah ke server",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });

                    } else {
                        // ===== UPDATE =====
                        api.updateAssignment(
                                assignment.getId(),
                                new Assignment(
                                        assignment.getId(),
                                        etTitle.getText().toString(),
                                        etCourse.getText().toString(),
                                        etDeadline.getText().toString(),
                                        assignment.getImage()
                                )
                        ).enqueue(new Callback<Assignment>() {
                            @Override
                            public void onResponse(Call<Assignment> call,
                                                   Response<Assignment> response) {
                                loadAssignments();
                            }

                            @Override
                            public void onFailure(Call<Assignment> call, Throwable t) {
                                Toast.makeText(
                                        AssignmentActivity.this,
                                        "Gagal update ke server",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // =======================
    // 🔹 IMAGE UPLOAD
    // =======================
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && uploadAssignment != null) {

            Uri imageUri = data.getData();
            if (imageUri == null) return;

            uploadAssignment.setLocalImagePath(imageUri.toString());
            adapter.notifyDataSetChanged();

            ApiService api = RetrofitClient.getService();
            MultipartBody.Part imagePart =
                    FileUtils.prepareFilePart(this, "image", imageUri);

            api.uploadAssignmentImage(uploadAssignment.getId(), imagePart)
                    .enqueue(new Callback<ImageResponse>() {
                        @Override
                        public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String uploadedFileName = response.body().getFilename(); // misal ImageResponse ada getFilename()
                                uploadAssignment.setImage(uploadedFileName);

                                new Thread(() -> {
                                    db.assignmentDao().update(uploadAssignment); // update Room DB
                                    runOnUiThread(() -> {
                                        adapter.notifyDataSetChanged(); // refresh RecyclerView
                                    });
                                }).start();
                            } else {
                                Toast.makeText(
                                        AssignmentActivity.this,
                                        "Upload berhasil tapi server tidak merespon filename",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ImageResponse> call, Throwable t) {
                            Toast.makeText(
                                    AssignmentActivity.this,
                                    "Upload gagal: " + t.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        }
    }
            // =======================
            // 🔹 DELETE IMAGE
            // =======================
            private void deleteImage(AssignmentEntity assignment, int position) {
                ApiService api = RetrofitClient.getService();
                api.deleteAssignmentImage(assignment.getId()) // pastikan endpoint di server ada: DELETE /assignments/:id/image
                        .enqueue(new Callback<AssignmentEntity>() {
                            @Override
                            public void onResponse(Call<AssignmentEntity> call, Response<AssignmentEntity> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    assignments.set(position, response.body()); // update data list
                                    adapter.notifyItemChanged(position);        // refresh RecyclerView
                                    new Thread(() -> {
                                        db.assignmentDao().update(response.body()); // update Room DB
                                    }).start();
                                    Toast.makeText(AssignmentActivity.this, "Gambar dihapus", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<AssignmentEntity> call, Throwable t) {
                                Toast.makeText(AssignmentActivity.this, "Gagal menghapus gambar", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
