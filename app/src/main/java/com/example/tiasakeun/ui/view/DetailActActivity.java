package com.example.tiasakeun.ui.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    //Variabel untuk mengatur timer
    private CountDownTimer countDownTimer = null;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 0L;
    private long elapsedTimeInSeconds = 0L;


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
            //Atur nilai max dengan second dari target value di subactivity
            long targetInSeconds= subActivity.getTargetValue();
            cpTimber.setMax((int)targetInSeconds);

            long progressInSeconds = db.getValueProgress(subActivityId);
            Log.i(TAG, "onCreate: progress in seconds: " + progressInSeconds);
            cpTimber.setProgress( (int) progressInSeconds);

            //Cegah waktu menjadi minus
            long secondsRemaining = targetInSeconds - progressInSeconds;
            if (secondsRemaining < 0) {
                secondsRemaining = 0;
            }

            //Tampilkan data waktu ke timer
            long hh = secondsRemaining / 3600;
            long mm = (secondsRemaining % 3600) / 60;
            long ss = secondsRemaining % 60;
            String timerView = String.format("%02d:%02d:%02d", hh, mm, ss);
            tvTimer.setText(timerView);

            timeLeftInMillis = secondsRemaining * 1000L;
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

        btnPlayPauseTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTimerRunning) {
                    onPauseTimer();
                } else {
                    onStartTimer();
                }
            }
        });

        btnResetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActActivity.this);

                builder.setTitle("Konfirmasi reset waktu");
                builder.setMessage("Apakah anda yakin untuk mengulang waktu");
                builder.setCancelable(true);

                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            db.open();
                            db.deleteActivityLogs(subActivityId);
                        } catch (Exception e) {
                            Log.e(TAG, "onClick: Error reset time: " + e.getMessage());
                        } finally {
                            db.close();
                        }

                        timeLeftInMillis = subActivity.getTargetValue() * 1000L;
                        cpTimber.setProgress(0);

                        long hh = subActivity.getTargetValue() / 3600;
                        long mm = (subActivity.getTargetValue() % 3600) / 60;
                        long ss = subActivity.getTargetValue() % 60;
                        String timerView = String.format("%02d:%02d:%02d", hh, mm, ss);
                        tvTimer.setText(timerView);
                        elapsedTimeInSeconds = 0;
                    }
                });

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog resetDialog = builder.create();
                resetDialog.show();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isTimerRunning) {
                    Toast.makeText(DetailActActivity.this, "Tidak bisa keluar karena waktu berjalan", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void onStartTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onFinish() {
                isTimerRunning = false;
                Toast.makeText(DetailActActivity.this, "target waktu telah tercapai", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                elapsedTimeInSeconds++;

                int secondRemaining = (int) (timeLeftInMillis / 1000);
                int hh = secondRemaining / 3600;
                int mm = (secondRemaining % 3600) / 60;
                int ss = secondRemaining % 60;


                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hh, mm, ss);
                tvTimer.setText(timeFormatted);

                int currentProgress = cpTimber.getProgress() + 1;
                cpTimber.setProgress(currentProgress);
            }
        }.start();

        isTimerRunning = true;
        btnPlayPauseTimer.setIconResource(R.drawable.baseline_motion_photos_paused_24);
        btnResetTimer.setEnabled(false);
    }

    private void onPauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (elapsedTimeInSeconds > 0) {
            try {
                db.open();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                long createLogActivity = db.createLogActivity(subActivityId, elapsedTimeInSeconds, currentDate);

                if (createLogActivity != -1) {
                    elapsedTimeInSeconds = 0;
                    Log.i(TAG, "onPauseTimer: Data pause berhasil disimpan");
                } else {
                    Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "onPauseTimer: error - " + e.getMessage());
            } finally {
                db.close();
            }
        }


        isTimerRunning = false;
        btnPlayPauseTimer.setIconResource(R.drawable.outline_autoplay_24);
        btnResetTimer.setEnabled(true);
    }

    @Override
    protected void onPause() {
        onPauseTimer();
        super.onPause();
    }

    @Override
    protected void onStop() {
        onPauseTimer();
        super.onStop();
    }
}