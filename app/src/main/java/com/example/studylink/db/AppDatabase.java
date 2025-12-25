package com.example.studylink.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.studylink.AssignmentDao;
import com.example.studylink.AssignmentEntity;

@Database(entities = {AssignmentEntity.class, ScheduleEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract AssignmentDao assignmentDao();

    public abstract ScheduleDao scheduleDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "studylink_db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

        }
        return instance;
    }
}