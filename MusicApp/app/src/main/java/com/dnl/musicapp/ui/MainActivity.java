package com.dnl.musicapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.dnl.musicapp.MainApp;
import com.dnl.musicapp.R;
import com.dnl.musicapp.data.PlaylistWithSongs;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private List<PlaylistWithSongs> data = new ArrayList<>();
    private DataListAdapter adapter;

    private ExpandableListView expandableListView;
    private View noRecordsContainer;

    private FloatingActionMenu menuFab;

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
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        MainApp.instance.db.playlistDao().getAll().observe(this, newRecords -> {
            data.clear();

            if (newRecords != null) {
                for (PlaylistWithSongs playlistWithSongs : newRecords) {
                    if (!playlistWithSongs.playlist.isDefault || playlistWithSongs.songs.size() > 0)
                        data.add(playlistWithSongs);
                }
            }

            adapter.notifyDataSetChanged();

            if (data.size() == 0) {
                expandableListView.setVisibility(View.GONE);
                noRecordsContainer.setVisibility(View.VISIBLE);
            } else {
                noRecordsContainer.setVisibility(View.GONE);
                expandableListView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createNewRecord(int recordType) {
        Intent intent = new Intent(this, AddRecordActivity.class);
        intent.putExtra(AddRecordActivity.RECORD_TYPE_TAG, recordType);
        startActivity(intent);
    }
}
