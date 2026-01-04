package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AssignmentEntity assignment);

    @Update
    void update(AssignmentEntity assignment);

    @Delete
    void delete(AssignmentEntity assignment);

    @Query("DELETE FROM assignments")
    void deleteAll();   // 🔥 TAMBAHKAN INI

    @Query("SELECT * FROM assignments ORDER BY deadline ASC")
    List<AssignmentEntity> getAll();
}
