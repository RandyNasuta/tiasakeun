package com.example.tiasakeun.ui.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.example.tiasakeun.ui.adapter.IconAdapter;
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
import java.util.List;
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
        final int[] icon = {0};

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
        MaterialButton btnCreateActivity = dialogView.findViewById(R.id.btnCreateActivity);
        MaterialAutoCompleteTextView spinnerIcon = dialogView.findViewById(R.id.spinnerIcon);

        ArrayAdapter<Type> unitAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, typesUnits);
        ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scheduleTypes);

        IconAdapter iconAdapter = new IconAdapter(this, R.layout.icon_spinner, new ArrayList<>(
                List.of(R.drawable.lari,
                        R.drawable.air_putih,
                        R.drawable.berenang,
                        R.drawable.buku,
                        R.drawable.meditation)
        ));

        spinnerUnit.setAdapter(unitAdapter);
        spinnerIcon.setAdapter(iconAdapter);

        //Buat objeck dialog
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        //Jika satuannya dipilih, maka ubah nilai unit ke default
        spinnerUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType[0] = (Type) adapterView.getItemAtPosition(i);
            }
        });

        //Spinner pemilihan icon kegiatan
        spinnerIcon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                icon[0] = (int) adapterView.getItemAtPosition(i);

                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, icon[0]);

                if (drawable != null) {
                    int size = (int) (32 * getResources().getDisplayMetrics().density);
                    drawable.setBounds(0, 0, size, size);
                    spinnerIcon.setCompoundDrawablesRelative(drawable, null, null, null);
                }

                spinnerIcon.setText("");
            }
        });

        //Tombol untuk membuat activity
        btnCreateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validasi semua input data yang penting harus di isi
                //Jika field Judul, tipe, nilai, dan satuannya kosong, maka munculkan error
                String title = etActivityTitle.getText().toString().trim();

                if (title.isEmpty() || selectedType[0] == null ) {
                    if (title.isEmpty()) {
                        etActivityTitle.setError("Tidak boleh kosong");
                    }
                    Log.i(TAG, "title " + title.isEmpty());
                    Log.i(TAG, "type " + selectedType[0]);
                    Log.i(TAG, "schedule " + selectedSchedule[0]);
                    Toast.makeText(MainActivity.this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseDataSource.open();

                try {
                    long result = databaseDataSource.createActivity(
                            1,
                            selectedType[0].getId(),
                            title,
                            icon[0]
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