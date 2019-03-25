package com.dnl.musicapp.data;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Playlist.class, Song.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlaylistDao playlistDao();

    public abstract SongDao songDao();

    public void savePlaylist(Playlist record) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (record.id == 0)
                record.id = playlistDao().insert(record);
            else
                playlistDao().update(record);
        });
    }

    public void saveSong(Song record) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (record.id == 0)
                record.id = songDao().insert(record);
            else
                songDao().update(record);
        });
    }

    public void saveSongs(List<Song> records) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            for (Song record : records) {
                if (record.id == 0)
                    record.id = songDao().insert(record);
                else
                    songDao().update(record);
            }
        });
    }
}




