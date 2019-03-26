package com.dnl.musicapp;

import android.app.Application;

import com.dnl.musicapp.data.AppDatabase;

import androidx.room.Room;

public class MainApp extends Application {
    public static MainApp instance;

    public AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music_app.sqlite").build();
    }
}
