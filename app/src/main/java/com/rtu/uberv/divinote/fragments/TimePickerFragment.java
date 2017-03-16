package com.rtu.uberv.divinote.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    public static final String LOG_TAG= TimePickerFragment.class.getSimpleName();

    private OnTimePickedListener listener;

    private int mHour = -1, mMinute = -1;

    public void setOnDatePickerListener(OnTimePickedListener listener){
        this.listener=listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() instanceof OnTimePickedListener){
            listener = (OnTimePickedListener) getActivity();
        }else{
            String activityClassName = getActivity().getClass().getSimpleName();
            Log.w(LOG_TAG,"Activity '"+activityClassName+"' doesn't implement OnDatePickedListener interface!");
        }
    }

    public void setDefaultTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour,minute;
        if(mHour!=-1){
            hour=mHour;
            minute=mMinute;
        }else {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if(listener!=null){
            listener.onTimePicked(hourOfDay,minute);
        }
    }

    public interface OnTimePickedListener{
        void onTimePicked(int hourOfDay, int minute);
    }
}
