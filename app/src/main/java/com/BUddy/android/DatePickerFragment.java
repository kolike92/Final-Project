package com.BUddy.android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by rebeccagraber on 11/4/16.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private OnDateSelectedListener mListen;
    private Context mContext;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker


        mContext = getActivity();
        mListen = (OnDateSelectedListener) getActivity();

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(mContext, this,year,month,day );
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mListen.onDateSelected(year, month, dayOfMonth);
    }

    public interface OnDateSelectedListener
    {
        public void onDateSelected(int year, int month, int dayOfMonth);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mListen = (OnDateSelectedListener) context;
        mContext = context;
    }
}
