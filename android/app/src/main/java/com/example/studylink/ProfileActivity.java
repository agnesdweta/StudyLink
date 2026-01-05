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

    private TextView tvFirstName, tvLastName, tvEmail, btnEditProfile;
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

        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnBack = findViewById(R.id.btnBack);
        rvProfiles = findViewById(R.id.rvProfiles);

        db = AppDatabase.getInstance(this);

        adapter = new ProfileAdapter(this, userList, db, (user, position) -> {});
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        rvProfiles.setAdapter(adapter);

        userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getLong("user_id", -1);

        if (userId == -1) finish();

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

    private void loadUser() {
        ApiService api = RetrofitClient.getService();
        api.getUser(userId).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> res) {
                if (res.isSuccessful() && res.body() != null) {
                    UserEntity user = res.body();

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
                    } else {
                        ivProfilePhoto.setImageResource(R.drawable.default_avatar);
                    }

                    // Simpan ke Room
                    Executors.newSingleThreadExecutor().execute(() -> db.userDao().insert(user));

                    // update RecyclerView
                    userList.clear();
                    userList.add(user);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {}
        });
    }

    private void uploadPhoto(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int r;
            while ((r = is.read(buf)) != -1) bos.write(buf, 0, r);
            is.close();

            RequestBody req = RequestBody.create(bos.toByteArray(), MediaType.parse("image/*"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", req);

            RetrofitClient.getService().uploadUserPhoto(userId, part).enqueue(new Callback<UserEntity>() {
                @Override
                public void onResponse(Call<UserEntity> call, Response<UserEntity> res) {
                    if (res.isSuccessful() && res.body() != null) {
                        UserEntity u = res.body();

                        // update Room (photoPath saja)
                        Executors.newSingleThreadExecutor().execute(() ->
                                db.userDao().updatePhoto(u.getId(), u.getPhotoPath())
                        );

                        // update adapter + Glide
                        runOnUiThread(() -> {
                            adapter.updateUser(u);
                            Glide.with(ProfileActivity.this)
                                    .load("http://10.0.2.2:3000/uploads/" + u.getPhotoPath())
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.drawable.default_avatar)
                                    .into(ivProfilePhoto);
                            Toast.makeText(ProfileActivity.this, "Foto berhasil diupdate", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onFailure(Call<UserEntity> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Upload gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
