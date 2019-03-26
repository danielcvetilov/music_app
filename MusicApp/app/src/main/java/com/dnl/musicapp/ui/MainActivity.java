package com.dnl.musicapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.dnl.musicapp.MainApp;
import com.dnl.musicapp.R;
import com.dnl.musicapp.data.AppDatabase;
import com.dnl.musicapp.data.PlaylistWithSongs;
import com.dnl.musicapp.data.Song;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private List<PlaylistWithSongs> data = new ArrayList<>();
    private DataListAdapter adapter;

    private ExpandableListView expandableListView;
    private View noRecordsContainer;

    private FloatingActionMenu menuFab;
    private int lastExpandedGroup = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noRecordsContainer = findViewById(R.id.no_records_container);
        View addMusicBtn = findViewById(R.id.add_music_btn);
        addMusicBtn.setOnClickListener(view -> createNewRecord(AddRecordActivity.RecordType.generic));

        menuFab = findViewById(R.id.menu_fab);

        FloatingActionButton songFab = findViewById(R.id.song_fab);
        songFab.setOnClickListener(view -> {
            menuFab.close(false);
            createNewRecord(AddRecordActivity.RecordType.song);
        });

        FloatingActionButton playlistFab = findViewById(R.id.playlist_fab);
        playlistFab.setOnClickListener(view -> {
            menuFab.close(false);
            createNewRecord(AddRecordActivity.RecordType.playlist);
        });

        adapter = new DataListAdapter(this, data);

        expandableListView = findViewById(R.id.list_view);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupExpandListener(this::updateExpansionState);
        expandableListView.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, childId) -> showSong(groupPosition, childPosition));

        AppDatabase db = MainApp.instance.db;
        db.playlistDao().getAllRecords().observe(this, this::updateData);
    }

    private void updateExpansionState(int groupPosition) {
        if (groupPosition != lastExpandedGroup)
            expandableListView.collapseGroup(lastExpandedGroup);
        lastExpandedGroup = groupPosition;
    }

    private boolean showSong(int groupPosition, int childPosition) {
        if (groupPosition < 0 || groupPosition >= data.size())
            return false;

        PlaylistWithSongs playlistWithSongs = data.get(groupPosition);
        if (childPosition < 0 || childPosition >= playlistWithSongs.songs.size())
            return false;

        Song song = playlistWithSongs.songs.get(childPosition);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(playlistWithSongs.playlist.name);
        alertDialogBuilder.setMessage(song.name);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, null);

        alertDialogBuilder.show();
        return true;
    }

    private void updateData(List<PlaylistWithSongs> newRecords) {
        data.clear();

        if (newRecords != null && newRecords.size() > 0)
            data.addAll(newRecords);

        adapter.notifyDataSetChanged();
        showNoRecordsLabelIfNeeded();
    }

    private void showNoRecordsLabelIfNeeded() {
        if (data.size() == 0) {
            expandableListView.setVisibility(View.GONE);
            noRecordsContainer.setVisibility(View.VISIBLE);
        } else {
            noRecordsContainer.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
        }
    }

    private void createNewRecord(int recordType) {
        Intent intent = new Intent(this, AddRecordActivity.class);
        intent.putExtra(AddRecordActivity.RECORD_TYPE_TAG, recordType);
        startActivity(intent);
    }
}
