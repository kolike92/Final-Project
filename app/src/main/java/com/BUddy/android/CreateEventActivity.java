package com.BUddy.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    SimpleDateFormat sdf, stf;
    Calendar c;
    FragmentManager fragMan;
    BuddyUser user;

    //  DatePicker dpDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        user = getIntent().getExtras().getParcelable(StaticConstants.USER_KEY);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etDetails = (EditText) findViewById(R.id.etDetails);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etNumber = (EditText) findViewById(R.id.etNumber);
        spnCategories = (Spinner) findViewById(R.id.spnCategories);
        sdf = new SimpleDateFormat("MM-dd-yyyy");
        stf = new SimpleDateFormat("HH:mm");
        //  dpDate = (DatePicker) findViewById(R.id.dpDate);
        c = Calendar.getInstance();
        fragMan = getFragmentManager();



        tpdTime = new TimePickerFragment();
        dpdDate = new DatePickerFragment();




        ArrayAdapter<EventCategory> dataAdapter = new ArrayAdapter<EventCategory>(this,
                android.R.layout.simple_spinner_item, Arrays.asList(EventCategory.values()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategories.setAdapter(dataAdapter);


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get a reference to the todoItems child items it the database
        final DatabaseReference myRef = database.getReference("events");



        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String location = etLocation.getText().toString();
                int number = Integer.parseInt(etNumber.getText().toString());
                //fake date
                int day = 1;//dpDate.getDayOfMonth();
                int month = 1;//dpDate.getMonth();
                int year = 2016;//dpDate.getYear();
                Calendar c = Calendar.getInstance();
                c.set(year,month,day);

                try
                {
                    //there has GOT to be a better way to do this
                    c.setTime(sdf.parse(etDate.getText().toString()));
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DATE);
                    c.setTime(stf.parse(etTime.getText().toString()));
                    int hour = c.get(Calendar.HOUR);
                    int minute = c.get(Calendar.MINUTE);
                    c.set(year,month,day,hour,minute);


                }
                catch(ParseException pe)
                {

                }

                String details = etDetails.getText().toString();

                Date d = c.getTime();
                EventCategory category = (EventCategory) spnCategories.getSelectedItem();
                BUEvent e = new BUEvent(d, title, number, details, category.getId(), location, user.getFirebaseId());
                DatabaseReference eventRef = myRef.push();
                e.setFirebaseId(eventRef.getKey());
                eventRef.setValue(e);

                Intent intent = new Intent(getBaseContext(), EventDetail.class);
                Bundle b = new Bundle();
                b.putParcelable(StaticConstants.EVENT_KEY, e);
                b.putString(StaticConstants.EID_KEY, e.getFirebaseId());
                b.putParcelable(StaticConstants.USER_KEY, user);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(),HomeActivity.class );
                i.putExtra(StaticConstants.USER_KEY,user);
                startActivity(i);


            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fragMan.beginTransaction();
                Fragment prev = fragMan.findFragmentByTag("Choose Time");
                if(prev != null)
                {
                    ft.remove(prev);
                }
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
                if(prev != null)
                {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                if(!dpdDate.isAdded()) {
                    dpdDate.show(fragMan, "Choose Date");
                }
            }
        });


    }


    @Override
    public void onTimeSelected(int hourOfDay, int minute) {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        String time = stf.format(c.getTime());
        etTime.setText(time);

    }

    @Override
    public void onDateSelected(int year, int month, int dayOfMonth) {
        c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        String date = sdf.format(c.getTime());
        etDate.setText(date);
    }
}