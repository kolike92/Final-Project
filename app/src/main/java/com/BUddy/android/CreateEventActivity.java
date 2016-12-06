/**
 * Class: CreateEventActivity
 * Superclass: InnerActivity
 * Implements: TimePickerFragment.OnTimeSelectedListener, DatePickerFragment.OnDateSelectedListener
 * The activity for creating a new event
 */

package com.BUddy.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends InnerActivity
        implements TimePickerFragment.OnTimeSelectedListener, DatePickerFragment.OnDateSelectedListener{

    Button btnCreate, btnCancel;
    EditText etTitle, etLocation, etDetails, etDate, etTime,etNumber;
    Spinner spnCategories;
    DatePickerFragment dpdDate;
    TimePickerFragment tpdTime;
    Calendar c;
    FragmentManager fragMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //User should be passed in bundle
        user = getIntent().getExtras().getParcelable(StaticConstants.USER_KEY);

        //Set up Views
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etDetails = (EditText) findViewById(R.id.etDetails);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etNumber = (EditText) findViewById(R.id.etNumber);
        spnCategories = (Spinner) findViewById(R.id.spnCategories);
        c = Calendar.getInstance();
        fragMan = getFragmentManager();
        Context ct = getApplicationContext();



        tpdTime = new TimePickerFragment();
        dpdDate = new DatePickerFragment();


        //create adapter for event category spinner
        ArrayAdapter<EventCategory> dataAdapter = new ArrayAdapter<EventCategory>(this,
                android.R.layout.simple_spinner_item, Arrays.asList(EventCategory.values()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategories.setAdapter(dataAdapter);

        //database handle
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("events");

        //when create is clicked, save event
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get event data from EditTexts
                String title = etTitle.getText().toString();
                String location = etLocation.getText().toString();
                //number of participants
                int number = Integer.parseInt(etNumber.getText().toString());

                //initialize calendar object
                int day = 1;//dpDate.getDayOfMonth();
                int month = 1;//dpDate.getMonth();
                int year = 2016;//dpDate.getYear();
                Calendar c = Calendar.getInstance();
                c.set(year,month,day);

                try
                {
                    //set calendar object to event date
                    c.setTime(StaticConstants.SDF.parse(etDate.getText().toString()));
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DATE);
                    c.setTime(StaticConstants.STF.parse(etTime.getText().toString()));
                    int hour = c.get(Calendar.HOUR);
                    int minute = c.get(Calendar.MINUTE);
                    c.set(year,month,day,hour,minute);
                }
                catch(ParseException pe)
                {
                    Log.e(StaticConstants.TAG, "Error: unparseable event date: " + pe.getMessage());
                }

                String details = etDetails.getText().toString();

                Date d = c.getTime();
                EventCategory category = (EventCategory) spnCategories.getSelectedItem();

                //create new BUEvent object
                BUEvent e = new BUEvent(d, title, number, details, category.getId(), location, user.getFirebaseId(),0);

                //save event to DB
                DatabaseReference eventRef = myRef.push();
                e.setFirebaseId(eventRef.getKey());
                eventRef.setValue(e);

                //go to EventDetail page for editing
                Intent intent = new Intent(getBaseContext(), EventDetail.class);
                Bundle b = new Bundle();
                //passing eventid separate from event objects guards against bad BUEvent objects
                b.putParcelable(StaticConstants.EVENT_KEY, e);
                b.putString(StaticConstants.EID_KEY, e.getFirebaseId());
                //user is always passed
                b.putParcelable(StaticConstants.USER_KEY, user);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        //Cancel: return to main page
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(),HomeActivity.class );
                //always pass user
                i.putExtra(StaticConstants.USER_KEY,user);
                startActivity(i);
            }
        });

        //Pop out TimePickerFragment when time EditText is clicked
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fragMan.beginTransaction();
                Fragment prev = fragMan.findFragmentByTag("Choose Time");
                //remove any previous instance of fragment
                if(prev != null)
                {
                    ft.remove(prev);
                }
                //can't go back to a DialogFragment
                ft.addToBackStack(null);
                if(!tpdTime.isAdded()) {

                    tpdTime.show(fragMan, "Choose Time");
                }
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = fragMan.beginTransaction();
                Fragment prev = fragMan.findFragmentByTag("Choose Date");
                //remove any previous instance of fragment
                if(prev != null)
                {
                    ft.remove(prev);
                }
                //can't go back to a DialogFragment
                ft.addToBackStack(null);
                if(!dpdDate.isAdded()) {
                    dpdDate.show(fragMan, "Choose Date");
                }
            }
        });


    }


    // For TimePickerFragment.OnTimeSelectedListener
    // set time EditText to show selected time
    @Override
    public void onTimeSelected(int hourOfDay, int minute) {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        String time = StaticConstants.STF.format(c.getTime());
        etTime.setText(time);

    }

    // For DatePickerFragment.OnDateSelectedListener
    // set date EditText to show selected date
    @Override
    public void onDateSelected(int year, int month, int dayOfMonth) {
        c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        String date = StaticConstants.SDF.format(c.getTime());
        etDate.setText(date);
    }
}