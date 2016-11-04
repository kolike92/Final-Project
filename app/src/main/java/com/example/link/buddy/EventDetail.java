package com.example.link.buddy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private TextView tvDetails;
    private BUEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Bundle b = getIntent().getExtras();
      //  String event_id = b.getString("EventID");
        String  event_id = "-KVkHLDRUTMAOxF_XmfU";
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbEvent = db.getReference("events/" + event_id);
        tvDetails = (TextView) findViewById(R.id.tvDetails);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                event = dataSnapshot.getValue(BUEvent.class);
                tvDetails.setText(event.getEventTitle());
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
