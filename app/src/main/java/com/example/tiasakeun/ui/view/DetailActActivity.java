package com.example.tiasakeun.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.source.DatabaseDataSource;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;

public class DetailActActivity extends AppCompatActivity {

    //Variabel view
    private MaterialAutoCompleteTextView spSubActivity;
    private LinearLayout llQuantity;
    private LinearLayout llTimer;
    private TextView tvProgress;
    private TextView tvPercentProgress;
    private TextView tvTimer;
    private Slider sliderProgress;
    private MaterialButton btnMinus;
    private MaterialButton btnPlus;
    private MaterialButton btnFinish;
    private MaterialButton btnResetTimer;
    private MaterialButton btnPlayPauseTimer;
    private MaterialButton btnSaveTimeLog;
    private CircularProgressIndicator cpTimber;
    private long subActivityId = 0L;
    private long activityId = 0L;
    private final String TAG = "DetailActActivity";

    //Variabel data
    private ArrayList<SubActivity> subActivities = new ArrayList<>();
    private SubActivity subActivity = null;
    private String categoryType;


    //Database
    private DatabaseDataSource db = null;


    private void initView() {
        spSubActivity = findViewById(R.id.spSubActivity);
        llQuantity = findViewById(R.id.llQuantity);
        llTimer = findViewById(R.id.llTimer);
        tvProgress = findViewById(R.id.tvProgress);
        tvPercentProgress = findViewById(R.id.tvPercentProgress);
        tvTimer = findViewById(R.id.tvTimer);
        sliderProgress = findViewById(R.id.sliderProgress);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnFinish = findViewById(R.id.btnFinish);
        btnResetTimer = findViewById(R.id.btnResetTimer);
        btnPlayPauseTimer = findViewById(R.id.btnPlayPauseTimer);
        btnSaveTimeLog = findViewById(R.id.btnSaveTimeLog);
        cpTimber = findViewById(R.id.cpTimber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_act);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_act), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseDataSource(DetailActActivity.this);

        initView();

        Intent intent = getIntent();

        if (intent != null) {
            subActivityId = intent.getLongExtra("SUB_ACTIVITY_ID", 0L);
            activityId = intent.getLongExtra("ACTIVITY_ID", 0L);
            Log.i(TAG, "subActivityId: " + subActivityId + " | activityId: " + activityId);
        }

        db.open();
        subActivities.addAll(db.getSubActivitiesByActivityId(activityId));
        subActivity = db.getSubActivityById(subActivityId);
        categoryType = db.getCategoryTypeById(activityId);

        //Masukkan data ke spinner dan progress sub activity
        spSubActivity.setText(subActivity.getTitle(), false);
        if (categoryType.equals("Waktu")) {
            int targetMinutes = subActivity.getTargetValue();
            int targetInSeconds = targetMinutes * 60;

            cpTimber.setMax(targetInSeconds);

            int progressInSeconds = db.getValueProgress(subActivityId);
            cpTimber.setProgress(progressInSeconds);

            //Cegah waktu menjadi minus
            int secondsRemaining = targetInSeconds - progressInSeconds;
            if (secondsRemaining < 0) {
                secondsRemaining = 0;
            }

            //Tampilkan data waktu ke timer
            int hh = secondsRemaining / 3600;
            int mm = (secondsRemaining % 3600) / 60;
            int ss = secondsRemaining % 60;
            String timerView = String.format("%02d:%02d:%02d", hh, mm, ss);
            tvTimer.setText(timerView);
        } else {

        }

        db.close();





        ArrayList<String> subActivityTitles = new ArrayList<>();
        for (SubActivity data : subActivities) {
            subActivityTitles.add(data.getTitle());
        }
        ArrayAdapter<String> subActivityArrayAdapter = new ArrayAdapter<>(DetailActActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, subActivityTitles);
        spSubActivity.setAdapter(subActivityArrayAdapter);


        Log.i(TAG, "categoryType: " + categoryType);
        if (categoryType.equals("Waktu")) {
            llTimer.setVisibility(View.VISIBLE);
            llQuantity.setVisibility(View.GONE);
        } else {
            llTimer.setVisibility(View.GONE);
            llQuantity.setVisibility(View.VISIBLE);
        }

        spSubActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (categoryType.equals("Waktu")) {

                } else {

                }
            }
        });
    }


}