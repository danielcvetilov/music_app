package com.dnl.musicapp;

import android.app.Application;

import com.dnl.musicapp.data.AppDatabase;
import com.dnl.musicapp.data.Playlist;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Room;

public class MainApp extends Application {
    public static MainApp instance;

    public long defaultPlaylistId;

    public AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music_app.sqlite").build();

        checkDefaultRecords();
    }

    private void checkDefaultRecords() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Playlist defaultPlaylist = db.playlistDao().getDefaultPlaylist();

            if (defaultPlaylist == null) {
                defaultPlaylist = new Playlist();
                defaultPlaylist.name = "Others";
                defaultPlaylist.isDefault = true;

                db.savePlaylist(defaultPlaylist);
            }

            defaultPlaylistId = defaultPlaylist.id;
        });
    }
}
