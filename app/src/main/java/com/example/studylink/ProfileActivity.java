package com.example.studylink;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;



public class ProfileActivity extends AppCompatActivity {

    private TextView btnEditProfile, btnChangePassword;
    private TextView tvFirstName, tvLastName, tvEmail;
    private ImageView btnBack;
    private ActivityResultLauncher<Intent> editProfileLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // =======================
        // FIND VIEW BY ID
        // =======================
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        btnBack = findViewById(R.id.btnBack);

        // =======================
        // INIT DB
        // =======================


        // =======================
        // LOAD DATA USER
        // =======================


        // =======================
        // ACTIVITY RESULT LAUNCHER
        // =======================
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String firstName = data.getStringExtra("first_name");
                            String lastName = data.getStringExtra("last_name");
                            String email = data.getStringExtra("email");

                            tvFirstName.setText(firstName);
                            tvLastName.setText(lastName);
                            tvEmail.setText(email);
                        }
                    }
                });

        // =======================
        // CLICK LISTENERS
        // =======================
        btnBack.setOnClickListener(v -> onBackPressed());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    // =======================
    // METHOD: LOAD USER PROFILE DARI SQLITE
    // =======================


    @Override
    protected void onResume() {
        super.onResume();
        // reload otomatis ketika kembali dari EditProfileActivity

    }
}
