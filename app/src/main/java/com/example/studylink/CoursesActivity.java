package com.example.studylink;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

public class CoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        // Ambil toolbar dari include
        Toolbar toolbar = findViewById(R.id.toolbarInclude);
        setSupportActionBar(toolbar);

        // Aktifkan tombol back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Courses");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Fungsi tombol back di toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // kembali ke halaman sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
