package com.example.tiasakeun.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.SubActivityLog;

import java.util.ArrayList;

public class SubActivityReportAdapter extends RecyclerView.Adapter<SubActivityReportAdapter.ViewHolder> {

    private ArrayList<SubActivityLog> subActivityLogs;
    private Context context;

    public SubActivityReportAdapter(ArrayList<SubActivityLog> subActivityLogs, Context context) {
        this.subActivityLogs = subActivityLogs;
        this.context = context;
    }

    @NonNull
    @Override
    public SubActivityReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_activity_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubActivityReportAdapter.ViewHolder holder, int position) {
        SubActivityLog subActivityLog = subActivityLogs.get(position);

        holder.tvSubActivity.setText(subActivityLog.getSubActivityName());
        holder.tvSubActivityValue.setText(subActivityLog.getValue().toString());
        holder.tvSubActivityDate.setText(subActivityLog.getLogDate());
    }

    @Override
    public int getItemCount() {
        return subActivityLogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubActivity;
        TextView tvSubActivityValue;
        TextView tvSubActivityDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubActivity = itemView.findViewById(R.id.tvSubActivity);
            tvSubActivityValue = itemView.findViewById(R.id.tvSubActivityValue);
            tvSubActivityDate = itemView.findViewById(R.id.tvSubActivityDate);
        }
    }
}
