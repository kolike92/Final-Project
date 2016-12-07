package com.BUddy.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Sophia_ on 12/5/16.
 */

public class EventLikes extends InnerActivity {
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
        events = new ArrayList<BUEvent> (10);
        user = getIntent().getExtras().getParcelable(StaticConstants.USER_KEY);


        Query q = dbEvent.orderByChild("likes").limitToFirst(10);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    BUEvent e = child.getValue(BUEvent.class);
                    events.add(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            lvAdapter = new EventListAdapter(getBaseContext(), events, FirebaseDatabase.getInstance(),user);
            lvEventLikes.setAdapter(lvAdapter);

            lvEventLikes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BUEvent e = events.get(position);
                    String eventId = e.getFirebaseId();
                    Intent intent = new Intent(getBaseContext(), EventDetail.class);
                    intent.putExtra(StaticConstants.EID_KEY, eventId);
                    intent.putExtra(StaticConstants.EVENT_KEY, e);
                    intent.putExtra(StaticConstants.USER_KEY, user);
                    startActivity(intent);
                }
            });
        } catch (NullPointerException e) {}



    }
}
