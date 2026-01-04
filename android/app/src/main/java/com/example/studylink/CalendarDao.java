package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalendarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CalendarEntity event);

    @Update
    void update(CalendarEntity event);

    @Delete
    void delete(CalendarEntity event);

    @Query("SELECT * FROM calendar")
    List<CalendarEntity> getAll();

    @Query("SELECT * FROM calendar WHERE date = :date")
    List<CalendarEntity> getByDate(String date);
}
