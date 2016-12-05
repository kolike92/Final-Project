package com.BUddy.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Sophia_ on 12/5/16.
 */

public class EventLikes extends AppCompatActivity {
    private TextView tvEventLikes;
    private ListView lvEventLikes;
    private ListAdapter lvAdapter;

    private ArrayList<BUEvent> events;


    /* events database connection setup*/
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbEvent = db.getReference("events/");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventlikes);

        tvEventLikes = (TextView) findViewById(R.id.tvEventLikes);
        lvEventLikes = (ListView) findViewById(R.id.lvEventLikes);
        events = new ArrayList<BUEvent> ();


        dbEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    try {



                    } catch (NullPointerException e) {}

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
