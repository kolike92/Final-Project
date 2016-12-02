package com.BUddy.android;

import android.content.Intent;
//<<<<<<< HEAD
//=======
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
//>>>>>>> 810f6264fd8a818c426545aca4e017df9dd6cd7d
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//<<<<<<< HEAD
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
//=======
import com.google.firebase.database.Query;
//>>>>>>> 810f6264fd8a818c426545aca4e017df9dd6cd7d
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Sophia_ on 10/31/16.
 */

public class EventDetail extends InnerActivity{
    private TextView tvTitle;
    private EditText tvTitleSet;
    private TextView tvDate;
    private EditText tvDateSet;
    private TextView tvLocation;
    private EditText tvLocationSet;
    private TextView tvDetails;
    private EditText tvDetailsSet;
    private TextView tvCategories;
    private TextView tvCreatedBy;
    private Button btnMessage;
    private Button btnJoin;
    private BUEvent event;
    private BuddyUser user;
//<<<<<<< HEAD
    private String eventId;


    private DatabaseReference dbUser;
    private DatabaseReference dbEvent;
    private DatabaseReference dbRef;

    private boolean joined;
    private boolean isOwner;



//=======
    private  Button btnMap;
    private String creatorPhoneNo;
    private String creatorString;
    FirebaseDatabase firebaseDatabase;

    private BuddyUser creator;
//>>>>>>> 810f6264fd8a818c426545aca4e017df9dd6cd7d

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

     //   joinListener = new JoinListener();

        Bundle b = getIntent().getExtras();
//<<<<<<< HEAD

        //if this was called from saveInstanceState, get the info from there
        if(savedInstanceState != null)
        {
            user = savedInstanceState.getParcelable(StaticConstants.USER_KEY);
            event = savedInstanceState.getParcelable(StaticConstants.EVENT_KEY);
            eventId = savedInstanceState.getString(StaticConstants.EID_KEY);
        }
        else if (b != null && (user == null || event == null)) //if we have a bundle and we don't already have the info we need
        {
            user = (BuddyUser) b.getParcelable(StaticConstants.USER_KEY);
            event = (BUEvent) b.getParcelable(StaticConstants.EVENT_KEY);
            eventId =  b.getString(StaticConstants.EID_KEY);
        }


//=======

        //String  event_id = "-KVkHLDRUTMAOxF_XmfU";
//>>>>>>> 810f6264fd8a818c426545aca4e017df9dd6cd7d
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbEvent = db.getReference("events/" + eventId);
        dbUser = db.getReference("users/" + user.getFirebaseId());

        

        /* Set reference*/
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitleSet = (EditText) findViewById(R.id.tvTitleSet);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDateSet = (EditText) findViewById(R.id.tvDateSet);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocationSet = (EditText) findViewById(R.id.tvLocationSet);
        tvDetails = (TextView) findViewById(R.id.tvDetails);
        tvDetailsSet = (EditText) findViewById(R.id.tvDetailsSet);
        tvCategories = (TextView) findViewById(R.id.tvCategories);
        tvCreatedBy = (TextView) findViewById(R.id.tvCreatedBy);
        btnMessage = (Button) findViewById(R.id.btnMessage);
        btnJoin = (Button) findViewById(R.id.btnJoin);



        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String message = "Say Hello";

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("sms_body", message);
                sendIntent.putExtra("address", creatorPhoneNo);
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is a weird way of doing it but there is pretty much no good way to update
                //multiple objects in one transaction, so we use 2

               if (isOwner)
                {
                //cancel event
                    dbEvent.removeValue();
                    Toast.makeText(getApplicationContext(), "Canceling event", Toast.LENGTH_LONG).show();
                    Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                    home.putExtra(StaticConstants.USER_KEY, user);
                    startActivity(home);
                    return;
                }
                dbEvent.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        BUEvent current = mutableData.getValue(BUEvent.class);
                        if (current != null) {
                            event = current;
                        }

                        if (event.getParticipants().size() > event.getMaxParticipants()) {
                            Toast.makeText(getBaseContext(), "Oops, someone beat you to it!", Toast.LENGTH_LONG);
                            return Transaction.abort();
                        } else {
                            if(joined) {
                                event.removeParticipant(user.getFirebaseId());
                            }
                            else {
                                event.addParticipant(user.getFirebaseId());
                            }
                            mutableData.setValue(event);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(StaticConstants.TAG, "Yo");
                    }
                });
                dbUser.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        BuddyUser current = mutableData.getValue(BuddyUser.class);
                        if (current != null) {
                            user = current;
                        }

                            if(joined) {
                                user.removeEvent(event.getFirebaseId());
                            }
                            else {
                                user.addEvent(event.getFirebaseId());
                            }
                            mutableData.setValue(user);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if(databaseError != null) Log.d(StaticConstants.TAG, databaseError.getMessage());
                        else {
                            joined = !joined;
                            setJoinButtonText();
                            String left_or_joined = joined ? "joined" : "left";
                            Toast.makeText(getApplicationContext(),
                                    "You have " + left_or_joined + " the activity " + event.getEventTitle(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
//<<<<<<< HEAD
                if(dataSnapshot.exists()) {
                    event = dataSnapshot.getValue(BUEvent.class);
                    tvTitleSet.setText(event.getEventTitle());
                    tvDateSet.setText(StaticConstants.SDF.format(event.getEventDate()));
                    Calendar c = Calendar.getInstance();
                    Date now = c.getTime();
                    if (now.after(event.getEventDate())) {
                        btnJoin.setClickable(false);
                    }


                    tvLocationSet.setText(event.getLocation());
                    tvCategories.setText(EventCategory.getById(event.getCategory()).getName());
                    tvDetailsSet.setText(event.getEventDetails());
                    if (event.getCreator() != null && event.getCreator().equals(user.getFirebaseId())) {
                        isOwner = true;
                        btnJoin.setText("Cancel Event");


                    } else {
                        tvTitleSet.setEnabled(false);
                        tvDateSet.setEnabled(false);
                        tvLocationSet.setEnabled(false);
                        tvTitleSet.setEnabled(false);
                        if (event.getParticipants().size() >= event.getMaxParticipants()) {
                            btnJoin.setText("Sorry, this event is full");

                        } else {
                            // btnJoin.setOnClickListener(joinListener);
                        }
                    }
                }
//=======
                event = dataSnapshot.getValue(BUEvent.class);
                tvTitleSet.setText(event.getEventTitle());
                //tvDateSet.setText(event.getEventDate().toString());
                tvLocationSet.setText(event.getLocation());
                //tvCategories.setText(event.getCategory());
               // tvDetailsSet.setText(event.getEventDetails());
                creatorString = event.getCreator();
                DatabaseReference dbCreator = db.getReference("users/" + creatorString);
                ValueEventListener creatorListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        creator = dataSnapshot.getValue(BuddyUser.class);
                        creatorPhoneNo = creator.getPhoneNum();

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("BUDDY", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
                dbCreator.addListenerForSingleValueEvent(creatorListener);


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


    private void setJoinButtonText()
    {
        if (joined) btnJoin.setText("Leave Event");
        else btnJoin.setText("Join");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable(StaticConstants.USER_KEY,user);
        savedInstanceState.putParcelable(StaticConstants.EVENT_KEY, event);
        savedInstanceState.putString(StaticConstants.EID_KEY,event.getFirebaseId());
    }


//<<<<<<< HEAD
    @Override
    protected void onDestroy()
    {

        Log.d(StaticConstants.TAG, "onDestroy");
        super.onDestroy();
//=======



//>>>>>>> 810f6264fd8a818c426545aca4e017df9dd6cd7d
    }

    @Override
    protected void onStop()
    {

        Log.d(StaticConstants.TAG, "onStop");

        super.onStop();
    }

}
