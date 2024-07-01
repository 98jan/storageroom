package com.iu.storageroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class IconAdapter extends BaseAdapter {
    private Context context;
    private int[] iconResIds;

    public IconAdapter(Context context, int[] iconResIds) {
        this.context = context;
        this.iconResIds = iconResIds;
    }

    @Override
    public int getCount() {
        return iconResIds.length;
    }

    @Override
    public Object getItem(int position) {
        return iconResIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.icon_item, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon_image);
        icon.setImageResource(iconResIds[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.icon_item, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon_image);
        icon.setImageResource(iconResIds[position]);

        return convertView;
    }
}
