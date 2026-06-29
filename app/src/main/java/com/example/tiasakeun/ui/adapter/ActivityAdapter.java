package com.example.tiasakeun.ui.adapter;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.source.DatabaseDataSource;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private Context context;
    private final ArrayList<Activity> activityList;

    public ActivityAdapter(Context context, ArrayList<Activity> activities) {
        this.context = context;
        this.activityList = activities;
    }

    @NonNull
    @Override
    public ActivityAdapter.ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ActivityViewHolder holder, int position) {
        Activity activity = activityList.get(position);

        holder.tvMasterTitle.setText(activity.getTitle());
        holder.ivMasterIcon.setImageResource(activity.getImageResourceId());

        holder.rvSubActivities.setLayoutManager(new LinearLayoutManager(context));

        DatabaseDataSource databaseDataSource = new DatabaseDataSource(context);

        databaseDataSource.open();
        ArrayList<SubActivity> subActivities = databaseDataSource.getSubActivities(activity.getId());
        SubActivityAdapter subActivityAdapter = new SubActivityAdapter(subActivities);
        holder.rvSubActivities.setAdapter(subActivityAdapter);
        databaseDataSource.close();

        holder.layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExpanded = holder.layoutExpandable.getVisibility() == View.VISIBLE;

                AutoTransition transition = new AutoTransition();
                transition.setDuration(300);
                transition.setInterpolator(new OvershootInterpolator(1.2f));

                TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView, transition);

                if (isExpanded) {
                    holder.layoutExpandable.setVisibility(View.GONE);
                    holder.ivArrow.animate().rotation(0f).setDuration(300).start();
                } else {
                    holder.layoutExpandable.setVisibility(View.VISIBLE);
                    holder.ivArrow.animate().rotation(180f).setDuration(300).start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutHeader, layoutExpandable;
        TextView tvMasterTitle;
        ImageView ivMasterIcon, ivArrow;
        RecyclerView rvSubActivities;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            layoutExpandable = itemView.findViewById(R.id.layoutExpandable);
            tvMasterTitle = itemView.findViewById(R.id.tvMasterTitle);
            ivMasterIcon = itemView.findViewById(R.id.ivMasterIcon);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            rvSubActivities = itemView.findViewById(R.id.rvSubActivities);
        }
    }
}
