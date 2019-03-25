package com.dnl.musicapp.data;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PlaylistWithSongs {
    @Embedded
    public Playlist playlist;

    @Relation(parentColumn = "id", entityColumn = "playlist_id", entity = Song.class)
    public List<Song> songs;
}