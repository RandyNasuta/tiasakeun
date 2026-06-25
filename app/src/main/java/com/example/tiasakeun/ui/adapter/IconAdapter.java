package com.example.tiasakeun.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tiasakeun.R;

import java.util.ArrayList;

public class IconAdapter extends ArrayAdapter<Integer> {
    public IconAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Integer> icons) {
        super(context, 0, icons);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.icon_spinner, parent, false);
        }

        ImageView ivIconActivity = convertView.findViewById(R.id.ivIconActivity);
        int icon = getItem(position);
        ivIconActivity.setImageResource(icon);
        return convertView;
    }
}
