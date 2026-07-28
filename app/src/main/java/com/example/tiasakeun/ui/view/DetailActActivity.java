package com.example.tiasakeun.ui.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.model.SubActivityLog;
import com.example.tiasakeun.data.source.DatabaseDataSource;
import com.example.tiasakeun.ui.adapter.SubActivityLogAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.tiasakeun.data.local.ActivityLogContract.ActivityLogEntry;

public class DetailActActivity extends AppCompatActivity {
    private final String TAG = "DetailActActivity";

    //Variabel view

    //Section 1 view group
    private MaterialCardView cardSubActivityProgress;
    private MaterialAutoCompleteTextView spSubActivity;
    private LinearLayout llQuantity;
    private LinearLayout llTimer;
    private TextView tvProgress;
    private TextView tvPercentProgress;
    private TextView tvTimer;
    private TextInputEditText etQuantityTotalProgress;
    private MaterialButton btnFinish;
    private MaterialButton btnResetTimer;
    private MaterialButton btnPlayPauseTimer;
    private CircularProgressIndicator cpTimber;
    private TextView tvNoSubActivity;

    //Section 2 view group
    private RecyclerView rvLogSubAcivity;
    private MaterialButtonToggleGroup btnToggleDays;
    private SubActivityLogAdapter subActivityLogAdapter;
    private MaterialAutoCompleteTextView spSortSubActivity;
    private TextView tvLogNotExists;
    private LinearLayout llLog;
    private BarChart bcActivityLog;


    //Variabel data
    //Section 1
    private long subActivityId = 0L;
    private long activityId = 0L;
    private ArrayList<SubActivity> subActivities = new ArrayList<>();
    private SubActivity subActivity = null;
    private String categoryType;
    private String unitName;

    //Variabel untuk mengatur timer
    private CountDownTimer countDownTimer = null;
    private boolean isTimerRunning = false;
    /**
     * timeLeftInMillis -> waktu yang tersisa untuk selesai
     */
    private long timeLeftInMillis = 0L;
    /**
     * elapsedTimeInSeconds -> waktu yang sudah berjalan
     */
    private long elapsedTimeInSeconds = 0L;

    //Section 2
    private ArrayList<SubActivityLog> subActivityLogs = new ArrayList<>();
    private final String[] sortingList = new String[]{"Terbaru", "Terlama", "Tertinggi", "Terendah"};
    private String durationChoosen, sortingChoosen;

    //Database
    private DatabaseDataSource db = null;

    private void initView() {
        //Section 1 view group
        cardSubActivityProgress = findViewById(R.id.cardSubActivityProgress);
        spSubActivity = findViewById(R.id.spSubActivity);
        llQuantity = findViewById(R.id.llQuantity);
        llTimer = findViewById(R.id.llTimer);
        tvProgress = findViewById(R.id.tvProgress);
        tvPercentProgress = findViewById(R.id.tvPercentProgress);
        tvTimer = findViewById(R.id.tvTimer);
        etQuantityTotalProgress = findViewById(R.id.etQuantityTotalProgress);
        btnFinish = findViewById(R.id.btnFinish);
        btnResetTimer = findViewById(R.id.btnResetTimer);
        btnPlayPauseTimer = findViewById(R.id.btnPlayPauseTimer);
        cpTimber = findViewById(R.id.cpTimber);
        tvNoSubActivity = findViewById(R.id.tvNoSubActivity);

        //Section 2 view group
        rvLogSubAcivity = findViewById(R.id.rvLogSubAcivity);
        rvLogSubAcivity.setLayoutManager(new LinearLayoutManager(this));
        rvLogSubAcivity.addItemDecoration(new DividerItemDecoration(rvLogSubAcivity.getContext(), new LinearLayoutManager(this).getOrientation()));
        btnToggleDays = findViewById(R.id.btnToggleDays);
        spSortSubActivity = findViewById(R.id.spSortSubActivity);
        tvLogNotExists = findViewById(R.id.tvLogNotExists);
        llLog = findViewById(R.id.llLog);
        bcActivityLog = findViewById(R.id.bcActivityLog);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        subActivityId = getIntent().getLongExtra("SUB_ACTIVITY_ID", 0L);
        activityId = getIntent().getLongExtra("ACTIVITY_ID", 0L);
        unitName = getIntent().getStringExtra("UNIT_NAME");
        Log.i(TAG, "subActivityId: " + subActivityId + " | activityId: " + activityId);

        db.open();
        subActivity = db.getSubActivityById(subActivityId);
        categoryType = db.getCategoryTypeById(activityId);
        subActivities.addAll(db.getSubActivitiesByActivityId(activityId, 0, categoryType));

        //Section untuk mengatur awal dari log aktivitas
        db.open();
        subActivityLogs.clear();
        subActivityLogs.addAll(db.getSubActivityLogs(subActivityId, durationChoosen, sortingChoosen));
        db.close();
        initSecondSection(true);

        if (subActivities.isEmpty()) {
            llTimer.setVisibility(GONE);
            llQuantity.setVisibility(GONE);
            btnFinish.setVisibility(GONE);
            tvNoSubActivity.setVisibility(VISIBLE);
        } else {
            Log.i(TAG, "categoryType: " + categoryType);
            if (categoryType.equals("Waktu")) {
                llTimer.setVisibility(VISIBLE);
                llQuantity.setVisibility(GONE);
            } else {
                llTimer.setVisibility(GONE);
                llQuantity.setVisibility(VISIBLE);
            }
        }
        db.close();

        initializeSpinner();
        initializeProgressValue();

        //Section 1
        spSubActivity.setOnItemClickListener((adapterView, view, i, l) -> {
            SubActivity selectedSubActivity = subActivities.get(i);

            //Berubah jika hanya sub activity-nya berbeda
            Log.i(TAG, "onItemSelected: sub activity yang dipilih: " + selectedSubActivity.getId());
            Log.i(TAG, "onItemSelected: sub activity yang lama: " + subActivityId);
            if (selectedSubActivity.getId() != subActivityId) {
                //Untuk mengatur widget progress
                //Atur sub activity id yang terbaru
                subActivityId = selectedSubActivity.getId();

                changeSubActivity(subActivities.get(i));

                //Reset filter tanggal
                Log.i(TAG, "onItemSelected: subActivityId baru: " + subActivityId);
                db.open();
                subActivityLogs.clear();
                subActivityLogs.addAll(db.getSubActivityLogs(subActivityId, durationChoosen, sortingChoosen));
                db.close();
                initSecondSection(false);
            }
        });

        btnPlayPauseTimer.setOnClickListener(view -> {
            if (isTimerRunning) {
                onPauseTimer();
            } else {
                onStartTimer();
            }
        });

        btnResetTimer.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActActivity.this);

            builder.setTitle("Konfirmasi reset waktu");
            builder.setMessage("Apakah anda yakin untuk mengulang waktu");
            builder.setCancelable(true);

            builder.setPositiveButton("Ya", (dialogInterface, i) -> {
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
            });

            builder.setNegativeButton("Tidak", (dialogInterface, i) ->
                dialogInterface.cancel()
            );

            AlertDialog resetDialog = builder.create();

            if (resetDialog.isShowing()) {
                return;
            }

            resetDialog.show();
        });

        btnFinish.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActActivity.this);

            builder.setTitle("Konfirmasi Selesai Aktivitas");
            builder.setMessage("Apakah anda yakin untuk mengakhiri aktivitas ini?");
            builder.setCancelable(true);

            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        isTimerRunning = false;
                    }

                    boolean isSaveSuccess = false;
                    try {
                        db.open();
                        if (categoryType.equals("Waktu")) {
                            if (elapsedTimeInSeconds > 0 ) {
                                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                long createLogActivity = db.createLogActivity(subActivityId, elapsedTimeInSeconds, currentDate);

                                if (createLogActivity != -1) {
                                    elapsedTimeInSeconds = 0;
                                } else {
                                    Toast.makeText(DetailActActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                                    return; //Berhenti jika gagal
                                }
                            }

                            long updateStatus = db.updateCompletedSubActivity(subActivityId);
                            if (updateStatus != -1) {
                                isSaveSuccess = true;
                                Toast.makeText(DetailActActivity.this, "Aktivitas berhasil diselesaikan", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DetailActActivity.this, "Gagal menyimpan data", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                            long finalQuantity = etQuantityTotalProgress.getText().toString().isEmpty() ? 0L : Long.parseLong(etQuantityTotalProgress.getText().toString());
                            long createLogActivity = db.createLogActivity(subActivityId, finalQuantity, currentDate);

                            if (createLogActivity == -1) {
                                Toast.makeText(DetailActActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                                return; //Berhenti jika gagal
                            }

                            long updateStatus = db.updateCompletedSubActivity(subActivityId);
                            if (updateStatus != -1) {
                                isSaveSuccess = true;
                                Toast.makeText(DetailActActivity.this, "Aktivitas berhasil diselesaikan", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DetailActActivity.this, "Gagal menyimpan data", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailActActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onClick: error tekan btn finish: " + e.getMessage());
                        db.close();
                        return;
                    } finally {
                        db.close();
                    }

                    Log.i(TAG, "onClick: apakah berhasil menyimpan data sub: " + isSaveSuccess);
                    if (isSaveSuccess) {
                        //Lakukan perpindahan sub activity ke activity dengan indeks paling pertama
                        //Ubah daftar subActivities
                        //Masukkan ke dalam spinner
                        //Pilih sub activity pertama
                        boolean isRemoved = subActivities.removeIf(subActivity1 -> subActivity1.getId() == subActivityId);
                        Log.i(TAG, "onClick: Berhasil hapus data subactivity: " + isRemoved);
                        Log.i(TAG, "onClick: data subActivities: " + subActivities.toString());
                        if (!subActivities.isEmpty()) {
                            changeSubActivity(subActivities.get(0));
                            initializeSpinner();
                        } else {
                            spSubActivity.setText("Tidak ada aktivitas hari ini", false);
                            llTimer.setVisibility(GONE);
                            llQuantity.setVisibility(GONE);
                            btnFinish.setVisibility(GONE);
                            tvNoSubActivity.setVisibility(VISIBLE);
                        }
                    }
                }
            });

            builder.setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.cancel());

            AlertDialog finishDialog = builder.create();

            if (finishDialog.isShowing()) {
                return;
            }

            finishDialog.show();
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


        etQuantityTotalProgress.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            // Mencegah terjadinya input 2 kali
            if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                return true;
            }

            // Kondisi tombol Enter ditekan
            if (actionId == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                Log.i(TAG, "onCreate: Memulai input jumlah");
                String totalInput = etQuantityTotalProgress.getText().toString().trim();

                if (!totalInput.isEmpty()) {
                    try {
                        long inputValue = Long.parseLong(totalInput);
                        long targetValue = subActivity.getTargetValue();

                        db.open();
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        long createLogActivity = db.createLogActivity(subActivityId, Long.parseLong(totalInput), currentDate);

                        if (createLogActivity != -1) {
                            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_LONG).show();

                            tvPercentProgress.setText(((inputValue * 100) / targetValue) + "%");

                            tvProgress.setText(Long.parseLong(totalInput) + "/" + subActivity.getTargetValue() + " " + unitName);

                            //Hilangkan keyboard setelah enter
                            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                            //Hilangkan fokus
                            etQuantityTotalProgress.clearFocus();
                        } else {
                            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Input quantity: error: "  + e.getMessage());
                    } finally {
                        db.close();
                    }
                    return true;
                } else {
                    Toast.makeText(this, "Input tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });

        //Section 2
        ArrayAdapter<String> listSorting = new ArrayAdapter<>(DetailActActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sortingList);
        spSortSubActivity.setAdapter(listSorting);

        spSortSubActivity.setOnItemClickListener((adapterView, view, i, l) -> {
            if (sortingList[i].equals("Terbaru") || sortingList[i].equals("Tertinggi")) {
                if (sortingList[i].equals("Terbaru")) {
                    sortingChoosen = ActivityLogEntry.COLUMN_LOG_DATE + " DESC";
                } else {
                    sortingChoosen = ActivityLogEntry.COLUMN_VALUE + " DESC";
                }
            } else {
                if (sortingList[i].equals("Terlama")) {
                    sortingChoosen = ActivityLogEntry.COLUMN_LOG_DATE + " ASC";
                } else {
                    sortingChoosen = ActivityLogEntry.COLUMN_VALUE + " ASC";
                }
            }

            db.open();
            subActivityLogs = db.getSubActivityLogs(subActivityId, durationChoosen, sortingChoosen);
            subActivityLogAdapter.setSubActivityLogs(subActivityLogs);

            if (subActivityLogs.isEmpty()) {
                llLog.setVisibility(GONE);
                tvLogNotExists.setVisibility(VISIBLE);
            } else {
                llLog.setVisibility(VISIBLE);
                tvLogNotExists.setVisibility(GONE);
            }
            db.close();
        });

        btnToggleDays.addOnButtonCheckedListener((materialButtonToggleGroup, chekedId, isChecked) -> {
            db.open();
            if (isChecked) {
                if (chekedId == R.id.btnOneDay) {
                    subActivityLogs = db.getSubActivityLogs(subActivityId, "-1 day", sortingChoosen);
                    durationChoosen = "-1 day";
                } else if (chekedId == R.id.btnOneWeek) {
                    subActivityLogs = db.getSubActivityLogs(subActivityId, "-7 day", sortingChoosen);
                    durationChoosen = "-7 day";
                } else if (chekedId == R.id.btnOneMonth) {
                    subActivityLogs = db.getSubActivityLogs(subActivityId, "-30 day", sortingChoosen);
                    durationChoosen = "-30 day";
                } else if (chekedId == R.id.btnOneYear) {
                    subActivityLogs = db.getSubActivityLogs(subActivityId, "-365 day", sortingChoosen);
                    durationChoosen = "-365 day";
                } else  {
                    subActivityLogs = db.getSubActivityLogs(subActivityId, "", sortingChoosen);
                    durationChoosen = "";
                }
                subActivityLogAdapter.setSubActivityLogs(subActivityLogs);

                if (subActivityLogs.isEmpty()) {
                    llLog.setVisibility(GONE);
                    tvLogNotExists.setVisibility(VISIBLE);
                } else {
                    llLog.setVisibility(VISIBLE);
                    tvLogNotExists.setVisibility(GONE);
                }
            }
            db.close();
            setupBarChart();
        });

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initSecondSection(boolean isFirstInit) {
        durationChoosen = "-1 day";
        sortingChoosen = ActivityLogEntry.COLUMN_LOG_DATE + " DESC";
        spSortSubActivity.setText("Terbaru", false);

        //Cek jika data log nya kosong
        if (subActivityLogs.isEmpty()) {
            llLog.setVisibility(GONE);
            tvLogNotExists.setVisibility(VISIBLE);
        } else {
            llLog.setVisibility(VISIBLE);
            tvLogNotExists.setVisibility(GONE);
        }

        //Inisialisasi adapter sub activity log
        btnToggleDays.clearChecked();
        if (isFirstInit) {
            subActivityLogAdapter = new SubActivityLogAdapter(subActivityLogs, DetailActActivity.this);
            rvLogSubAcivity.setAdapter(subActivityLogAdapter);
        } else {
            subActivityLogAdapter.setSubActivityLogs(subActivityLogs);
        }

        //Atur chart sub activity
        setupBarChart();
    }

    private void initializeSpinner() {
        //Masukkan data ke spinner dan progress sub activity
        ArrayAdapter<String> subActivityArrayAdapter = new ArrayAdapter<>(
                DetailActActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                subActivities
                        .stream()
                        .filter(data -> data.getIsCompleted() != 1)
                        .map(SubActivity::getTitle)
                        .collect(Collectors.toList())
        );
        spSubActivity.setAdapter(subActivityArrayAdapter);

        if (!subActivities.isEmpty()) {
            spSubActivity.setText(subActivity.getTitle(), false);
        } else {
            spSubActivity.setText("Tidak ada aktivitas hari ini", false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeSubActivity(SubActivity selectedSubActivity) {
        Log.i(TAG, "changeSubActivity: Mulai proses perubahan sub activity");
        //Kategorikan berdasarkan tipe progressnya - waktu atau jumlah
        if (categoryType.equals("Waktu")) {
            if (isTimerRunning) {
                onPauseTimer(); //Lakukan, skema onPauseTimer
            }
        }

        //Ambil data sub activity terbaru
        activityId = selectedSubActivity.getActivityId();
        subActivity = selectedSubActivity;
        Log.i(TAG, "subActivityId: " + subActivityId + " | activityId: " + activityId);

        initializeProgressValue();
    }

    private void onStartTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onFinish() {
                isTimerRunning = false;
                btnPlayPauseTimer.setIconResource(R.drawable.outline_autoplay_24);
                btnResetTimer.setEnabled(true);
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
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
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

    private void initializeProgressValue() {
        /**
         * targetInSeconds -> target waktu yang ingin dicapai
         * pada suatu aktivitas dikonversi ke detik
         */
        db.open();

        if (categoryType.equals("Waktu")) {
            long targetInSeconds= subActivity.getTargetValue();
            cpTimber.setMax((int)targetInSeconds);
            long progressInSeconds = db.getValueProgress(subActivityId, "Waktu");

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
            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hh, mm, ss));

            timeLeftInMillis = secondsRemaining * 1000L;
        } else {
            long progress = db.getValueProgress(subActivityId, "Jumlah");
            tvProgress.setText(progress + "/" +  subActivity.getTargetValue() + " " + unitName);
            tvPercentProgress.setText(((progress * 100) / subActivity.getTargetValue()) + "%");
            etQuantityTotalProgress.setText(String.valueOf(progress));
        }

        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupBarChart() {
        Log.i(TAG, "setupBarChart: run");
        db.open();
        LinkedHashMap<String, Long> dailyData = db.getDailyChartSummary(subActivityId, durationChoosen);
        db.close();

        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> xLabels = new ArrayList<>();

        //Atur format date string yang diterima
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
        //Atur format yang diinginkan
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault());


        int index = 0;
        for (Map.Entry<String, Long> entry : dailyData.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));

            //Ubah ke obyek LocalDateTime
            LocalDate dateTime = LocalDate.parse(entry.getKey(), inputFormatter);
            xLabels.add(dateTime.format(outputFormatter));
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Total Harian");
        dataSet.setColor(getResources().getColor(R.color.accent_blue));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        bcActivityLog.setData(barData);

        //Format sumbu x
        XAxis xAxis = bcActivityLog.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        bcActivityLog.getDescription().setEnabled(false);
        bcActivityLog.getAxisRight().setEnabled(false);
        bcActivityLog.animateY(1000);
        bcActivityLog.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause run");
        if (isTimerRunning && countDownTimer!= null) {
            countDownTimer.cancel();

            SharedPreferences sharedPreferences = getSharedPreferences("TIMER_PREF", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("IS_TIMER_RUNNING", true);
            editor.putLong("TIME_EXITED", System.currentTimeMillis());
            editor.putLong("TIME_LEFT_IN_MILLIS", timeLeftInMillis);
            editor.putLong("ELAPSED_TIME_IN_SECONDS", elapsedTimeInSeconds);
            editor.putLong("SUB_ACTIVITY_ID", subActivityId);
            editor.apply();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume run");

        SharedPreferences sharedPreferences = getSharedPreferences("TIMER_PREF", MODE_PRIVATE);
        boolean wasRunning = sharedPreferences.getBoolean("IS_TIMER_RUNNING", false);
        long savedSubActivityId = sharedPreferences.getLong("SUB_ACTIVITY_ID", -1);

        if (wasRunning && savedSubActivityId == subActivityId) {
            long timeExited = sharedPreferences.getLong("TIME_EXITED", 0);
            long savedTimeLeft = sharedPreferences.getLong("TIME_LEFT_IN_MILLIS", 0);
            long savedElapsedTime = sharedPreferences.getLong("ELAPSED_TIME_IN_SECONDS", 0);

            long timeAwayInMillis = System.currentTimeMillis() - timeExited;
            timeLeftInMillis = savedTimeLeft - timeAwayInMillis;

            //Hitung waktu berjalan normal jika timer belum habis
            elapsedTimeInSeconds = savedElapsedTime + (timeAwayInMillis / 1000);
            sharedPreferences.edit().clear().apply();

            if (timeLeftInMillis <= 0) {
                timeLeftInMillis = 0;
                isTimerRunning = false;

                String timerView = String.format(Locale.getDefault(), "%02d:%02d:%02d", 00, 00, 00);
                tvTimer.setText(timerView);
                Toast.makeText(this, "Waktu sudah habis", Toast.LENGTH_SHORT).show();

                //Hitung waktu aktual yang dihabiskan: waktu yang telah berjalan sebelum keluar
                //ditambah sisa waktu yang dihabiskan di background
                long actualTimeSpent = savedElapsedTime + (savedTimeLeft / 1000);

                if (actualTimeSpent > 0) {
                    try {
                        db.open();
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        long createLogActivity = db.createLogActivity(subActivityId, actualTimeSpent, currentDate);

                        if (createLogActivity != -1) {
                            Log.i(TAG, "onResume: Data background timer berhasil disimpan (" + actualTimeSpent + " detik");

                            subActivityLogs.clear();
                            subActivityLogs.addAll(db.getSubActivityLogs(subActivityId, durationChoosen, sortingChoosen));
                            initSecondSection(false);
                        } else {
                            Toast.makeText(this, "Gagal menyimpan data log", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onResume: DB Error: " + e.getMessage());
                    } finally {
                        db.close();
                    }
                }
            } else {
                isTimerRunning = true;
                onStartTimer();
            }
        } else {
            sharedPreferences.edit().clear().apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop run");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy run");
    }
}