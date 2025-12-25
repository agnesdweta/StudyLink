package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PostDao {

    @Insert
    void insert(PostEntity post);

    @Insert
    void insertAll(List<PostEntity> posts);

    @Query("SELECT * FROM post ORDER BY id DESC")
    List<PostEntity> getAll();

    @Update
    void update(PostEntity post);

    @Delete
    void delete(PostEntity post);
}
