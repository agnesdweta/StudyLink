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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private ScheduleDao dao;
    private ApiService api;
    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        dao = AppDatabase.getInstance(this).scheduleDao();
        api = RetrofitClient.getService();

        RecyclerView rv = findViewById(R.id.rvSchedule);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ScheduleAdapter(new ArrayList<>(), this::showEditDelete);
        rv.setAdapter(adapter);

        findViewById(R.id.btnAdd).setOnClickListener(v -> showAddDialog());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadFromRoom();
        syncFromApi();
    }

    // ================= LOAD ROOM =================
    private void loadFromRoom() {
        new Thread(() -> {
            List<ScheduleEntity> data = dao.getAll();
            List<Schedule> temp = new ArrayList<>();

            for (ScheduleEntity e : data) {
                temp.add(new Schedule(
                        e.getId(),
                        e.getTitle(),
                        e.getDate(),
                        e.getTime()
                ));
            }

            runOnUiThread(() -> adapter.updateData(temp));
        }).start();
    }

    // ================= SYNC API =================
    private void syncFromApi() {
        api.getSchedules().enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                new Thread(() -> {
                    dao.deleteAll(); // ⬅️ WAJIB
                    for (Schedule s : response.body()) {
                        dao.insert(new ScheduleEntity(
                                s.getId(),
                                s.getTitle(),
                                s.getDate(),
                                s.getTime()
                        ));
                    }
                    runOnUiThread(() -> loadFromRoom());
                }).start();
            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // ================= ADD =================
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Schedule");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

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
        builder.setPositiveButton("Simpan", (d, w) -> {
            if (etTitle.getText().toString().isEmpty()
                    || etDate.getText().toString().isEmpty()
                    || etTime.getText().toString().isEmpty()) {
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            addSchedule(etTitle.getText().toString(), etDate.getText().toString(), etTime.getText().toString());
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void addSchedule(String title, String date, String time) {
        api.addSchedule(new Schedule(title, date, time))
                .enqueue(new Callback<Schedule>() {
                    @Override
                    public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                        if (response.body() == null) return;
                        Schedule s = response.body();
                        new Thread(() -> {
                            dao.insert(new ScheduleEntity(
                                    s.getId(),
                                    s.getTitle(),
                                    s.getDate(),
                                    s.getTime()
                            ));
                            runOnUiThread(() -> loadFromRoom());
                        }).start();
                    }

                    @Override
                    public void onFailure(Call<Schedule> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    // ================= EDIT / DELETE =================
    private void showEditDelete(Schedule s) {
        new AlertDialog.Builder(this)
                .setItems(new String[]{"Edit", "Delete"}, (d, w) -> {
                    if (w == 0) showEditDialog(s);
                    else deleteSchedule(s.getId());
                }).show();
    }

    private void showEditDialog(Schedule s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Schedule");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etTitle = new EditText(this);
        etTitle.setText(s.getTitle());
        layout.addView(etTitle);

        EditText etDate = new EditText(this);
        etDate.setText(s.getDate());
        layout.addView(etDate);

        EditText etTime = new EditText(this);
        etTime.setText(s.getTime());
        layout.addView(etTime);

        builder.setView(layout);
        builder.setPositiveButton("Update", (d, w) ->
                updateSchedule(s.getId(),
                        etTitle.getText().toString(),
                        etDate.getText().toString(),
                        etTime.getText().toString())
        );
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void updateSchedule(int id, String title, String date, String time) {
        api.updateSchedule(id, new Schedule(id, title, date, time))
                .enqueue(new Callback<Schedule>() {
                    @Override
                    public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                        if (response.body() == null) return;
                        Schedule s = response.body();
                        new Thread(() -> {
                            dao.update(new ScheduleEntity(
                                    s.getId(),
                                    s.getTitle(),
                                    s.getDate(),
                                    s.getTime()
                            ));
                            runOnUiThread(() -> loadFromRoom());
                        }).start();
                    }

                    @Override
                    public void onFailure(Call<Schedule> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void deleteSchedule(int id) {
        api.deleteSchedule(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        dao.deleteById(id);
                        runOnUiThread(() -> loadFromRoom());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
