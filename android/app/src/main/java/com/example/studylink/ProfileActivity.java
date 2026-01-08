package com.example.studylink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;
import com.example.studylink.util.TokenManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvFirstName, tvLastName, tvEmail, btnEditProfile, tvPhotoFileName;
    private ImageView ivProfilePhoto, btnEditPhoto, btnBack;
    private long userId;

    private AppDatabase db;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private RecyclerView rvProfiles;
    private ProfileAdapter adapter;
    private List<UserEntity> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ======= findViewById =======
        tvProfileName = findViewById(R.id.tvProfileName);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnBack = findViewById(R.id.btnBack);
        tvPhotoFileName = findViewById(R.id.tvPhotoFileName);
        rvProfiles = findViewById(R.id.rvProfiles);

        db = AppDatabase.getInstance(this);

        adapter = new ProfileAdapter(this, userList, db, (user, position) -> {});
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        rvProfiles.setAdapter(adapter);

        TokenManager tokenManager = new TokenManager(this);
        this.userId = tokenManager.getUserId();

        if (this.userId <= 0) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> onBackPressed());
        btnEditProfile.setOnClickListener(v -> openEditProfile());
        btnEditPhoto.setOnClickListener(v -> pickPhoto());

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) uploadPhoto(uri);
                    }
                }
        );

        loadUser();
    }

    private void openEditProfile() {
        Intent i = new Intent(this, EditProfileActivity.class);
        i.putExtra("user_id", userId);
        startActivity(i);
    }

    private void pickPhoto() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        pickImageLauncher.launch(i);
    }

    private void uploadPhoto(Uri uri) {
        try {
            // Convert Uri → byte[]
            InputStream is = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read;
            while ((read = is.read(buf)) != -1) {
                bos.write(buf, 0, read);
            }
            byte[] bytes = bos.toByteArray();
            is.close();

            // MultipartBody.Part
            RequestBody requestFile = RequestBody.create(bytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("photo", "profile.jpg", requestFile);

            // Upload ke server
            RetrofitClient.getService()
                    .uploadUserPhoto(userId, body)
                    .enqueue(new Callback<UserEntity>() {
                        @Override
                        public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(ProfileActivity.this, "Foto berhasil diupload", Toast.LENGTH_SHORT).show();
                                UserEntity updatedUser = response.body();

                                // Update CircularImageView
                                if (updatedUser.getPhotoPath() != null && !updatedUser.getPhotoPath().isEmpty()) {
                                    Glide.with(ProfileActivity.this)
                                            .load("http://10.0.2.2:3000/uploads/" + updatedUser.getPhotoPath())
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.default_avatar)
                                            .into(ivProfilePhoto);

                                    // Update tvPhotoFileName tanpa .jpg
                                    String fileName = updatedUser.getPhotoPath();
                                    if (fileName.contains(".")) {
                                        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                                    }
                                    tvPhotoFileName.setText(fileName);
                                }

                                // Simpan ke Room
                                Executors.newSingleThreadExecutor().execute(() -> db.userDao().insert(updatedUser));

                                // Update RecyclerView
                                userList.clear();
                                userList.add(updatedUser);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Upload gagal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserEntity> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error membaca file", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUser() {
        ApiService api = RetrofitClient.getService();
        api.getUser(userId).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> res) {
                if (res.isSuccessful() && res.body() != null) {
                    UserEntity user = res.body();

                    tvProfileName.setText(
                            user.getUsername() != null ? user.getUsername() : user.getFirstName() + " " + user.getLastName()
                    );
                    tvFirstName.setText(user.getFirstName());
                    tvLastName.setText(user.getLastName());
                    tvEmail.setText(user.getEmail());

                    Glide.with(ProfileActivity.this).clear(ivProfilePhoto);
                    if (user.getPhotoPath() != null && !user.getPhotoPath().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load("http://10.0.2.2:3000/uploads/" + user.getPhotoPath())
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.default_avatar)
                                .into(ivProfilePhoto);

                        String fileName = user.getPhotoPath();
                        if (fileName.contains(".")) fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                        tvPhotoFileName.setText(""); // kosongkan

                    } else {
                        ivProfilePhoto.setImageResource(R.drawable.default_avatar);
                        tvPhotoFileName.setText("Belum ada foto");
                    }

                    Executors.newSingleThreadExecutor().execute(() -> db.userDao().insert(user));

                    userList.clear();
                    userList.add(user);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
            }
        });
    }
}
