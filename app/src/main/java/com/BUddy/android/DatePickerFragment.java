/**
 * Class: DatePickerFragment
 * Superclass: DialogFragment
 * Implements: DatePickerDialog.OnDateSetListener
 * @author NOGE
 * Pops out date picker dialog
 */

package com.BUddy.android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;



public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private OnDateSelectedListener mListen;
    private Context mContext;


    /**
     * From DialogFragment
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        mContext = getActivity();
        mListen = (OnDateSelectedListener) getActivity();

        //initialize calendar to today
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(mContext, this,year,month,day );
    }

    /**
     * For DatePickerDialog.OnDateSetListener. On date set, call fragment listener to update UI
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mListen.onDateSelected(year, month, dayOfMonth);
    }

    /**
     * Interface for fragment listener
     */
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
