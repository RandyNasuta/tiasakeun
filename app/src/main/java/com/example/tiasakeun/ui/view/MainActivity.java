package com.example.tiasakeun.ui.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.model.Schedule;
import com.example.tiasakeun.data.model.Type;
import com.example.tiasakeun.data.source.DatabaseDataSource;
import com.example.tiasakeun.ui.adapter.ActivityAdapter;
import com.example.tiasakeun.ui.picker.DatePickerFragment;
import com.example.tiasakeun.ui.picker.TimePickerFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FloatingActionButton fabAddActivity;
    private RecyclerView rVActiviy;
    private ActivityAdapter activityAdapter;
    private ArrayList<Activity> activityList = new ArrayList<>();
    private DatabaseDataSource databaseDataSource = null;

    private void initView() {
        fabAddActivity = findViewById(R.id.fabAddActivity);

        //RecyclerView
        rVActiviy = findViewById(R.id.rVActiviy);
        rVActiviy.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        databaseDataSource.open();
        activityList.addAll(databaseDataSource.getAllActivities());
        databaseDataSource.close();

        activityAdapter = new ActivityAdapter(activityList);
        rVActiviy.setAdapter(activityAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inisialisasi database
        databaseDataSource = new DatabaseDataSource(this);
        initView();

        refreshDataAct();

        buttonClick();
    }

    private void refreshDataAct() {
        if (databaseDataSource != null) {
            databaseDataSource.open();
            activityList.clear();
            activityList.addAll(databaseDataSource.getAllActivities());
            databaseDataSource.close();

            if (activityAdapter != null) {
                activityAdapter.notifyDataSetChanged();
            }
        }
    }

    private void buttonClick() {
        fabAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCreateActivity();
            }
        });
    }

    private void showDialogCreateActivity() {
        //Variabel
        final Type[] selectedType = {null};
        final Schedule[] selectedSchedule = {null};
        final int[] sYear = {0}, sMonth = {0}, sDay = {0}, sHour = {0}, sMinute = {0};

        //Panggil DatabaseDataSource
        databaseDataSource.open();

        ArrayList<Type> typesUnits = databaseDataSource.getAllTypes();
        ArrayList<Schedule> scheduleTypes = databaseDataSource.getAllSchedules();

        databaseDataSource.close();

        //Builder untuk AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Inflate / cetak layout XML dialog yang sudah dibuat
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_activity, null);
        builder.setView(dialogView);

        //Inisialisasi view
        TextInputEditText etActivityTitle = dialogView.findViewById(R.id.etActivityTitle);
        MaterialAutoCompleteTextView spinnerUnit = dialogView.findViewById(R.id.spinnerUnit);
        TextInputEditText etTotal = dialogView.findViewById(R.id.etTotal);
        MaterialButton btnTotalMinus = dialogView.findViewById(R.id.btnTotalMinus);
        MaterialButton btnTotalPlus = dialogView.findViewById(R.id.btnTotalPlus);
        TextInputLayout spinnerScheduleLayout = dialogView.findViewById(R.id.spinnerScheduleLayout);
        MaterialAutoCompleteTextView spinnerSchedule = dialogView.findViewById(R.id.spinnerSchedule);
        MaterialButton btnCreateActivity = dialogView.findViewById(R.id.btnCreateActivity);
        MaterialCheckBox cbSchedule = dialogView.findViewById(R.id.cbSchedule);
        MaterialButton btnDateActivity = dialogView.findViewById(R.id.btnDateActivity);
        MaterialButton btnTimeActivity = dialogView.findViewById(R.id.btnTimeActivity);

        ArrayAdapter<Type> unitAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, typesUnits);
        ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scheduleTypes);

        spinnerUnit.setAdapter(unitAdapter);
        spinnerSchedule.setAdapter(scheduleAdapter);

        //Buat objeck dialog
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        spinnerScheduleLayout.setVisibility(GONE);
        btnTimeActivity.setVisibility(GONE);
        btnDateActivity.setVisibility(GONE);

        dialog.show();

        //Jika satuannya dipilih, maka ubah nilai unit ke default
        spinnerUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType[0] = (Type) adapterView.getItemAtPosition(i);
            }
        });

        cbSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
                if (b) {
                    spinnerScheduleLayout.setVisibility(VISIBLE);
                    btnTimeActivity.setVisibility(VISIBLE);
                    btnDateActivity.setVisibility(VISIBLE);

                } else {
                    spinnerScheduleLayout.setVisibility(GONE);
                    btnTimeActivity.setVisibility(GONE);
                    btnDateActivity.setVisibility(GONE);

                    selectedSchedule[0] = null;
                    sYear[0] = 0;
                    sMonth[0] = 0;
                    sDay[0] = 0;
                    sHour[0] = 0;
                    sMinute[0] = 0;
                    btnTimeActivity.setText(R.string.set_time);
                    btnDateActivity.setText(R.string.set_date);
                }
            }
        });

        spinnerSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSchedule[0] = (Schedule) adapterView.getItemAtPosition(i);
            }
        });

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

        btnTotalPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etTotal.getText().toString().trim();
                int currValue = text.isEmpty() ? 0 : Integer.valueOf(etTotal.getText().toString());

                currValue++;
                etTotal.setText(String.valueOf(currValue));
            }
        });

        btnTimeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment fragment = new TimePickerFragment();

                fragment.setTimePickerListener(new TimePickerFragment.TimePickerListener() {
                    @Override
                    public void onTimeSelected(int hour, int minute) {
                        sHour[0] = hour;
                        sMinute[0] = minute;
                        btnTimeActivity.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                    }
                });

                fragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });

        btnDateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();

                fragment.setDatePickerListener(new DatePickerFragment.DatePickerListener() {
                    @Override
                    public void onDateSelected(int day, int month, int year) {
                        sYear[0] = year;
                        sMonth[0] = month;
                        sDay[0] = day;
                        btnDateActivity.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month+1, year));
                    }
                });

                fragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        //Tombol untuk membuat activity
        btnCreateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validasi semua input data yang penting harus di isi
                //Jika field Judul, tipe, nilai, dan satuannya kosong, maka munculkan error
                String title = etActivityTitle.getText().toString().trim();
                String total = etTotal.getText().toString().trim();

                boolean isScheduleChecked = cbSchedule.isChecked();
                boolean isScheduleValid = !isScheduleChecked || (selectedSchedule[0] != null && sYear[0] != 0 && sHour[0] != 0);

                if (title.isEmpty() || total.isEmpty() || selectedType[0] == null || selectedSchedule[0] == null || !isScheduleValid) {
                    if (title.isEmpty()) {
                        etActivityTitle.setError("Tidak boleh kosong");
                    }
                    Toast.makeText(MainActivity.this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
                    return;
                }

                String formattedDateTimeActivity;

                if (isScheduleChecked) {
                    formattedDateTimeActivity = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00",
                            sYear[0], sMonth[0] + 1, sDay[0], sHour[0], sMinute[0]);
                } else {
                    Calendar current = Calendar.getInstance();
                    formattedDateTimeActivity = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00",
                            current.get(Calendar.YEAR), current.get(Calendar.MONTH) + 1, current.get(Calendar.DAY_OF_MONTH),
                            current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE));
                }

                databaseDataSource.open();

                try {
                    boolean schduleChecked = cbSchedule.isChecked();
                    long result = databaseDataSource.createActivity(
                            1,
                            selectedType[0].getId(),
                            schduleChecked ? selectedSchedule[0].getId() : null,
                            formattedDateTimeActivity,
                            title,
                            0,
                            Integer.parseInt(etTotal.getText().toString()),
                            schduleChecked ? 1 : 0,
                            R.drawable.lari
                     );

                    if (result != -1) {
                        Toast.makeText(MainActivity.this, R.string.create_data_succesfully, Toast.LENGTH_SHORT).show();

                        refreshDataAct();

                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.failed_to_save_the_data, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    databaseDataSource.close();
                }
            }
        });
    }
}