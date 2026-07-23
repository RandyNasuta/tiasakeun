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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SubActivityReportAdapter extends RecyclerView.Adapter<SubActivityReportAdapter.SubActivityReportViewHolder> {

    private ArrayList<SubActivityLog> subActivityLogs;
    private Context context;

    public SubActivityReportAdapter(ArrayList<SubActivityLog> subActivityLogs, Context context) {
        this.subActivityLogs = subActivityLogs;
        this.context = context;
    }

    @NonNull
    @Override
    public SubActivityReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_activity_report, parent, false);
        return new SubActivityReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubActivityReportViewHolder holder, int position) {
        SubActivityLog subActivityLog = subActivityLogs.get(position);

        //Atur format date string yang diterima
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        //Ubah ke obyek LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(subActivityLog.getLogDate(), inputFormatter);

        //Atur format yang diinginkan
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault());

        holder.tvSubActivity.setText(subActivityLog.getSubActivityName());

        String value = subActivityLog.getValue().toString();
        if (subActivityLog.getCategoryType().equals("Waktu")) {
            value = String.valueOf(subActivityLog.getValue() / 60L) ;
        }
        holder.tvSubActivityValue.setText("[ "+value + " " + subActivityLog.getUnitName() + " ]");
        holder.tvSubActivityDate.setText(dateTime.format(outputFormatter));
    }

    @Override
    public int getItemCount() {
        return subActivityLogs.size();
    }

    public static class SubActivityReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubActivity;
        TextView tvSubActivityValue;
        TextView tvSubActivityDate;

        public SubActivityReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubActivity = itemView.findViewById(R.id.tvSubActivity);
            tvSubActivityValue = itemView.findViewById(R.id.tvSubActivityValue);
            tvSubActivityDate = itemView.findViewById(R.id.tvSubActivityDate);
        }
    }
}
