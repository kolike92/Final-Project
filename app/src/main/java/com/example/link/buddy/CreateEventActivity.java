package com.example.link.buddy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    Button btnCreate, btnCancel;
    EditText etTitle, etLocation, etDetails;
    Spinner spnCategories;
    DatePicker dpDate;
    long categoryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etDetails = (EditText) findViewById(R.id.etDetails);
        spnCategories = (Spinner) findViewById(R.id.spnCategories);
        dpDate = (DatePicker) findViewById(R.id.dpDate);



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
                int day = dpDate.getDayOfMonth();
                int month = dpDate.getMonth();
                int year = dpDate.getYear();
                String details = etDetails.getText().toString();
                Calendar c = Calendar.getInstance();
                c.set(year,month,day);
                Date d = c.getTime();
                EventCategory category = (EventCategory) spnCategories.getSelectedItem();
                BUEvent e = new BUEvent(d, title, details, category.getId(), location);
                DatabaseReference eventRef = myRef.push();
                eventRef.setValue(e);
            }
        });


    }

    private class CategoryAdapter extends ArrayAdapter<EventCategory>
    {

        public CategoryAdapter(Context context, int textViewResourceId, ArrayList<EventCategory> items) {
            super(context, textViewResourceId, items);
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.category_row, parent, false);



            }
            TextView tv = (TextView) convertView.findViewById(R.id.tvCategory);


            EventCategory item = getItem(position);
            if (item!= null) {
                // My layout has only one TextView
                // do whatever you want with your string and long
                tv.setText(item.getName());
            }

            return convertView;
        }
    }
}
