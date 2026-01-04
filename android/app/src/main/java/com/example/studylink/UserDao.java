package com.example.studylink;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    // Insert user, replace jika sudah ada
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    // Update user (bisa update firstName, lastName, email, photoPath)
    @Update
    void update(UserEntity user);

    // Ambil 1 user berdasarkan id
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    UserEntity getUserById(long userId);

    // Delete user berdasarkan id
    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUserById(long userId);

    // Optional: ambil semua user (misal untuk RecyclerView)
    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();
}
