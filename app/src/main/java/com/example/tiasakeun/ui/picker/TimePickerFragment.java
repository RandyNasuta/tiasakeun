package com.example.tiasakeun.ui.picker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public interface TimePickerListener {
        void onTimeSelected(int hour, int minute);
    }

    private TimePickerListener listener;

    public void setTimePickerListener(TimePickerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),
                this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if (listener != null) {
            listener.onTimeSelected(i, i1);
        }
    }
}
