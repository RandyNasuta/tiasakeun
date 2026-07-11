package com.example.tiasakeun.ui.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.model.Schedule;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.source.DatabaseDataSource;
import com.example.tiasakeun.ui.picker.DatePickerFragment;
import com.example.tiasakeun.ui.picker.TimePickerFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private Context context;
    private final ArrayList<Activity> activityList;
    private DatabaseDataSource databaseDataSource = null;
    private ArrayList<SubActivity> subActivities = new ArrayList<>();
    private SubActivityAdapter subActivityAdapter = null;

    public ActivityAdapter(Context context, ArrayList<Activity> activities) {
        this.context = context;
        this.activityList = activities;
        databaseDataSource = new DatabaseDataSource(context);
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

        databaseDataSource.open();
        subActivities = databaseDataSource.getSubActivitiesByActivityId(activity.getId());
        String category = databaseDataSource.getTypeCategory(activity.getId());
        databaseDataSource.close();

        subActivityAdapter = new SubActivityAdapter(subActivities, context);
        holder.rvSubActivities.setAdapter(subActivityAdapter);

        holder.btnAddSubActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Schedule[] selectedSchedule = {null};
                final int[] sYear = {0}, sMonth = {0}, sDay = {0}, sHour = {0}, sMinute = {0};
                final int[] icon = {0};

                databaseDataSource.open();
                ArrayList<Schedule> scheduleTypes = databaseDataSource.getAllSchedules();
                databaseDataSource.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_sub_activity, null);
                builder.setView(dialogView);

                TextInputEditText etSubActivityTitle = dialogView.findViewById(R.id.etSubActivityTitle);
                TextInputEditText etTotal = dialogView.findViewById(R.id.etTotal);
                MaterialButton btnTotalMinus = dialogView.findViewById(R.id.btnTotalMinus);
                MaterialButton btnTotalPlus = dialogView.findViewById(R.id.btnTotalPlus);
                TextInputLayout spinnerScheduleLayout = dialogView.findViewById(R.id.spinnerScheduleLayout);
                MaterialAutoCompleteTextView spinnerSchedule = dialogView.findViewById(R.id.spinnerSchedule);
                MaterialButton btnCreateSubActivity = dialogView.findViewById(R.id.btnCreateSubActivity);
                MaterialCheckBox cbSchedule = dialogView.findViewById(R.id.cbSchedule);
                MaterialButton btnDateSubActivity = dialogView.findViewById(R.id.btnDateSubActivity);
                MaterialButton btnTimeSubActivity = dialogView.findViewById(R.id.btnTimeSubActivity);
                LinearLayout llTargetValueQuantity = dialogView.findViewById(R.id.llTargetValueQuantity);
                LinearLayout llTargetValueTime = dialogView.findViewById(R.id.llTargetValueTime);
                TimePicker tpSpinner = dialogView.findViewById(R.id.tpSpinner);
                tpSpinner.setIs24HourView(true);
                tpSpinner.setHour(0);
                tpSpinner.setMinute(0);

                spinnerScheduleLayout.setVisibility(GONE);
                btnTimeSubActivity.setVisibility(GONE);
                btnDateSubActivity.setVisibility(GONE);

                //Atur kategori dari target aktivitas
                if (category.equals("Jumlah")) {
                    llTargetValueQuantity.setVisibility(VISIBLE);
                    llTargetValueTime.setVisibility(GONE);
                } else {
                    llTargetValueQuantity.setVisibility(GONE);
                    llTargetValueTime.setVisibility(VISIBLE);
                }

                ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<>(context.getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scheduleTypes);
                spinnerSchedule.setAdapter(scheduleAdapter);

                AlertDialog dialog = builder.create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                dialog.show();

                //Kondisi ketika checbox di checked atau unchecked
                cbSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
                        if (b) {
                            spinnerScheduleLayout.setVisibility(VISIBLE);
                            btnTimeSubActivity.setVisibility(VISIBLE);
                            btnDateSubActivity.setVisibility(VISIBLE);

                        } else {
                            spinnerScheduleLayout.setVisibility(GONE);
                            btnTimeSubActivity.setVisibility(GONE);
                            btnDateSubActivity.setVisibility(GONE);

                            selectedSchedule[0] = null;
                            sYear[0] = 0;
                            sMonth[0] = 0;
                            sDay[0] = 0;
                            sHour[0] = 0;
                            sMinute[0] = 0;
                            btnTimeSubActivity.setText(R.string.set_time);
                            btnDateSubActivity.setText(R.string.set_date);
                        }
                    }
                });

                //Spinner pemilihan penjadwalan: Harian, Bulanan, Tahunan
                spinnerSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedSchedule[0] = (Schedule) adapterView.getItemAtPosition(i);
                    }
                });

                //Tombol untuk mengurangi nilai target
                btnTotalMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = etTotal.getText().toString().trim();
                        int currValue = text.isEmpty() ? 0 : Integer.valueOf(etTotal.getText().toString());

                        if (currValue > 0) {
                            currValue--;
                            etTotal.setText(String.valueOf(currValue));
                        }
                    }
                });

                //Tombol untuk menambah nilai target
                btnTotalPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = etTotal.getText().toString().trim();
                        int currValue = text.isEmpty() ? 0 : Integer.valueOf(etTotal.getText().toString());

                        currValue++;
                        etTotal.setText(String.valueOf(currValue));
                    }
                });

                //Tombol untuk memilih jam kegiatan
                btnTimeSubActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TimePickerFragment fragment = new TimePickerFragment();

                        fragment.setTimePickerListener(new TimePickerFragment.TimePickerListener() {
                            @Override
                            public void onTimeSelected(int hour, int minute) {
                                sHour[0] = hour;
                                sMinute[0] = minute;
                                btnTimeSubActivity.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            }
                        });

                        fragment.show(((FragmentActivity)context).getSupportFragmentManager(), "TimePicker");
                    }
                });

                //Tombol untuk memilih tanggal kegiatan
                btnDateSubActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerFragment fragment = new DatePickerFragment();

                        fragment.setDatePickerListener(new DatePickerFragment.DatePickerListener() {
                            @Override
                            public void onDateSelected(int day, int month, int year) {
                                sYear[0] = year;
                                sMonth[0] = month;
                                sDay[0] = day;
                                btnDateSubActivity.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month+1, year));
                            }
                        });

                        fragment.show(((FragmentActivity)context).getSupportFragmentManager(), "DatePicker");
                    }
                });

                //Tombol untuk membuat activity
                btnCreateSubActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Validasi semua input data yang penting harus di isi
                        //Jika field Judul, tipe, nilai, dan satuannya kosong, maka munculkan error
                        String title = etSubActivityTitle.getText().toString().trim();


                        boolean isScheduleChecked = cbSchedule.isChecked();
                        boolean isScheduleValid = !isScheduleChecked || (selectedSchedule[0] != null && sYear[0] != 0 && sHour[0] != 0);

                        if (category.equals("Jumlah")) {
                            String total = etTotal.getText().toString().trim();
                            if (title.isEmpty() || total.isEmpty() || !isScheduleValid) {
                                if (title.isEmpty()) {
                                    etSubActivityTitle.setError("Tidak boleh kosong");
                                }
                                if (total.isEmpty()) {
                                    etTotal.setError("Tidak boleh kosong");
                                }
                                Toast.makeText(context.getApplicationContext(), "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            int checkTargetValue = tpSpinner.getHour() * 60 + tpSpinner.getMinute();
                            if (title.isEmpty() || checkTargetValue == 0 || !isScheduleValid) {
                                if (title.isEmpty()) {
                                    etSubActivityTitle.setError("Tidak boleh kosong");
                                }
                                Toast.makeText(context.getApplicationContext(), "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                        String formattedDateTimeActivity;

                        if (isScheduleChecked) {
                            formattedDateTimeActivity = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00",
                                    sYear[0], sMonth[0] + 1, sDay[0], sHour[0], sMinute[0]);
                        } else {
                            //Jika penjadwalan tidak di checked, maka kegiatan akan dilakukan pada hari tersebut
                            Calendar current = Calendar.getInstance();
                            formattedDateTimeActivity = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                    current.get(Calendar.YEAR), current.get(Calendar.MONTH) + 1, current.get(Calendar.DAY_OF_MONTH));
                        }

                        databaseDataSource.open();

                        try {
                            boolean scheduleChecked = cbSchedule.isChecked();
                            long result = databaseDataSource.createSubActivity(
                                    activity.getId(),
                                    scheduleChecked ? selectedSchedule[0].getId() : 0,
                                    title,
                                    category.equals("Jumlah") ? Integer.parseInt(etTotal.getText().toString()) : tpSpinner.getHour() * 60 + tpSpinner.getMinute(),
                                    formattedDateTimeActivity,
                                    scheduleChecked ? 1 : 0,
                                    0
                            );

                            if (result != -1) {
                                Toast.makeText(context.getApplicationContext(), R.string.create_data_succesfully, Toast.LENGTH_SHORT).show();

                                databaseDataSource.open();
                                ArrayList<SubActivity> newSubActivities = databaseDataSource.getSubActivitiesByActivityId(activity.getId());
                                databaseDataSource.close();

                                subActivities.clear();
                                subActivities.addAll(newSubActivities);
                                subActivityAdapter.notifyDataSetChanged();

                                dialog.dismiss();
                            } else {
                                Toast.makeText(context.getApplicationContext(), R.string.failed_to_save_the_data, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context.getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        } finally {
                            databaseDataSource.close();
                        }
                    }
                });
            }
        });

        holder.layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExpanded = holder.layoutExpandable.getVisibility() == VISIBLE;

                AutoTransition transition = new AutoTransition();
                transition.setDuration(300);
                transition.setInterpolator(new OvershootInterpolator(1.2f));

                TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView, transition);

                if (isExpanded) {
                    holder.layoutExpandable.setVisibility(GONE);
                    holder.ivArrow.animate().rotation(0f).setDuration(300).start();
                } else {
                    holder.layoutExpandable.setVisibility(VISIBLE);
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
        MaterialButton btnAddSubActivity;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            layoutExpandable = itemView.findViewById(R.id.layoutExpandable);
            tvMasterTitle = itemView.findViewById(R.id.tvMasterTitle);
            ivMasterIcon = itemView.findViewById(R.id.ivMasterIcon);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            rvSubActivities = itemView.findViewById(R.id.rvSubActivities);
            btnAddSubActivity = itemView.findViewById(R.id.btnCreateSubActivity);
        }
    }
}
