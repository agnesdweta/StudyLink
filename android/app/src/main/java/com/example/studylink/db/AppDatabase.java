package com.example.studylink.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.studylink.AssignmentDao;
import com.example.studylink.AssignmentEntity;
import com.example.studylink.ExamDao;
import com.example.studylink.ExamEntity;
import com.example.studylink.ForumDao;
import com.example.studylink.ForumEntity;
import com.example.studylink.ScheduleDao;
import com.example.studylink.ScheduleEntity;
import com.example.studylink.CourseDao;
import com.example.studylink.CourseEntity;

@Database(
        entities = {
                AssignmentEntity.class,
                ScheduleEntity.class,
                CourseEntity.class,
                ExamEntity.class,
                ForumEntity.class
        },
        version = 12,           // ⬅️ NAIKKAN VERSION (WAJIB)
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract AssignmentDao assignmentDao();

    public abstract ScheduleDao scheduleDao();

    public abstract CourseDao courseDao();

    public abstract ExamDao examDao();
    public abstract ForumDao forumDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "studylink_db"
                            )
                            // HAPUS DB LAMA JIKA STRUKTUR BERUBAH
                            .fallbackToDestructiveMigration()

                            // BOLEH UNTUK TUGAS / SKRIPSI / UAS
                            .allowMainThreadQueries()

                            .build();
                }
            }
        }
        return instance;
    }
}
