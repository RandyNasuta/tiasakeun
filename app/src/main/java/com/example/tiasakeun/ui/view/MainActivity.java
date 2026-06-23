package com.example.tiasakeun.ui.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAddActivity;
    private RecyclerView rVActiviy;
    private ActivityAdapter activityAdapter;
    private ArrayList<Activity> activityList = new ArrayList<>();

    private void initView() {
        fabAddActivity = findViewById(R.id.fabAddActivity);
        rVActiviy = findViewById(R.id.rVActiviy);
        rVActiviy.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

        initView();
        buttonClick();
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
        //Panggil DatabaseDataSource
        DatabaseDataSource databaseDataSource = new DatabaseDataSource(this);
        databaseDataSource.open();

        ArrayList<String> typeCategories = new ArrayList<>();
        ArrayList<String> typesUnits = new ArrayList<>();
        ArrayList<String> scheduleTypes = new ArrayList<>();

        String unique = "";
        for (Type type : databaseDataSource.getAllTypes()) {
            if (!unique.contains(type.getCategory())){
                typeCategories.add(type.getCategory());
                unique = unique + type.getCategory() + ",";
            }
            typesUnits.add(type.getUnitName());
        }

        for (Schedule schedule : databaseDataSource.getAllSchedules())  {
            scheduleTypes.add(schedule.getType());
        }


        databaseDataSource.close();

        //Builder untuk AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Inflate / cetak layout XML dialog yang sudah dibuat
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_activity, null);
        builder.setView(dialogView);

        //Inisialisasi view
        TextInputEditText etDialogTitle = dialogView.findViewById(R.id.etDialogTitle);
        MaterialAutoCompleteTextView spinnerType = dialogView.findViewById(R.id.spinnerType);
        MaterialAutoCompleteTextView spinnerUnit = dialogView.findViewById(R.id.spinnerUnit);
        TextInputEditText etTotal = dialogView.findViewById(R.id.etTotal);
        MaterialButton btnTotalMinus = dialogView.findViewById(R.id.btnTotalMinus);
        MaterialButton btnTotalPlus = dialogView.findViewById(R.id.btnTotalPlus);
        MaterialAutoCompleteTextView spinnerSchedule = dialogView.findViewById(R.id.spinnerSchedule);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, typeCategories);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, typesUnits);
        ArrayAdapter<String> scheduleAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, scheduleTypes);

        spinnerType.setAdapter(typeAdapter);
        spinnerUnit.setAdapter(unitAdapter);
        spinnerSchedule.setAdapter(scheduleAdapter);

        //Buat objeck dialog
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        btnTotalMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currValue = Integer.valueOf(etTotal.getText().toString());

                if (currValue > 0) {
                    currValue--;
                    etTotal.setText(String.valueOf(currValue));
                }
            }
        });

        btnTotalPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currValue = Integer.valueOf(etTotal.getText().toString());
                currValue++;
                etTotal.setText(String.valueOf(currValue));
            }
        });
    }
}