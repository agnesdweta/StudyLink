package com.example.studylink.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "studylink.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    // 🔥 DELETE USER (LOGOUT)
    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM user");
        db.close();
    }

    public Cursor getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM user LIMIT 1", null);
    }

    public void insertUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Hapus user lama (biar 1 user login saja)
        db.execSQL("DELETE FROM user");

        // Insert user baru
        db.execSQL(
                "INSERT INTO user (name) VALUES (?)",
                new Object[]{username}
        );

        db.close();
    }
    public void updateUserName(String newName) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(
                "UPDATE user SET name = ? WHERE id = (SELECT id FROM user LIMIT 1)",
                new Object[]{newName}
        );

        db.close();
    }

    public void updateProfile(String fullName, String email) {
    }
}
