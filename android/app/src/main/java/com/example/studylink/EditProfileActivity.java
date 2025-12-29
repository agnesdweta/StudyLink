package com.example.studylink;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
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

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);



        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }


    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (firstName.isEmpty()) {
            Toast.makeText(this, "Nama depan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = firstName + (lastName.isEmpty() ? "" : " " + lastName);

        // 🔥 UPDATE SQLite

        Intent resultIntent = new Intent();
        resultIntent.putExtra("first_name", firstName);
        resultIntent.putExtra("last_name", lastName);
        resultIntent.putExtra("email", email);
        setResult(Activity.RESULT_OK, resultIntent);

        Toast.makeText(this, "Profile berhasil diupdate", Toast.LENGTH_SHORT).show();

        // Kembali ke Dashboard, nama otomatis berubah karena onResume()
        finish();
    }
}
