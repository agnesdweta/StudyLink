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

    // 🔹 AMBIL SEMUA DATA
    @Query("SELECT * FROM assignments")
    List<AssignmentEntity> getAll();

    // 🔹 AMBIL 1 DATA BERDASARKAN ID (OPSIONAL)
    @Query("SELECT * FROM assignments WHERE id = :id LIMIT 1")
    AssignmentEntity getById(int id);

    // 🔹 INSERT / SIMPAN DATA
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AssignmentEntity assignment);

    // 🔹 INSERT BANYAK DATA (DARI API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AssignmentEntity> data);

    // 🔹 UPDATE DATA
    @Update
    void update(AssignmentEntity assignment);

    // 🔹 DELETE 1 DATA
    @Delete
    void delete(AssignmentEntity assignment);

    // 🔹 HAPUS SEMUA DATA
    @Query("DELETE FROM assignments")
    void deleteAll();
}
