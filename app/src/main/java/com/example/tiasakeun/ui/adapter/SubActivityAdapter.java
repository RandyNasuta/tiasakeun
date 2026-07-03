package com.example.tiasakeun.ui.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Schedule;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.source.DatabaseDataSource;
import com.example.tiasakeun.ui.picker.DatePickerFragment;
import com.example.tiasakeun.ui.picker.TimePickerFragment;
import com.example.tiasakeun.ui.view.DetailActActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SubActivityAdapter extends RecyclerView.Adapter<SubActivityAdapter.ViewHolder> {

    private ArrayList<SubActivity> subActivities;
    private DatabaseDataSource databaseDataSource = null;
    private final String TAG = "SubActivityAdapter";
    private Context context;

    public SubActivityAdapter(ArrayList<SubActivity> subActivities, Context context) {
        this.subActivities = subActivities;
        this.context = context;
        databaseDataSource = new DatabaseDataSource(this.context);
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
        holder.tvSubProgress.setText(String.format("%s / %s %s", String.valueOf(subActivity.getCurrentValue()), String.valueOf(subActivity.getTargetValue()), subActivity.getUnitName()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Schedule[] selectedSchedule = {null};
                final int[] sYear = {0}, sMonth = {0}, sDay = {0}, sHour = {0}, sMinute = {0};

                databaseDataSource.open();
                ArrayList<Schedule> scheduleTypes = databaseDataSource.getAllSchedules();
                databaseDataSource.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_add_sub_activity, null);
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

                ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<>(context.getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scheduleTypes);
                spinnerSchedule.setAdapter(scheduleAdapter);

                etSubActivityTitle.setText(subActivity.getTitle());
                etTotal.setText(String.valueOf(subActivity.getTargetValue()));

                //Ubah format tanggal di db dengan tanggal di UI
                //Define format asalnya
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

                //Define format yang diinginkan
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                try {
                    /**
                     * Alur logika
                     * 1. Gunakan var inputFormat dan masukkan sumber data
                     * 2. Gunakan var outputFormat untuk mengubah format
                     * 3. Split string menjadi array string dengan spasi
                     */
                    String[] dateFormat = outputFormat.format((Date)Objects.requireNonNull(inputFormat.parse(subActivity.getDateActivity()))).split(" ");
                    btnDateSubActivity.setText(dateFormat[0]);
                    btnTimeSubActivity.setText(dateFormat[1]);

                    if (subActivity.getNotification() == 1) {
                        cbSchedule.setChecked(true);
                        databaseDataSource.open();
                        selectedSchedule[0] = (Schedule) databaseDataSource.getScheduleByType(subActivity.getScheduleType());
                        databaseDataSource.close();
                        sYear[0] = Integer.parseInt(dateFormat[0].split("/")[2]);
                        sHour[0] = Integer.parseInt(dateFormat[1].split(":")[0]);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "onLongClick: Error pergantian format tanggal: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                spinnerSchedule.setText(subActivity.getScheduleType());
                btnCreateSubActivity.setText(R.string.update);

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

                //Tombol untuk update sub activity
                btnCreateSubActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Validasi semua input data yang penting harus di isi
                        //Jika field Judul, tipe, nilai, dan satuannya kosong, maka munculkan error
                        String title = etSubActivityTitle.getText().toString().trim();
                        String total = etTotal.getText().toString().trim();

                        boolean isScheduleChecked = cbSchedule.isChecked();
                        boolean isScheduleValid = !isScheduleChecked || (selectedSchedule[0] != null && sYear[0] != 0 && sHour[0] != 0);

                        if (title.isEmpty() || total.isEmpty() || !isScheduleValid) {
                            Log.e(TAG, "validasi: title is empty: " + title.isEmpty());
                            Log.e(TAG, "validasi: total is empty: " + total.isEmpty());
                            Log.e(TAG, "validasi: schedule is not valid: " + !isScheduleValid);

                            if (title.isEmpty()) {
                                etSubActivityTitle.setError("Tidak boleh kosong");
                            } else if (total.isEmpty()) {
                                etTotal.setError("Tidak boleh kosong");
                            }
                            Toast.makeText(context.getApplicationContext(), "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
                            return;
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
                            long result = databaseDataSource.updateSubActivity(
                                    subActivity.getId(),
                                    scheduleChecked ? selectedSchedule[0].getId() : 0,
                                    title,
                                    Integer.parseInt(etTotal.getText().toString()),
                                    formattedDateTimeActivity,
                                    scheduleChecked ? 1 : 0
                            );

                            if (result != -1) {
                                Toast.makeText(context.getApplicationContext(), R.string.create_data_succesfully, Toast.LENGTH_SHORT).show();

                                subActivities.clear();
                                subActivities.addAll(databaseDataSource.getSubActivitiesByActivityId(subActivity.getActivityId()));
                                notifyDataSetChanged();

                                dialog.dismiss();
                            } else {
                                Toast.makeText(context.getApplicationContext(), R.string.failed_to_save_the_data, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "error update sub activity: " + e.getMessage());
                        } finally {
                            databaseDataSource.close();
                        }
                    }
                });
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActActivity.class);
                intent.putExtra("SUB_ACTIVITY_ID", subActivity.getId());
                intent.putExtra("ACTIVITY_ID", subActivity.getActivityId());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subActivities == null ? 0 : subActivities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubTitle;
        TextView tvSubProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            tvSubProgress = itemView.findViewById(R.id.tvSubProgress);
        }
    }
}
