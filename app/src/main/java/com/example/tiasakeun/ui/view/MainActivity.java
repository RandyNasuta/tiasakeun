package com.example.tiasakeun.ui.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.ui.adapter.ActivityAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAddActivity;
    private RecyclerView rVActiviy;
    private ActivityAdapter activityAdapter;
    private ArrayList<Activity> activityList;

    private void initView() {
        fabAddActivity = findViewById(R.id.fabAddActivity);
        rVActiviy = findViewById(R.id.rVActiviy);
        rVActiviy.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityList = new ArrayList<>();
        activityList.add(new Activity(
                1, 1, 2, 1, "Minum Air Putih", 400, 2000, 1, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                2, 1, 1, 1, "Lari Pagi", 0, 10, 0, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                3, 1, 1, 1, "Lari Siang", 0, 10, 0, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                4, 1, 1, 1, "Lari Malam", 0, 10, 0, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                5, 1, 2, 1, "Minum Air Putih", 400, 2000, 1, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                6, 1, 1, 1, "Lari Pagi", 0, 10, 0, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                7, 1, 1, 1, "Lari Siang", 0, 10, 0, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                8, 1, 1, 1, "Lari Malam", 0, 10, 0, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                9, 1, 2, 1, "Minum Air Putih", 400, 2000, 1, R.drawable.air_putih
        ));
        activityList.add(new Activity(
                10, 1, 1, 1, "Lari Pagi", 0, 10, 0, R.drawable.air_putih
        ));

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
    }
}