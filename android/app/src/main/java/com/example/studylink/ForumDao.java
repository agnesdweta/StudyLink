package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ForumDao {
    @Query("SELECT * FROM forum ORDER BY id ASC")
    List<ForumEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ForumEntity forum);

    @Update
    void update(ForumEntity forum);

    @Delete
    void delete(ForumEntity forum);

    @Query("DELETE FROM forum")
    void deleteAll();
}
