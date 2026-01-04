package com.example.studylink;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.studylink.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class AgendaDialog extends Dialog {

    // ================= LISTENER =================
    public interface AgendaListener {
        void onSave(CalendarEntity event);
        void onUpdate(CalendarEntity event);
        void onDelete(CalendarEntity event);
    }

    private final Activity activity; // ✅ SIMPAN ACTIVITY
    private final AgendaListener listener;
    private final String date;

    private EditText edtTitle, edtDescription, edtUser;
    private Button btnAdd, btnClose;
    private ListView lvAgenda;

    private ArrayAdapter<String> adapter;
    private final ArrayList<CalendarEntity> agendaList = new ArrayList<>();

    // ================= CONSTRUCTOR =================
    public AgendaDialog(
            @NonNull Activity activity,
            String date,
            List<CalendarEntity> events,
            AgendaListener listener
    ) {
        super(activity);
        this.activity = activity;
        this.date = date;
        this.listener = listener;

        for (CalendarEntity e : events) {
            if (date.equals(e.getDate())) {
                agendaList.add(e);
            }
        }
    }

    // ================= ON CREATE =================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agenda);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtUser = findViewById(R.id.edtUser);
        btnAdd = findViewById(R.id.btnAdd);
        btnClose = findViewById(R.id.btnClose);
        lvAgenda = findViewById(R.id.lvAgenda);

        adapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_list_item_1,
                getTitles(agendaList)
        );
        lvAgenda.setAdapter(adapter);

        // ================= TAMBAH AGENDA =================
        btnAdd.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();
            String user = edtUser.getText().toString().trim();

            if (title.isEmpty() || user.isEmpty()) return;

            CalendarEntity event = new CalendarEntity(date, title, desc, user);
            event.setId(System.currentTimeMillis());

            listener.onSave(event);
            reloadFromRoom();

            edtTitle.setText("");
            edtDescription.setText("");
            edtUser.setText("");
        });

        btnClose.setOnClickListener(v -> dismiss());

        // ================= CLICK → PINDAH MENU =================
        lvAgenda.setOnItemClickListener((parent, view, position, id) -> {
            CalendarEntity event = agendaList.get(position);

            Intent intent;
            String title = event.getTitle().toLowerCase();

            if (title.contains("exam")) {
                intent = new Intent(activity, ExamActivity.class);
            } else if (title.contains("forum")) {
                intent = new Intent(activity, ForumActivity.class);
            } else {
                intent = new Intent(activity, ScheduleActivity.class);
            }

            intent.putExtra("date", event.getDate());
            activity.startActivity(intent);
        });

        // ================= LONG CLICK =================
        lvAgenda.setOnItemLongClickListener((parent, view, position, id) -> {
            CalendarEntity event = agendaList.get(position);

            String[] options = {"Edit", "Hapus"};
            new AlertDialog.Builder(activity)
                    .setTitle("Pilih Aksi")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            showEditDialog(event);
                        } else {
                            listener.onDelete(event);
                            reloadFromRoom();
                        }
                    })
                    .show();
            return true;
        });
    }

    // ================= EDIT DIALOG =================
    private void showEditDialog(CalendarEntity event) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_agenda_edit, null);

        EditText edtEditTitle = view.findViewById(R.id.edtEditTitle);
        EditText edtEditDesc = view.findViewById(R.id.edtEditDescription);
        EditText edtEditUser = view.findViewById(R.id.edtEditUser);

        edtEditTitle.setText(event.getTitle());
        edtEditDesc.setText(event.getDescription());
        edtEditUser.setText(event.getUser());

        new AlertDialog.Builder(activity)
                .setTitle("Edit Agenda")
                .setView(view)
                .setPositiveButton("Simpan", (d, w) -> {
                    event.setTitle(edtEditTitle.getText().toString());
                    event.setDescription(edtEditDesc.getText().toString());
                    event.setUser(edtEditUser.getText().toString());

                    listener.onUpdate(event);
                    reloadFromRoom();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // ================= RELOAD ROOM (FIXED) =================
    private void reloadFromRoom() {
        new Thread(() -> {
            List<CalendarEntity> fresh =
                    AppDatabase.getInstance(activity)
                            .calendarDao()
                            .getByDate(date);

            agendaList.clear();
            agendaList.addAll(fresh);

            if (activity.isFinishing() || activity.isDestroyed()) return;

            activity.runOnUiThread(() -> {
                adapter.clear();
                for (CalendarEntity e : agendaList) {
                    adapter.add(e.getTitle());
                }
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    // ================= HELPER =================
    private List<String> getTitles(List<CalendarEntity> list) {
        List<String> titles = new ArrayList<>();
        for (CalendarEntity e : list) {
            titles.add(e.getTitle());
        }
        return titles;
    }
}
