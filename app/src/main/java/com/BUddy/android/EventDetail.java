package com.BUddy.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Sophia_ on 10/31/16.
 */

public class EventDetail extends AppCompatActivity{
    private TextView tvTitle;
    private TextView tvTitleSet;
    private TextView tvDate;
    private TextView tvDateSet;
    private TextView tvLocation;
    private TextView tvLocationSet;
    private TextView tvDetails;
    private TextView tvDetailsSet;
    private TextView tvCategories;
    private TextView tvCreatedBy;
    private Button btnMessage;
    private Button btnJoin;
    private BUEvent event;
    private BuddyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Bundle b = getIntent().getExtras();
        String event_id = b.getString("EventID");
        //String  event_id = "-KVkHLDRUTMAOxF_XmfU";
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbEvent = db.getReference("events/" + event_id);
        user = b.getParcelable("user");

        /* Set reference*/
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitleSet = (TextView) findViewById(R.id.tvTitleSet);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDateSet = (TextView) findViewById(R.id.tvDateSet);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocationSet = (TextView) findViewById(R.id.tvLocationSet);
        tvDetails = (TextView) findViewById(R.id.tvDetails);
        tvDetailsSet = (TextView) findViewById(R.id.tvDetailsSet);
        tvCategories = (TextView) findViewById(R.id.tvCategories);
        tvCreatedBy = (TextView) findViewById(R.id.tvCreatedBy);
        btnMessage = (Button) findViewById(R.id.btnMessage);
        btnJoin = (Button) findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                event = dataSnapshot.getValue(BUEvent.class);
                tvTitleSet.setText(event.getEventTitle());
                tvDateSet.setText(event.getEventDate().toString());
                tvLocationSet.setText(event.getLocation());
                tvCategories.setText(event.getCategory());
                tvDetailsSet.setText(event.getEventDetails());
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("BUDDY", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbEvent.addListenerForSingleValueEvent(postListener);


    }
}
