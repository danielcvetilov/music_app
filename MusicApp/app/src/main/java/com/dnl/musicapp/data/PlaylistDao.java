package com.dnl.musicapp.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    LiveData<List<PlaylistWithSongs>> getAll();

    @Query("SELECT * FROM playlist WHERE is_default = 1")
    Playlist getDefaultPlaylist();

    @Insert
    long insert(Playlist record);

    @Update
    void update(Playlist record);

    @Delete
    void delete(Playlist record);
}
