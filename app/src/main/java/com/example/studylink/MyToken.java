package com.example.studylink;

import android.content.Context;
import android.content.SharedPreferences;

public class MyToken {

    private static final String PREFS_NAME = "my_prefs";
    private static final String KEY_TOKEN = "token";

    // Simpan token
    public static void save(Context context, String token){
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, token).apply();
    }

    // Ambil token
    public static String get(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TOKEN, ""); // default kosong kalau belum ada
    }

    // Hapus token (logout)
    public static void clear(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(KEY_TOKEN).apply();
    }
}
