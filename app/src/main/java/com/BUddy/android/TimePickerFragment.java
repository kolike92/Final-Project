package com.BUddy.android;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by rebeccagraber on 11/4/16.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    OnTimeSelectedListener mListen;
    Context mContext;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mContext = getActivity();
            mListen = (OnTimeSelectedListener) getActivity();
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(mContext, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mListen.onTimeSelected(hourOfDay, minute);
        }

    public interface OnTimeSelectedListener
    {
        public void onTimeSelected(int hourOfDay, int minute);

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mListen = (OnTimeSelectedListener) context;
    }
}
