package com.dnl.musicapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dnl.musicapp.MainApp;
import com.dnl.musicapp.R;
import com.dnl.musicapp.data.AppDatabase;
import com.dnl.musicapp.data.Playlist;
import com.dnl.musicapp.data.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

public class AddRecordActivity extends AppCompatActivity {

    public static final String RECORD_NAME_TAG = "record_name";
    public static final String RECORD_TYPE_TAG = "record_type";

    private TextView titleTv;
    private EditText nameEt;
    private RadioGroup recordTypeRg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        titleTv = findViewById(R.id.title_tv);
        nameEt = findViewById(R.id.name_et);
        recordTypeRg = findViewById(R.id.record_type_rg);

        View saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(view -> saveRecord());

        titleTv.setText(R.string.generic_add_record_title);

        recordTypeRg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.playlist_rb:
                    titleTv.setText(R.string.playlist_add_record_title);
                    break;
                case R.id.song_rb:
                    titleTv.setText(R.string.song_add_record_title);
                    break;
            }
        });

        boolean hasParams;
        if (savedInstanceState != null)
            hasParams = InitBySavedInstance(savedInstanceState);
        else
            hasParams = InitByNewInstance();

        if (!hasParams) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void saveRecord() {
        String name = nameEt.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase db = MainApp.instance.db;
        switch (recordTypeRg.getCheckedRadioButtonId()) {
            case R.id.playlist_rb:
                saveAsPlaylist(db, name);
                break;
            case R.id.song_rb:
                saveAsSong(db, name);
                break;
            default:
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_LONG).show();
                return;
        }

        Toast.makeText(this, R.string.record_success, Toast.LENGTH_LONG).show();
        finish();
    }

    private void saveAsSong(AppDatabase db, String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Playlist defaultPlaylist = db.getDefaultPlaylistOrCreateIfNeeded();

            Song song = createSong(name, defaultPlaylist.id);
            db.saveSong(song);
        });
    }

    private void saveAsPlaylist(AppDatabase db, String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Playlist playlist = new Playlist();
            playlist.name = name;

            db.savePlaylist(playlist);

            List<Song> songs = createSongsForPlaylist(playlist);
            db.saveSongs(songs);
        });
    }

    private List<Song> createSongsForPlaylist(Playlist playlist) {
        Random random = new Random();
        int numberOfSongs = random.nextInt(10) + 1;

        List<Song> songsToSave = new ArrayList<>();
        for (int i = 1; i <= numberOfSongs; i++) {
            String name = String.format(getString(R.string.playlist_song_template), i);
            Song song = createSong(name, playlist.id);
            songsToSave.add(song);
        }

        return songsToSave;
    }

    private Song createSong(String name, long playlistId) {
        Song song = new Song();
        song.name = name;
        song.playlistId = playlistId;

        return song;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(RECORD_NAME_TAG, nameEt.getText());

        switch (recordTypeRg.getCheckedRadioButtonId()) {
            case R.id.playlist_rb:
                outState.putInt(RECORD_TYPE_TAG, RecordType.playlist);
            case R.id.song_rb:
                outState.putInt(RECORD_TYPE_TAG, RecordType.song);
        }
    }

    private boolean InitBySavedInstance(Bundle savedInstanceState) {
        if (!savedInstanceState.containsKey(RECORD_NAME_TAG) && !savedInstanceState.containsKey(RECORD_TYPE_TAG))
            return false;

        CharSequence name = savedInstanceState.getCharSequence(RECORD_NAME_TAG);
        int recordType = savedInstanceState.getInt(RECORD_TYPE_TAG);

        nameEt.setText(name);
        switch (recordType) {
            case RecordType.playlist:
                recordTypeRg.check(R.id.playlist_rb);
            case RecordType.song:
                recordTypeRg.check(R.id.playlist_rb);
        }

        return true;
    }

    private boolean InitByNewInstance() {
        Intent intent = getIntent();
        if (intent == null)
            return false;

        if (!intent.hasExtra(RECORD_TYPE_TAG))
            return false;

        int recordType = intent.getIntExtra(RECORD_TYPE_TAG, RecordType.generic);
        switch (recordType) {
            case RecordType.playlist:
                recordTypeRg.check(R.id.playlist_rb);
                break;
            case RecordType.song:
                recordTypeRg.check(R.id.song_rb);
                break;
        }

        return true;
    }

    public static class RecordType {
        public static final int generic = 1;
        public static final int playlist = 2;
        public static final int song = 3;
    }
}
