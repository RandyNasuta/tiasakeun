package com.example.tiasakeun.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.SubActivity;

import java.util.ArrayList;

public class SubActivityAdapter extends RecyclerView.Adapter<SubActivityAdapter.ViewHolder> {

    private ArrayList<SubActivity> subActivities;

    public SubActivityAdapter(ArrayList<SubActivity> subActivities) {
        this.subActivities = subActivities;
    }

    @NonNull
    @Override
    public SubActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubActivityAdapter.ViewHolder holder, int position) {
        SubActivity subActivity = subActivities.get(position);
        holder.tvSubTitle.setText(subActivity.getTitle());
        holder.tvSuProgress.setText(String.format("%s / %s %s", String.valueOf(subActivity.getCurrentValue()), String.valueOf(subActivity.getTargetValue()), subActivity.getUnitName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return subActivities == null ? 0 : subActivities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubTitle;
        TextView tvSuProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            tvSuProgress = itemView.findViewById(R.id.tvSuProgress);
        }
    }
}
