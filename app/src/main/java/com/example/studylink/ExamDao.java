package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExamDao {

    @Insert
    void insert(ExamEntity exam);

    @Update
    void update(ExamEntity exam);

    @Delete
    void delete(ExamEntity exam);

    @Query("SELECT * FROM ExamEntity")
    List<ExamEntity> getAll();
}
