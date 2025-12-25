package com.example.studylink;

import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;
import com.example.studylink.db.ScheduleDao;
import com.example.studylink.db.ScheduleEntity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView rvSchedule;
    ScheduleAdapter adapter;
    List<Schedule> scheduleList;
    ApiService apiService;
    AppDatabase db;
    ScheduleDao scheduleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        rvSchedule = findViewById(R.id.rvSchedule);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAdd).setOnClickListener(v -> showAddDialog());

        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(scheduleList, schedule -> showEditDeleteDialog(schedule));
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvSchedule.setAdapter(adapter);

        // Inisialisasi API & Room DB
        apiService = RetrofitClient.getService();
        db = AppDatabase.getInstance(this);
        scheduleDao = db.scheduleDao();

        // Load data dari Room dulu
        loadFromLocal();

        // Load dari API (online)
        loadSchedules();
    }

    // ================== LOAD DATA LOCAL ==================
    private void loadFromLocal() {
        List<ScheduleEntity> local = scheduleDao.getAllSchedules();
        scheduleList.clear();
        for (ScheduleEntity e : local) {
            scheduleList.add(new Schedule(e.getId(), e.getTitle(), e.getDate(), e.getTime()));
        }
        adapter.notifyDataSetChanged();
    }

    // ================== ADD DIALOG ==================
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Schedule");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etTitle = new EditText(this);
        etTitle.setHint("Title");
        layout.addView(etTitle);

        EditText etDate = new EditText(this);
        etDate.setHint("Date");
        layout.addView(etDate);

        EditText etTime = new EditText(this);
        etTime.setHint("Time");
        layout.addView(etTime);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            Schedule schedule = new Schedule(title, date, time);

            // Tambah ke API
            addSchedule(schedule);

            // Tambah ke Room DB
            ScheduleEntity local = new ScheduleEntity(title, date, time);
            scheduleDao.insert(local);

            // Refresh list dari lokal
            loadFromLocal();
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    // ================== ADD API ==================
    private void addSchedule(Schedule schedule) {
        apiService.addSchedule(schedule).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ScheduleActivity.this, "Schedule ditambahkan (API)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScheduleActivity.this, "Gagal API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Toast.makeText(ScheduleActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== EDIT / DELETE DIALOG ==================
    private void showEditDeleteDialog(Schedule schedule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit / Delete Schedule");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etTitle = new EditText(this);
        etTitle.setText(schedule.getTitle());
        layout.addView(etTitle);

        EditText etDate = new EditText(this);
        etDate.setText(schedule.getDate());
        layout.addView(etDate);

        EditText etTime = new EditText(this);
        etTime.setText(schedule.getTime());
        layout.addView(etTime);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            schedule.setTitle(etTitle.getText().toString());
            schedule.setDate(etDate.getText().toString());
            schedule.setTime(etTime.getText().toString());

            updateSchedule(schedule);

            // Update Room DB
            ScheduleEntity local = new ScheduleEntity(schedule.getTitle(), schedule.getDate(), schedule.getTime());
            local.setId(schedule.getId());
            scheduleDao.update(local);

            // Refresh list lokal
            loadFromLocal();
        });

        builder.setNeutralButton("Delete", (dialog, which) -> {
            deleteSchedule(schedule.getId());

            // Delete Room DB
            ScheduleEntity local = new ScheduleEntity(schedule.getTitle(), schedule.getDate(), schedule.getTime());
            local.setId(schedule.getId());
            scheduleDao.delete(local);

            // Refresh list lokal
            loadFromLocal();
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    // ================== UPDATE API ==================
    private void updateSchedule(Schedule schedule) {
        apiService.updateSchedule(schedule.getId(), schedule).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ScheduleActivity.this, "Schedule diperbarui (API)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScheduleActivity.this, "Gagal update API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Toast.makeText(ScheduleActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== DELETE API ==================
    private void deleteSchedule(int id) {
        apiService.deleteSchedule(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ScheduleActivity.this, "Schedule dihapus (API)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScheduleActivity.this, "Gagal delete API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ScheduleActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== LOAD API ==================
    private void loadSchedules() {
        apiService.getSchedules().enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    scheduleList.clear();
                    scheduleList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // fallback ke Room
                    loadFromLocal();
                    Toast.makeText(ScheduleActivity.this, "Load dari lokal DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                // fallback ke Room
                loadFromLocal();
                Toast.makeText(ScheduleActivity.this, "Load dari lokal DB", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
