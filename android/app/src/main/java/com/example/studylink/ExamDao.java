package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExamDao {

    @Query("SELECT * FROM exams ORDER BY id DESC")
    List<ExamEntity> getAll();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExamEntity exam);

    @Update
    void update(ExamEntity exam);

    @Delete
    void delete(ExamEntity exam);

    @Query("DELETE FROM exams")
    void deleteAll();
}
