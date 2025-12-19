package com.example.studylink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.model.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView rvForum;
    private EditText edtComment;
    private Button btnSend;

    private ForumAdapter adapter;
    private List<Post> postList = new ArrayList<>();

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish(); // kembali ke Dashboard
        });


        // init
        rvForum = findViewById(R.id.rvForum);
        edtComment = findViewById(R.id.edtComment);
        btnSend = findViewById(R.id.btnSend);

        apiService = RetrofitClient.getService();

        // RecyclerView
        adapter = new ForumAdapter(postList, apiService);
        rvForum.setAdapter(adapter);

        rvForum.setLayoutManager(new LinearLayoutManager(this));
        rvForum.setAdapter(adapter);

        // Load data
        loadPosts();

        // Kirim komentar
        btnSend.setOnClickListener(v -> sendPost());
    }

    private void loadPosts() {
        apiService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(ForumActivity.this,
                        "Gagal memuat forum", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPost() {
        String komentar = edtComment.getText().toString().trim();

        if (komentar.isEmpty()) {
            edtComment.setError("Komentar tidak boleh kosong");
            return;
        }

        Post post = new Post();
        post.setTitle("Komentar Forum");
        post.setContent(komentar);

        apiService.createPost(post).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    edtComment.setText("");
                    loadPosts(); // refresh
                    Toast.makeText(ForumActivity.this,
                            "Komentar terkirim", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(ForumActivity.this,
                        "Gagal mengirim komentar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
