package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM schedules ORDER BY id DESC")
    List<ScheduleEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScheduleEntity schedule);

    @Update
    void update(ScheduleEntity schedule);

    @Query("DELETE FROM schedules WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM schedules")
    void deleteAll();
}

