package com.rtu.uberv.divinote.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    public static final String LOG_TAG = DatePickerFragment.class.getSimpleName();

    private OnDatePickedListener listener;

    private int mYear = -1, mMonth = -1, mDay = -1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof OnDatePickedListener) {
            listener = (OnDatePickedListener) getActivity();
        } else {
            String activityClassName = getActivity().getClass().getSimpleName();
            Log.w(LOG_TAG, "Activity '" + activityClassName + "' doesn't implement OnDatePickedListener interface!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        if (mYear != -1) {
            year = mYear;
            month = mMonth;
            day = mDay;
        } else {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setDefaultDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    public void setOnDatePickerListener(OnDatePickedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if (listener != null) {
            listener.onDatePicked(year, month, day);
        }
    }

    public interface OnDatePickedListener {
        void onDatePicked(int year, int month, int day);
    }

}
