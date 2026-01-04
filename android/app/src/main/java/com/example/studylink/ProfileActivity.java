package com.example.studylink;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private TextView btnEditProfile, btnChangePassword;
    private TextView tvFirstName, tvLastName, tvEmail;
    private ImageView btnBack;
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private long userId = 1;
    private ImageView ivProfilePhoto, btnEditPhoto;

    private AppDatabase db; // <--- penting
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBack = findViewById(R.id.btnBack);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);

        db = AppDatabase.getInstance(this);
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Intent data = result.getData();
                        String firstName = data.getStringExtra("first_name");
                        String lastName = data.getStringExtra("last_name");
                        String email = data.getStringExtra("email");

                        tvFirstName.setText(firstName);
                        tvLastName.setText(lastName);
                        tvEmail.setText(email);

                        UserEntity user = new UserEntity(userId, firstName, lastName, email);

                        // update Room
                        new Thread(() -> db.userDao().update(user)).start();

                        // update backend
                        ApiService api = RetrofitClient.getService();
                        api.updateUser(userId, user).enqueue(new Callback<UserEntity>() {
                            @Override
                            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) { }
                            @Override
                            public void onFailure(Call<UserEntity> call, Throwable t) { t.printStackTrace(); }
                        });
                    }
                });
        // ===== launcher untuk pilih foto =====
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        if(imageUri != null){
                            ivProfilePhoto.setImageURI(imageUri); // tampil di ImageView
                            savePhoto(imageUri);                    // simpan ke Room & backend
                        }
                    }
                });
        btnBack.setOnClickListener(v -> onBackPressed());
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("user_id", userId);
            editProfileLauncher.launch(intent);
        });
        btnEditPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*"); // hanya gambar
            pickImageLauncher.launch(intent);
        });

        // load user
        loadUser();
    }
    private void savePhoto(Uri imageUri) {
        String path = imageUri.toString();

        // ===== Update Room =====
        new Thread(() -> {
            UserEntity userRoom = db.userDao().getUserById(userId);
            if (userRoom != null) {
                userRoom.setPhotoPath(path);
                db.userDao().update(userRoom);
            }
        }).start();

        // ===== Update backend user info =====
        UserEntity user = new UserEntity(
                userId,
                tvFirstName.getText().toString(),
                tvLastName.getText().toString(),
                tvEmail.getText().toString()
        );
        user.setPhotoPath(path);
        ApiService api = RetrofitClient.getService();
        api.updateUser(userId, user).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) { }
            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) { t.printStackTrace(); }
        });

        // ===== Upload foto ke backend =====
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            RequestBody requestFile = RequestBody.create(bytes, okhttp3.MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("photo", "photo.jpg", requestFile);

            api.uploadUserPhoto(userId, body).enqueue(new Callback<UserEntity>() {
                @Override
                public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // update Room lagi dengan data backend (opsional)
                        UserEntity updated = response.body();
                        new Thread(() -> db.userDao().update(updated)).start();
                    }
                }

                @Override
                public void onFailure(Call<UserEntity> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadUser() {
        ApiService api = RetrofitClient.getService();
        api.getUser(userId).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                if(response.isSuccessful() && response.body() != null){
                    UserEntity user = response.body();
                    tvFirstName.setText(user.getFirstName());
                    tvLastName.setText(user.getLastName());
                    tvEmail.setText(user.getEmail());

                    new Thread(() -> db.userDao().insert(user)).start();
                }
            }
            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
