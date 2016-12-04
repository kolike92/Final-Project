package com.BUddy.android;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import com.google.firebase.database.Query;

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
    private EditText tvnumpeopleSet;

    private String eventId;


    private DatabaseReference dbUser;
    private DatabaseReference dbEvent;
    private DatabaseReference dbRef;

    private boolean joined;
    private boolean isOwner;




    private  Button btnMap;
    private String creatorPhoneNo;
    private String creatorString;
    FirebaseDatabase firebaseDatabase;

    private BuddyUser creator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);



        Bundle b = getIntent().getExtras();


        //if this was called from saveInstanceState, get the info from there
        if(savedInstanceState != null)
        {
            user = savedInstanceState.getParcelable(StaticConstants.USER_KEY);
            event = savedInstanceState.getParcelable(StaticConstants.EVENT_KEY);
            eventId = savedInstanceState.getString(StaticConstants.EID_KEY);
            joined = savedInstanceState.getBoolean("JOINED");
        }
        else if (b != null && (user == null || event == null)) //if we have a bundle and we don't already have the info we need
        {
            user = (BuddyUser) b.getParcelable(StaticConstants.USER_KEY);
            event = (BUEvent) b.getParcelable(StaticConstants.EVENT_KEY);
            eventId =  b.getString(StaticConstants.EID_KEY);
        }



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
        btnMap = (Button) findViewById(R.id.btnMap);
        tvnumpeopleSet = (EditText) findViewById(R.id.tvnumpeopleset);



        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(creatorPhoneNo != null && !creatorPhoneNo.equals("")) {

                    String message = "Say Hello";

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", message);
                    sendIntent.putExtra("address", creatorPhoneNo);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    try {
                        startActivity(sendIntent);
                    }
                    catch(ActivityNotFoundException ane)
                    {
                        Toast.makeText(getApplicationContext(),"Unable to find texting activity.",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {

                }

            }
        });


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intentData = tvLocationSet.getText().toString();;
                Uri gmmIntentUri = Uri.parse("geo:42.35,-71.11?q="+intentData);     //The  geo:42.35,-71.11 will ensure the search is around BU campus
                Intent NextScreen = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //NextScreen.setPackage("com.google.android.apps.maps");
                //Package: Calling setPackage("com.google.android.apps.maps") will ensure that the Google Maps app for Android handles the Intent.
                //If the package isn't set, the system will determine which apps can handle the Intent. If multiple apps are available, the user may be asked which app they would like to use.

                //To verify that an app is available to receive the intent, call resolveActivity() on your Intent object. If the result is non-null, there is at least one app that can handle the intent
                // and it's safe to call startActivity(). If the result is null, you should not use the intent and, if possible, you should disable the feature that invokes the intent.
                if (NextScreen.resolveActivity(getPackageManager()) != null) {
                    startActivity(NextScreen);
                }
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
                    String s = String.valueOf(event.getMaxParticipants());
                    String s2 = String.valueOf(event.getParticipants().size());
                    tvnumpeopleSet.setText(s2+"/"+s);
                    tvCategories.setText(EventCategory.getById(event.getCategory()).getName());
                    tvDetailsSet.setText(event.getEventDetails());
                    if (event.getCreator() != null && event.getCreator().equals(user.getFirebaseId())) {
                        isOwner = true;
                        btnJoin.setText("Cancel Event");
                        tvTitleSet.setEnabled(true);
                        tvTitleSet.addTextChangedListener(new ChangeEventListener("eventTitle"));
                        tvDetailsSet.setEnabled(true);
                        tvDetailsSet.addTextChangedListener(new ChangeEventListener("eventDetails"));
                        tvDateSet.setEnabled(true);
                        tvDateSet.addTextChangedListener(new ChangeEventListener("eventDate"));
                        tvLocationSet.setEnabled(true);
                        tvLocationSet.addTextChangedListener(new ChangeEventListener("eventLocation"));

                    } else {
                        tvTitleSet.setEnabled(false);
                        tvDateSet.setEnabled(false);
                        tvLocationSet.setEnabled(false);
                        tvTitleSet.setEnabled(false);
                        if (event.getParticipants().size() >= event.getMaxParticipants()) {
                            btnJoin.setClickable(false);
                            btnJoin.setText("Sorry, this event is full");

                        } else {
                            // btnJoin.setOnClickListener(joinListener);
                        }
                    }
                }

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
                        if(creatorPhoneNo == null || creatorPhoneNo.equals(""))
                        {
                            btnMessage.setClickable(false);
                            btnMessage.setText("No phone number available");
                        }
                        else
                        {
                            btnMessage.setClickable(true);
                            btnMessage.setText(getString(R.string.message));
                        }

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
        savedInstanceState.putBoolean("JOINED",joined);
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

    private class ChangeEventListener implements TextWatcher
    {
        String child;


        public ChangeEventListener(String sChild)
        {
            child = sChild;

        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String newVal = s.toString();
            DatabaseReference thisRef = dbEvent.child(child);
            thisRef.setValue(newVal);
        }
    }


}
