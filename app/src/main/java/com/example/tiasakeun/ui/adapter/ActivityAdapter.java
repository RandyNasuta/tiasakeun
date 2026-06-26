package com.example.tiasakeun.ui.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.source.DatabaseDataSource;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private final ArrayList<Activity> activityList;
    private final String TAG = "activity_adapter";

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
        holder.tVActivityProgress.setText(activity.getCurrentValue() + " / " + activity.getTargetValue() + " " + activity.getUnitName());
        holder.iVActivityMark.setImageResource(activity.getImageResourceId());

        holder.cbActivity.setOnCheckedChangeListener(null);
        holder.cbActivity.setChecked(false);
        holder.cbActivity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle(R.string.completion_activity)
                            .setMessage("Apakah Anda yakin untuk menyelesaikan kegiatan ini?")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int currentPosition = holder.getAdapterPosition();

                                    if (currentPosition == RecyclerView.NO_POSITION) return;

                                    DatabaseDataSource databaseDataSource = new DatabaseDataSource(holder.itemView.getContext());

                                    try {
                                        databaseDataSource.open();

                                        long result = databaseDataSource.createActivityLog(
                                                activity.getId(),
                                                activity.getCurrentValue(),
                                                activity.getDateActivity()
                                        );

                                        long result2 = databaseDataSource.updateActivityCompleted(true, activity.getId());

                                        if (result != -1 && result2 != -1) {
                                            Toast.makeText(holder.itemView.getContext(), "Data aktivitas selesai", Toast.LENGTH_SHORT).show();

                                            activityList.remove(currentPosition);
                                            notifyItemRemoved(currentPosition);
                                            notifyItemRangeChanged(currentPosition, activityList.size());
                                            notifyItemChanged(holder.getAdapterPosition());
                                        } else {
                                            Toast.makeText(holder.itemView.getContext(), "Gagal mengubah data", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        Log.e(TAG, "onClick: " + e.getMessage());
                                        Toast.makeText(holder.itemView.getContext(), "Gagal mengubah data", Toast.LENGTH_SHORT).show();
                                    } finally {
                                        databaseDataSource.close();
                                    }
                                }
                            })
                            .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    notifyItemChanged(holder.getAdapterPosition());
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tVActivityTitle;
        TextView tVActivityProgress;
        ImageView iVActivityMark;
        MaterialCheckBox cbActivity;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tVActivityTitle = itemView.findViewById(R.id.tVActivityTitle);
            tVActivityProgress = itemView.findViewById(R.id.tVActivityProgress);
            iVActivityMark = itemView.findViewById(R.id.iVActivityMark);
            cbActivity = itemView.findViewById(R.id.cbActivity);
        }
    }
}
