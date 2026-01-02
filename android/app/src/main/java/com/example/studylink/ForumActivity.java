package com.example.studylink;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumActivity extends AppCompatActivity implements ForumAdapter.ForumClickListener {

    private RecyclerView rvForum;
    private EditText edtComment;
    private Button btnSend;
    private List<ForumEntity> forumList;
    private AppDatabase db;
    private ForumAdapter adapter;
    private ApiService api;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        rvForum = findViewById(R.id.rvForum);
        edtComment = findViewById(R.id.edtComment);
        btnSend = findViewById(R.id.btnSend);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);
        forumList = new ArrayList<>();
        adapter = new ForumAdapter(forumList, this);
        rvForum.setLayoutManager(new LinearLayoutManager(this));
        rvForum.setAdapter(adapter);

        api = RetrofitClient.getService();

        loadForumsFromDB();
        loadForumsFromAPI();

        btnSend.setOnClickListener(v -> sendComment());
    }

    // ===== Load dari Room
    private void loadForumsFromDB() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ForumEntity> list = db.forumDao().getAll();
            runOnUiThread(() -> {
                forumList.clear();
                forumList.addAll(list);
                adapter.notifyDataSetChanged();
            });
        });
    }

    // ===== Load dari API (sinkron Room)
    private void loadForumsFromAPI() {
        api.getForums().enqueue(new Callback<List<ForumEntity>>() {
            @Override
            public void onResponse(Call<List<ForumEntity>> call, Response<List<ForumEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.forumDao().deleteAll(); // Hapus data lama di Room
                        List<ForumEntity> list = response.body();
                        for (ForumEntity f : list) {
                            db.forumDao().insert(f); // insert pakai REPLACE
                        }
                        runOnUiThread(() -> {
                            forumList.clear();
                            forumList.addAll(list);
                            adapter.notifyDataSetChanged();
                        });
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ForumEntity>> call, Throwable t) {
                Toast.makeText(ForumActivity.this, "Gagal load forum: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===== Send Comment
    private void sendComment() {
        String text = edtComment.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Komentar kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String createdAt = sdf.format(new Date());

        ForumEntity forum = new ForumEntity(id, text, "Agnes", createdAt);
        // Insert ke API dulu
        api.createForumPost(forum).enqueue(new Callback<ForumEntity>() {
            @Override
            public void onResponse(Call<ForumEntity> call, Response<ForumEntity> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForumEntity created = response.body();
                    // Update ID Room sesuai ID server
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.forumDao().insert(created);
                        runOnUiThread(() -> {
                            forumList.add(created);
                            adapter.notifyItemInserted(forumList.size() - 1);
                            rvForum.scrollToPosition(forumList.size() - 1);
                            edtComment.setText("");
                        });
                    });
                } else {
                    Toast.makeText(ForumActivity.this, "API gagal menyimpan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForumEntity> call, Throwable t) {
                Toast.makeText(ForumActivity.this, "Gagal sinkron ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===== ForumClickListener =====
    @Override
    public void onUpdate(ForumEntity forum, int position, String newText) {
        ForumEntity selected = forumList.get(position);
        selected.setContent(newText);

        Executors.newSingleThreadExecutor().execute(() -> {
            db.forumDao().update(selected);

            api.updateForumPost(selected.getId(), selected).enqueue(new Callback<ForumEntity>() {
                @Override
                public void onResponse(Call<ForumEntity> call, Response<ForumEntity> response) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            forumList.set(position, response.body());
                            adapter.notifyItemChanged(position);
                            Toast.makeText(ForumActivity.this, "Update sukses", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForumActivity.this, "Update API gagal", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ForumEntity> call, Throwable t) {
                    runOnUiThread(() ->
                            Toast.makeText(ForumActivity.this, "Update gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    @Override
    public void onDelete(ForumEntity forum, int position) {
        // 1️⃣ Hapus dari Room di background
        Executors.newSingleThreadExecutor().execute(() -> db.forumDao().delete(forum));

        // 3️⃣ Hapus ke API
        api.deleteForumPost(forum.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                runOnUiThread(() -> {
                    for (int i = forumList.size() - 1; i >= 0; i--) {
                        if (forumList.get(i).getId() == forum.getId()) {
                            forumList.remove(i);
                            adapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                    if (!response.isSuccessful()) {
                        Toast.makeText(ForumActivity.this, "Delete API gagal", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(ForumActivity.this, "Delete gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}