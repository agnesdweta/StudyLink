package com.example.studylink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studylink.api.RetrofitClient;
import com.example.studylink.model.EditProfileRequest;
import com.example.studylink.util.TokenManager;
import com.example.studylink.UserEntity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail;
    private TextView btnSaveProfile;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        userId = new TokenManager(this).getUserId();

        // isi data awal
        etFirstName.setText(getIntent().getStringExtra("first_name"));
        etLastName.setText(getIntent().getStringExtra("last_name"));
        etEmail.setText(getIntent().getStringExtra("email"));

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (firstName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        EditProfileRequest body =
                new EditProfileRequest(firstName, lastName, email);

        RetrofitClient.getService()
                .updateProfile(userId, body)
                .enqueue(new Callback<UserEntity>() {
                    @Override
                    public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            setResult(Activity.RESULT_OK);
                            Toast.makeText(EditProfileActivity.this,
                                    "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Toast.makeText(EditProfileActivity.this,
                                    "Gagal update profil", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserEntity> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
