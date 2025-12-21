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

import com.example.studylink.db.DBHelper;

public class ProfileActivity extends AppCompatActivity {

    private TextView btnEditProfile, btnChangePassword;
    private TextView tvFirstName, tvLastName, tvEmail;
    private ImageView btnBack;
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private DBHelper dbHelper; // 🔥 DBHelper global

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
        dbHelper = new DBHelper(this);

        // =======================
        // LOAD DATA USER
        // =======================
        loadUserProfile();

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
    private void loadUserProfile() {
        Cursor cursor = dbHelper.getUser();
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            if (name.contains(" ")) {
                String[] parts = name.split(" ", 2);
                tvFirstName.setText(parts[0]);
                tvLastName.setText(parts[1]);
            } else {
                tvFirstName.setText(name);
                tvLastName.setText("");
            }
            tvEmail.setText(email);
            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // reload otomatis ketika kembali dari EditProfileActivity
        loadUserProfile();
    }
}
