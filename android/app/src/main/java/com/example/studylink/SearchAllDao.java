package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchDao {

    @Insert
    void insert(SearchEntity entity);

    @Query("SELECT * FROM search_items WHERE title LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    List<SearchEntity> search(String keyword);
}
