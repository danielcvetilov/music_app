package com.dnl.musicapp.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.dnl.musicapp.R;
import com.dnl.musicapp.data.PlaylistWithSongs;

import java.util.List;

public class DataListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<PlaylistWithSongs> data;

    public DataListAdapter(Context context, List<PlaylistWithSongs> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).songs.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return data.get(groupPosition).songs.get(childPosition).id;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        ChildViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            viewHolder = new ChildViewHolder();
            viewHolder.songTitleTv = view.findViewById(R.id.song_title_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        viewHolder.songTitleTv.setText(data.get(groupPosition).songs.get(childPosition).name);

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).songs.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return data.get(groupPosition).playlist.id;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        GroupViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_group_item, null);
            viewHolder = new GroupViewHolder();
            viewHolder.playlistTitleTv = view.findViewById(R.id.playlist_title_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
        }

        viewHolder.playlistTitleTv.setText(data.get(groupPosition).playlist.name);

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView playlistTitleTv;
    }

    static class ChildViewHolder {
        TextView songTitleTv;
    }

}