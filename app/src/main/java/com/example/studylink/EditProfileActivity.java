package com.example.studylink;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail;
    private TextView btnSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // findViewById
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());

        // contoh data awal
        etFirstName.setText("Agnes");
        etLastName.setText("Dwetasari");
        etEmail.setText("anisleogils@gmail.com");

        btnSaveProfile.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String email = etEmail.getText().toString();

            Toast.makeText(this,
                    "Profile berhasil diperbarui",
                    Toast.LENGTH_SHORT).show();

            finish(); // kembali ke ProfileActivity
        });
    }
}
