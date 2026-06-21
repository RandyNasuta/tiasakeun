package com.example.tiasakeun.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Activity;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private final ArrayList<Activity> activityList;

    public ActivityAdapter(ArrayList<Activity> activities) {
        this.activityList = activities;
    }

    @NonNull
    @Override
    public ActivityAdapter.ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ActivityViewHolder holder, int position) {
        Activity activity = activityList.get(position);
        holder.tVActivityTitle.setText(activity.getTitle());
        holder.tVActivityProgress.setText(R.string.example_activity_progress);
        holder.iVActivityMark.setImageResource(activity.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tVActivityTitle;
        TextView tVActivityProgress;
        ImageView iVActivityMark;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tVActivityTitle = itemView.findViewById(R.id.tVActivityTitle);
            tVActivityProgress = itemView.findViewById(R.id.tVActivityProgress);
            iVActivityMark = itemView.findViewById(R.id.iVActivityMark);
        }
    }
}
