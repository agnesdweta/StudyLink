package com.example.studylink;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        syncFromApi(); // sinkron API → Room → RecyclerView
    }

    // ================= SYNC API → ROOM =================
    private void syncFromApi() {
        api.getSchedules().enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                new Thread(() -> {
                    dao.deleteAll(); // hapus data lama
                    for (Schedule s : response.body()) {
                        dao.insert(new ScheduleEntity(s.getId(), s.getTitle(), s.getDate(), s.getTime()));
                    }
                    loadFromRoom();
                }).start();
            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // ================= LOAD FROM ROOM =================
    private void loadFromRoom() {
        new Thread(() -> {
            List<ScheduleEntity> data = dao.getAll();
            List<Schedule> temp = new ArrayList<>();
            for (ScheduleEntity e : data) {
                temp.add(new Schedule(e.getId(), e.getTitle(), e.getDate(), e.getTime()));
            }
            runOnUiThread(() -> adapter.updateData(temp));
        }).start();
    }

    // ================= ADD =================
    private void addSchedule(Schedule s) {
        api.addSchedule(s).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.body() == null) return;

                Schedule r = response.body();
                new Thread(() -> {
                    ScheduleEntity existing = dao.getById(r.getId());
                    if (existing == null) {
                        dao.insert(new ScheduleEntity(r.getId(), r.getTitle(), r.getDate(), r.getTime()));
                    } else {
                        dao.update(new ScheduleEntity(r.getId(), r.getTitle(), r.getDate(), r.getTime()));
                    }
                    loadFromRoom();
                }).start();
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // ================= UPDATE =================
    private void updateSchedule(Schedule s) {
        api.updateSchedule(s.getId(), s).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.body() == null) return;

                Schedule r = response.body();
                new Thread(() -> {
                    dao.update(new ScheduleEntity(r.getId(), r.getTitle(), r.getDate(), r.getTime()));
                    runOnUiThread(ScheduleActivity.this::loadFromRoom);
                }).start();
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    // ================= DELETE =================
    private void deleteSchedule(long id) {
        api.deleteSchedule(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        dao.deleteById(id); // Room delete
                        runOnUiThread(ScheduleActivity.this::loadFromRoom); // RecyclerView refresh
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // ================= DIALOG ADD =================
    private void showAddDialog() {
        EditText etTitle = new EditText(this);
        EditText etDate = new EditText(this);
        EditText etTime = new EditText(this);

        etDate.setFocusable(false);
        etTime.setFocusable(false);

        etTitle.setHint("Title");
        etDate.setHint("Pilih Tanggal");
        etTime.setHint("HH:mm - HH:mm");

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                        etDate.setText(displayFormat.format(selectedDate.getTime()));
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        etTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            // TimePicker pertama: jam mulai
            new TimePickerDialog(this, (view, hourStart, minuteStart) -> {
                String start = String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart);

                // TimePicker kedua: jam selesai
                new TimePickerDialog(this, (view1, hourEnd, minuteEnd) -> {
                    String end = String.format(Locale.getDefault(), "%02d:%02d", hourEnd, minuteEnd);
                    etTime.setText(start + " - " + end);
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();

            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        layout.addView(etTitle);
        layout.addView(etDate);
        layout.addView(etTime);

        new AlertDialog.Builder(this)
                .setTitle("Add Schedule")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String dateStr = etDate.getText().toString().trim();
                    String time = etTime.getText().toString().trim();

                    if (title.isEmpty() || dateStr.isEmpty() || time.isEmpty()) {
                                Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_SHORT).show();
                                return;
                    }
                    String saveDate = dateStr;
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                        Date date = displayFormat.parse(dateStr);
                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        saveDate = originalFormat.format(date);
                    } catch (Exception ignored) {}
                    // Jangan generate ID sendiri, API yang kasih ID
                    addSchedule(new Schedule(0, title, saveDate, time));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    // ================= DIALOG EDIT / DELETE =================
    private void showEditDelete(Schedule s) {
        EditText etTitle = new EditText(this);
        EditText etDate = new EditText(this);
        EditText etTime = new EditText(this);

        etTitle.setText(s.getTitle());
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = originalFormat.parse(s.getDate());
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
            etDate.setText(displayFormat.format(parsedDate));
        } catch (Exception e) {
            etDate.setText(s.getDate());
        }
        etTime.setText(s.getTime());

        etDate.setFocusable(false);
        etTime.setFocusable(false);

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                Date date = sdf.parse(etDate.getText().toString());
                c.setTime(date);
            } catch (Exception ignored) {}
            new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                        etDate.setText(displayFormat.format(selectedDate.getTime()));
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // TimePicker range (mulai - selesai)
        etTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            String[] times = etTime.getText().toString().split(" - ");
            int hStart = 0, mStart = 0;
            if (times.length > 0) {
                String[] startParts = times[0].split(":");
                hStart = Integer.parseInt(startParts[0]);
                mStart = Integer.parseInt(startParts[1]);
            }
            new TimePickerDialog(this, (view, hourStart, minuteStart) -> {
                String start = String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart);

                new TimePickerDialog(this, (view1, hourEnd, minuteEnd) -> {
                    String end = String.format(Locale.getDefault(), "%02d:%02d", hourEnd, minuteEnd);
                    etTime.setText(start + " - " + end);
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();

            }, hStart, mStart, true).show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        layout.addView(etTitle);
        layout.addView(etDate);
        layout.addView(etTime);

        new AlertDialog.Builder(this)
                .setTitle("Edit / Delete Schedule")
                .setView(layout)
                .setPositiveButton("Update", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String dateStr = etDate.getText().toString().trim();
                    String time = etTime.getText().toString().trim();
                    if (title.isEmpty() || dateStr.isEmpty() || time.isEmpty()) {
                        Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String saveDate = dateStr;
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID"));
                        Date date = displayFormat.parse(dateStr);
                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        saveDate = originalFormat.format(date);
                    } catch (Exception ignored) {}

                    updateSchedule(new Schedule(s.getId(), title, saveDate, time));
                })
                .setNegativeButton("Delete", (dialog, which) -> deleteSchedule(s.getId()))
                .setNeutralButton("Cancel", null)
                .show();
    }
}
