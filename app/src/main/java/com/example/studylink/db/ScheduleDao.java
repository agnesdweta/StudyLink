package com.example.studylink.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert
    void insert(ScheduleEntity schedule);

    @Update
    void update(ScheduleEntity schedule);

    @Delete
    void delete(ScheduleEntity schedule);

    @Query("SELECT * FROM schedules ORDER BY id DESC")
    List<ScheduleEntity> getAllSchedules();
}
