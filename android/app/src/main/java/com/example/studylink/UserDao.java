package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    UserEntity getUserById(long userId);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUserById(long userId);

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    // ⬇⬇⬇ FIX UNTUK SINKRON FOTO PROFIL ⬇⬇⬇
    @Query("UPDATE users SET photoPath = :photoPath WHERE id = :userId")
    void updatePhoto(long userId, String photoPath);
}
