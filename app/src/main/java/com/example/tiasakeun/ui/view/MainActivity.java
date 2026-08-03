package com.example.tiasakeun.ui.view;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.model.Schedule;
import com.example.tiasakeun.data.model.Type;
import com.example.tiasakeun.data.source.DatabaseDataSource;
import com.example.tiasakeun.ui.adapter.ActivityAdapter;
import com.example.tiasakeun.ui.adapter.IconAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Variabel view
    private static final String TAG = "MainActivity";
    private FloatingActionButton fabAddActivity;
    private RecyclerView rVActiviy;
    private ActivityAdapter activityAdapter;
    private ArrayList<Activity> activityList = new ArrayList<>();
    private DatabaseDataSource db = null;
    private TextView tvNoDataActiviy;

    private void initView() {
        fabAddActivity = findViewById(R.id.fabAddActivity);

        //RecyclerView
        rVActiviy = findViewById(R.id.rVActiviy);
        rVActiviy.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tvNoDataActiviy = findViewById(R.id.tvNoDataActiviy);

        db.open();
        activityList.addAll(db.getAllActivities());
        db.close();

        if (activityList.isEmpty()) {
            tvNoDataActiviy.setVisibility(View.VISIBLE);
            rVActiviy.setVisibility(View.GONE);
        } else {
            tvNoDataActiviy.setVisibility(View.GONE);
            rVActiviy.setVisibility(View.VISIBLE);
        }

        activityAdapter = new ActivityAdapter(MainActivity.this, activityList);
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
        db = new DatabaseDataSource(this);
        initView();

        refreshDataAct();

        fabAddActivity.setOnClickListener(view -> showDialogCreateActivity());

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.35f;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Activity activity = activityList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    if (viewHolder.itemView.getTag() != null && (boolean) viewHolder.itemView.getTag()) {
                        return;
                    }
                    viewHolder.itemView.setTag(true);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Hapus Aktivitas?");
                    builder.setMessage("Aktivitas yang dihapus akan menghapus semua kegiatan yang sudah dilakukan");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Hapus", (dialogInterface, i) -> {
                        db.open();

                        try {
                            boolean deleteData = db.deleteActivity(activity.getId());

                            if (deleteData) {
                                Toast.makeText(MainActivity.this, "Aktivitas berhasil dihapus", Toast.LENGTH_SHORT).show();

                                int currPos = activityList.indexOf(activity);
                                if (currPos != -1) {
                                    activityList.remove(currPos);
                                    activityAdapter.notifyItemRemoved(currPos);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Aktivitas gagal dihapus", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onBindViewHolder: Gagal hapus data aktivitas: " + e.getMessage());
                        } finally {
                            db.close();
                        }
                    });

                    builder.setNegativeButton("Batal", (dialogInterface, i) -> {
                        dialogInterface.dismiss();

                        //Mengembalikkan item yang sudah hilang dari layar
                        activityAdapter.notifyItemChanged(position);
                    });

                    builder.setOnDismissListener(dialogInterface -> {
                        viewHolder.itemView.setTag(false);
                    });

                    AlertDialog dialog = builder.create();

                    dialog.show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    if (position == RecyclerView.NO_POSITION) return;

                    if (viewHolder.itemView.getTag() != null && (boolean) viewHolder.itemView.getTag()) {
                        return;
                    }
                    viewHolder.itemView.setTag(true);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_edit_activity, null);;
                    builder.setView(dialogView);
                    builder.setCancelable(false);

                    TextInputEditText etActivityTitle = dialogView.findViewById(R.id.etActivityTitle);
                    MaterialButton btnCancelEditActivity = dialogView.findViewById(R.id.btnCancelEditActivity);
                    MaterialButton btnUpdateActivity = dialogView.findViewById(R.id.btnUpdateActivity);

                    //Tampilkan judul activity saat ini
                    etActivityTitle.setText(activity.getTitle());

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    btnCancelEditActivity.setOnClickListener(view -> {
                        viewHolder.itemView.setTag(false);
                        activityAdapter.notifyItemChanged(position);
                        dialog.dismiss();
                    });

                    btnUpdateActivity.setOnClickListener(view -> {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setTitle("Yakin untuk ubah judul Aktivitas?");
                        builder1.setMessage("Judul aktivitas yang sudah diubah masih diubah lagi");
                        builder1.setCancelable(false);

                        builder1.setPositiveButton("Ubah", (dialogInterface, i) -> {
                           db.open();

                           try {
                               boolean updateTitle = db.updateActivityTitle(activity.getId(), etActivityTitle.getText().toString().trim());

                               if (updateTitle) {
                                   activity.setTitle(etActivityTitle.getText().toString().trim());

                                   Toast.makeText(MainActivity.this, "Judul aktivitas berhasil diubah", Toast.LENGTH_SHORT).show();
                                   viewHolder.itemView.setTag(false);

                                   activityAdapter.notifyItemChanged(position);
                                   dialog.dismiss();
                               } else {
                                   Toast.makeText(MainActivity.this, "Judul aktivitas gagal diubah", Toast.LENGTH_SHORT).show();
                               }
                           } catch (Exception e) {
                               Log.e(TAG, "onSwiped: Ubah judul aktivitas: " + e.getMessage());
                           } finally {
                               db.close();
                           }
                        });


                        builder1.setNegativeButton("Batal", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });

                        AlertDialog dialog1 = builder1.create();
                        dialog1.show();
                    });
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getBottom() - itemView.getTop();

                Paint paint = new Paint();
                paint.setAntiAlias(true);

                ColorDrawable background;
                Drawable icon;

                float cornerRadius = 24f;

                //Swipe ke kanan
                if (dX > 0) {
                    paint.setColor(Color.parseColor("#2196F3"));

                    RectF backgroundRect = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dX, itemView.getBottom());
                    c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, paint);

                    icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_edit_24);
                    if (icon != null) {
                        icon.setTint(Color.WHITE);
                        int iconTop = itemView.getTop() + (itemHeight - icon.getIntrinsicHeight()) / 2;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        int iconLeft = itemView.getLeft() + 32;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                } else if (dX < 0) {
                    paint.setColor(Color.parseColor("#F44336"));

                    RectF backgroundRect = new RectF(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, paint);

                    icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_delete_24);
                    if (icon != null) {
                        icon.setTint(Color.WHITE);
                        int iconTop = itemView.getTop() + (itemHeight - icon.getIntrinsicHeight()) / 2;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        int iconRight = itemView.getRight() - 32;
                        int iconLeft = iconRight - icon.getIntrinsicWidth();

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rVActiviy);
    }

    private void refreshDataAct() {
        if (db != null) {
            db.open();
            ArrayList<Activity> newData = db.getAllActivities();
            db.close();

            if (newData.isEmpty()) {
                tvNoDataActiviy.setVisibility(View.VISIBLE);
                rVActiviy.setVisibility(View.GONE);
            } else {
                tvNoDataActiviy.setVisibility(View.GONE);
                rVActiviy.setVisibility(View.VISIBLE);
            }

            if (activityList != null) {
                activityList.clear();
                activityList.addAll(newData);
            }

            activityAdapter = new ActivityAdapter(MainActivity.this, activityList);
            rVActiviy.setAdapter(activityAdapter);
        }
    }

    private void showDialogCreateActivity() {
        //Variabel
        final Type[] selectedType = {null};
        final Schedule[] selectedSchedule = {null};
        final int[] sYear = {0}, sMonth = {0}, sDay = {0}, sHour = {0}, sMinute = {0};
        final int[] icon = {0};

        //Panggil DatabaseDataSource
        db.open();

        ArrayList<Type> typesUnits = db.getAllTypes();
        ArrayList<Schedule> scheduleTypes = db.getAllSchedules();

        db.close();

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

                db.open();

                try {
                    long result = db.createActivity(
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
                    db.close();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        refreshDataAct();
    }
}