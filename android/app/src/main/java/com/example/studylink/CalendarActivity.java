package com.example.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink.api.RetrofitClient;
import com.example.studylink.api.ApiService;
import com.example.studylink.db.AppDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity
        implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Calendar selectedDate;
    private CalendarAdapter calendarAdapter;
    private ApiService apiService;
    private AgendaDialog agendaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        initWidgets();
        selectedDate = Calendar.getInstance();
        apiService = RetrofitClient.getService(); // inisialisasi Retrofit

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        calendarAdapter = new CalendarAdapter(
                new ArrayList<>(),
                new ArrayList<>(),
                this
        );
        calendarRecyclerView.setAdapter(calendarAdapter);

        setMonthView();
        syncFromServer(); // sinkron saat pertama kali buka

        // tombol kembali
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(
                        CalendarActivity.this,
                        DashboardActivity.class
                );
                startActivity(intent);
                finish();
            });
        }
    }

    private void syncFromServer() {
        apiService.getCalendarEvents().enqueue(new Callback<List<CalendarEntity>>() {
            @Override
            public void onResponse(Call<List<CalendarEntity>> call, Response<List<CalendarEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(CalendarActivity.this);
                        for (CalendarEntity e : response.body()) {
                            db.calendarDao().insert(e); // IGNORE kalau id sudah ada
                        }
                        runOnUiThread(() -> refreshCalendar());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<CalendarEntity>> call, Throwable t) {
                Toast.makeText(CalendarActivity.this, "Gagal sinkron server", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate());
        ArrayList<String> daysInMonth = daysInMonthArray();

        new Thread(() -> {
            List<CalendarEntity> allEvents =
                    AppDatabase.getInstance(this).calendarDao().getAll();

            runOnUiThread(() -> {
                calendarAdapter.updateData(daysInMonth, allEvents);
            });
        }).start();
    }

    private ArrayList<String> daysInMonthArray() {
        ArrayList<String> days = new ArrayList<>();

        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < 42; i++) {
            if (i < firstDayOfWeek || i >= daysInMonth + firstDayOfWeek) {
                days.add("");
            } else {
                days.add(String.valueOf(i - firstDayOfWeek + 1));
            }
        }
        return days;
    }

    private String monthYearFromDate() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault());
        return sdf.format(selectedDate.getTime());
    }

    public void previousMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, -1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, 1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (dayText.isEmpty()) return;

        String selectedFullDate = String.format(
                "%04d-%02d-%02d",
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH) + 1,
                Integer.parseInt(dayText)
        );

        new Thread(() -> {
            List<CalendarEntity> events = AppDatabase.getInstance(this)
                    .calendarDao().getByDate(selectedFullDate);

            runOnUiThread(() -> showAgendaDialog(selectedFullDate, events));
        }).start();
    }

    private void showAgendaDialog(String date, List<CalendarEntity> events) {
        if (isFinishing() || isDestroyed()) return;

        agendaDialog = new AgendaDialog(
                this,
                date,
                events,
                new AgendaDialog.AgendaListener() {

                    @Override
                    public void onSave(CalendarEntity event) {
                        // 1️⃣ INSERT KE API DULU
                        apiService.createCalendarEvent(event)
                                .enqueue(new Callback<CalendarEntity>() {
                                    @Override
                                    public void onResponse(Call<CalendarEntity> call, Response<CalendarEntity> response) {

                                        if (response.isSuccessful() && response.body() != null) {

                                            // 2️⃣ AMBIL ID SERVER
                                            long serverId = response.body().getId();

                                            // 3️⃣ SET ID KE OBJECT
                                            event.setId(serverId);

                                            // 4️⃣ INSERT KE ROOM (ID SUDAH BENAR)
                                            new Thread(() -> {
                                                AppDatabase.getInstance(CalendarActivity.this)
                                                        .calendarDao()
                                                        .insert(event);

                                                runOnUiThread(() -> refreshCalendar());
                                            }).start();
                                        } else {
                                            Toast.makeText(CalendarActivity.this,
                                                    "Insert server gagal", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CalendarEntity> call, Throwable t) {
                                        Toast.makeText(CalendarActivity.this,
                                                "Tidak bisa konek ke server", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }


                    @Override
                    public void onUpdate(CalendarEntity event) {
                        new Thread(() -> {
                            AppDatabase.getInstance(CalendarActivity.this)
                                    .calendarDao().update(event);
                            runOnUiThread(() -> refreshCalendar());
                            syncUpdateToServer(event);
                        }).start();
                    }

                    @Override
                    public void onDelete(CalendarEntity event) {
                        new Thread(() -> {
                            AppDatabase.getInstance(CalendarActivity.this)
                                    .calendarDao().delete(event);
                            runOnUiThread(() -> refreshCalendar());
                            syncDeleteToServer(event);
                        }).start();
                    }
                }
        );

        agendaDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (agendaDialog != null && agendaDialog.isShowing()) {
            agendaDialog.dismiss();
        }
    }



    private void syncDeleteToServer(CalendarEntity event) {
        apiService.deleteCalendarEvent(event.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        // sukses delete di server, tidak perlu action
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        runOnUiThread(() -> Toast.makeText(CalendarActivity.this,
                                "Gagal hapus di server", Toast.LENGTH_SHORT).show());
                    }
                });
    }


    private void syncUpdateToServer(CalendarEntity event) {
        apiService.updateCalendarEvent(event.getId(), event)
                .enqueue(new Callback<CalendarEntity>() {
                    @Override
                    public void onResponse(Call<CalendarEntity> call, Response<CalendarEntity> response) {
                        // sukses update, tidak perlu lakukan apa-apa
                    }

                    @Override
                    public void onFailure(Call<CalendarEntity> call, Throwable t) {
                        // gunakan CalendarActivity.this, bukan 'this'
                        runOnUiThread(() -> Toast.makeText(CalendarActivity.this,
                                "Gagal update ke server", Toast.LENGTH_SHORT).show());
                    }
                });
    }


    private void syncInsertToServer(CalendarEntity event) {
        apiService.createCalendarEvent(event).enqueue(new Callback<CalendarEntity>() {
            @Override
            public void onResponse(Call<CalendarEntity> call, Response<CalendarEntity> response) {
                // sukses insert ke server
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(CalendarActivity.this, "Gagal insert ke server", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<CalendarEntity> call, Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(CalendarActivity.this, "Gagal insert ke server", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    private void refreshCalendar() {
        new Thread(() -> {
            List<CalendarEntity> allEvents =
                    AppDatabase.getInstance(this).calendarDao().getAll();

            ArrayList<String> days = daysInMonthArray();

            runOnUiThread(() -> {
                calendarAdapter.updateData(days, allEvents);
            });
        }).start();
    }
}

