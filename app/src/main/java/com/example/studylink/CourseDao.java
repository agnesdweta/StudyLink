package com.example.studylink;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY id DESC")
    LiveData<List<CourseEntity>> getAllCoursesLive();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CourseEntity course);

    @Update
    void update(CourseEntity course);

    @Delete
    void delete(CourseEntity course);

    @Query("DELETE FROM courses")
    void deleteAll();

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    CourseEntity getById(int id);

    @Query("DELETE FROM courses WHERE id = :id")
    void deleteById(int id);
}
