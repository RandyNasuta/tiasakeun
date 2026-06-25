package com.example.tiasakeun.ui.picker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {
         void onDateSelected(int day, int month, int year);
    }

    private DatePickerListener listener;

    public void setDatePickerListener(DatePickerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), this, year, month, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (listener != null) {
            listener.onDateSelected(i2, i1, i);
        }
    }
}
