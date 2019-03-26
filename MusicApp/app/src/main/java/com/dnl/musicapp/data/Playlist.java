package com.dnl.musicapp.data;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist")
public class Playlist implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "is_default")
    public boolean isDefault;

    public Playlist() {
    }

    public static Playlist createDefaultRecord() {
        Playlist defaultPlaylist = new Playlist();
        defaultPlaylist.name = "Others";
        defaultPlaylist.isDefault = true;

        return defaultPlaylist;
    }

    public static final Creator CREATOR = new Creator() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    private Playlist(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.isDefault = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeByte((byte) (this.isDefault ? 1 : 0));
    }
}
