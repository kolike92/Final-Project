/**
 * Class: EventDetail
 * @author NOGE
 * Superclass: InnerActivity
 * The page for viewing, joining, liking, and editing events. Also used for messaging event creators
 * and checking event locations on a map
 */


package com.BUddy.android;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.net.Uri;

import android.os.Bundle;
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

import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class EventDetail extends InnerActivity{

    //Views
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
    private EditText tvnumpeopleSet;

    private String eventId;


    private DatabaseReference dbUser;
    private DatabaseReference dbEvent;

//booleans for user relationship to event
    private boolean joined;
    private boolean isOwner;
    private boolean liked;
    private boolean past;


    private  Button btnMap;
    private String creatorPhoneNo;
    private String creatorString;

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
            joined = savedInstanceState.getBoolean(StaticConstants.JOINED_KEY);
        }

        //if we have a bundle, get the information from there
        else if (b != null && (user == null || event == null))
        {
            user = (BuddyUser) b.getParcelable(StaticConstants.USER_KEY);
            event = (BUEvent) b.getParcelable(StaticConstants.EVENT_KEY);
            eventId =  b.getString(StaticConstants.EID_KEY);
        }

        //user is already a member of event
        if(user.getEids().contains(eventId))
        {
            joined = true;
        }

        //user has already liked event
        if(user.getLikes().contains(eventId))
        {
            liked = true;
        }

        //check if event has passed (cannot be joined, only liked)
        long now = System.currentTimeMillis();
        if(event.getEventDate() == null || now > event.getEventDate().getTime() )
        {
            past = true;
        }


        //database handles
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbEvent = db.getReference("events/" + eventId);
        dbUser = db.getReference("users/" + user.getFirebaseId());

        

        //Set up views
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
        setJoinButtonText();


        //Button to message creator
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(creatorPhoneNo != null && !creatorPhoneNo.equals("")) {
                    //we have a phone number to message
                    String message = getString(R.string.question_about_event);

                    //implicit intent to sms activity
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", message);
                    sendIntent.putExtra("address", creatorPhoneNo);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    try {
                        startActivity(sendIntent);
                    }
                    catch(ActivityNotFoundException ane)
                    {
                        //No texting activity, toast an error
                        Toast.makeText(getApplicationContext(),getString(R.string.unable_to_find_texting_activity),
                                Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    //If there is no number, the button shouldn't be clickable, but just in case, toast an error
                    Log.e(StaticConstants.TAG, "Attempt to message nonexistent creator. Button should not be clickable");
                    Toast.makeText(getApplicationContext(),getString(R.string.unable_to_find_creator_number),
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        //button to show location on map
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intentData = tvLocationSet.getText().toString();;
                Uri gmmIntentUri = Uri.parse("geo:42.35,-71.11?q="+intentData);     //The  geo:42.35,-71.11 will ensure the search is around BU campus
                Intent NextScreen = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                //To verify that an app is available to receive the intent, call resolveActivity() on your Intent object. If the result is non-null, there is at least one app that can handle the intent
                // and it's safe to call startActivity(). If the result is null, you should not use the intent and, if possible, you should disable the feature that invokes the intent.
                if (NextScreen.resolveActivity(getPackageManager()) != null) {
                    startActivity(NextScreen);
                }
            }

        });

        //overloaded button for user interaction with event
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the user owns the event, the button is used to cancel the event
               if (isOwner)
                {
                //cancel event
                    dbEvent.removeValue();
                    Toast.makeText(getApplicationContext(), getString(R.string.canceling_event), Toast.LENGTH_LONG).show();
                    Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                    //go to the home page. Always pass user
                    home.putExtra(StaticConstants.USER_KEY, user);
                    startActivity(home);
                    return;
                }


               //Firebase does not not really do transactions. It's closest approximation
                // is to load an object, change it, check to see if the DB object still matches
                // the one we loaded, and, if it does, make the change. Otherwise, try again.
                dbEvent.runTransaction(new Transaction.Handler() {
                    //Transaction 1: Update event
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            BUEvent current = mutableData.getValue(BUEvent.class);

                            //update event object to match the one in the DB
                            if (current != null) {
                                event = current;
                            }

                            //someone else has joined and the event is now full. abort
                            if (event.getParticipants().size() > event.getMaxParticipants()) {
                                Toast.makeText(getBaseContext(), getString(R.string.beat_to_it), Toast.LENGTH_LONG);
                                return Transaction.abort();
                            } else {
                                //if the user had previously joined event, they are now leaving it
                                if (!past && joined) {
                                    event.removeParticipant(user.getFirebaseId());
                                 //if the user had not previously joined event, they are joining
                                } else if(!past && !joined) {
                                    event.addParticipant(user.getFirebaseId());
                                }
                                //if the user liked the event (can only like past events), remove the like
                                else if(past && liked)
                                {
                                    event.removeLike();
                                }
                                //if the user had not yet liked the event, like it
                                else
                                {
                                    event.addLike();
                                }

                                //attempt to update DB
                                mutableData.setValue(event);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            if(databaseError != null)
                            {
                                Log.e(StaticConstants.TAG, "Error updating the events database: " + databaseError.getMessage());
                            }
                        }
                    });

                //update user object. Ideally, this would be in a transaction with the event update, but
                //Firebase doesn't have transactions
                dbUser.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        BuddyUser current = mutableData.getValue(BuddyUser.class);
                        if (current != null) {
                            user = current;
                        }
                        //if user has previously joined event, leave it
                            if(!past && joined) {
                                user.removeEvent(event.getFirebaseId());
                            }
                            //if user has not joined event, join it
                            else if (!past && !joined) {
                                user.addEvent(event.getFirebaseId());
                            }
                            //if user has liked event, unlike it
                        else if(past && liked)
                            {
                                user.removeLike(event.getFirebaseId());
                            }
                            //if user has not liked event, like it
                        else
                            {
                                user.addLike(event.getFirebaseId());
                            }
                            mutableData.setValue(user);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if(databaseError != null) Log.e(StaticConstants.TAG, databaseError.getMessage());
                        else {
                            //transaction was successful, update Activity state
                            if(!past) joined = !joined;
                            else liked = !liked;

                            setJoinButtonText();

                            int resId = 0;
                            if(!past && joined) resId = R.string.joined_event;
                            if(!past && !joined) resId = R.string.left_event;
                            if(past && liked) resId = R.string.liked_event;
                            if(past && !liked) resId = R.string.unliked_event;

                            //Toast to let the user know
                            Toast.makeText(getApplicationContext(),
                                    getString(resId)  + event.getEventTitle(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //"Queries" db for event information (firebase doesn't actually have queries, but
                // that is essentially what listeners do)

                if(dataSnapshot.exists()) {
                    //event found, update UI with event information
                    event = dataSnapshot.getValue(BUEvent.class);
                    tvTitleSet.setText(event.getEventTitle());
                    tvDateSet.setText(StaticConstants.SDF.format(event.getEventDate()));
                    Calendar c = Calendar.getInstance();
                    Date now = c.getTime();

                    tvLocationSet.setText(event.getLocation());
                    String s = String.valueOf(event.getMaxParticipants());
                    String s2 = String.valueOf(event.getParticipants().size());
                    tvnumpeopleSet.setText(s2+"/"+s);
                    tvCategories.setText(EventCategory.getById(event.getCategory()).getName(getApplicationContext()));
                    tvDetailsSet.setText(event.getEventDetails());

                    //if the user is the creator, the join button is used for canceling and
                    //the event details can be changed in the UI
                    if (event.getCreator() != null && event.getCreator().equals(user.getFirebaseId())) {
                        isOwner = true;
                        btnJoin.setText(getString(R.string.cancel_event));
                        tvTitleSet.setEnabled(true);
                        tvTitleSet.addTextChangedListener(new StringChangeEventListener("eventTitle"));
                        tvDetailsSet.setEnabled(true);
                        tvDetailsSet.addTextChangedListener(new StringChangeEventListener("eventDetails"));
                        tvDateSet.setEnabled(true);
                        tvDateSet.addTextChangedListener(new DateChangeEventListener("eventDate"));
                        tvLocationSet.setEnabled(true);
                        tvLocationSet.addTextChangedListener(new StringChangeEventListener("eventLocation"));

                    } else {
                        //user is not owner, cannot update information
                        tvTitleSet.setEnabled(false);
                        tvDateSet.setEnabled(false);
                        tvLocationSet.setEnabled(false);
                        tvTitleSet.setEnabled(false);
                        tvDetailsSet.setEnabled(false);

                        //event is full, disable join button
                        if (event.getParticipants().size() >= event.getMaxParticipants()) {
                            btnJoin.setClickable(false);
                            btnJoin.setText(getString(R.string.event_full));
                            btnJoin.setClickable(false);
                        }
                    }
                }

                //get event creator information
                creatorString = event.getCreator();
                DatabaseReference dbCreator = db.getReference("users/" + creatorString);

                //"query" db for creator information
                ValueEventListener creatorListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //update Activity variables with DB info
                        creator = dataSnapshot.getValue(BuddyUser.class);
                        creatorPhoneNo = creator.getPhoneNum();
                        //no phone number, make button unclickable
                        if(creatorPhoneNo == null || creatorPhoneNo.equals(""))
                        {
                            btnMessage.setClickable(false);
                            btnMessage.setText(getString(R.string.no_phone_number));
                        }
                        else
                        {
                            btnMessage.setClickable(true);
                            btnMessage.setText(getString(R.string.message));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                       Log.e(StaticConstants.TAG, "DB error: " + databaseError.getMessage());
                    }
                };

                //the actual query happens here
                dbCreator.addListenerForSingleValueEvent(creatorListener);


            }

            //onCancel for event ValuePostListener
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(StaticConstants.TAG, "DB error: " + databaseError.getMessage());
            }
        };

        //add listener to handle
        dbEvent.addListenerForSingleValueEvent(postListener);
    }


    private void setJoinButtonText()
    {
        //set join button text to reflect current functionality
        if(!past)
        {
            if (joined) btnJoin.setText(getString(R.string.leave_event));
            else btnJoin.setText(getString(R.string.join_event));
        }
        else
        {
            if(joined) {
                if (liked) btnJoin.setText(getString(R.string.like_event));
                else btnJoin.setText(R.string.unlike_event);
                btnJoin.setClickable(true);
            }
            else {
                btnJoin.setText(getString(R.string.event_passed));
                btnJoin.setClickable(false);
            }
        }

    }


    //make sure we save user and event state
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable(StaticConstants.USER_KEY,user);
        savedInstanceState.putParcelable(StaticConstants.EVENT_KEY, event);
        savedInstanceState.putString(StaticConstants.EID_KEY,event.getFirebaseId());
        savedInstanceState.putBoolean(StaticConstants.JOINED_KEY,joined);
    }

    //restore user and event state
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        user = savedInstanceState.getParcelable(StaticConstants.USER_KEY);
        event = savedInstanceState.getParcelable(StaticConstants.EVENT_KEY);
        eventId = savedInstanceState.getString(StaticConstants.EID_KEY);
        joined = savedInstanceState.getBoolean("JOINED");


    }



    // Change event details (with string values) to user input if the user is the creator
    private class StringChangeEventListener implements TextWatcher
    {
        String child; //db field name

        public StringChangeEventListener(String sChild)
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
            //save to DB
            String newVal = s.toString();
            DatabaseReference thisRef = dbEvent.child(child);
            thisRef.setValue(newVal);
        }
    }

    // Change event details (with date values) to user input if the user is the creator
    private class DateChangeEventListener implements TextWatcher
    {
        String child; //db field name

        public DateChangeEventListener(String sChild)
        {
            child = sChild;

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        //parse date and save
        @Override
        public void afterTextChanged(Editable s) {
            String newVal = s.toString();
            Date d;
            try {
                d = StaticConstants.SDF.parse(newVal);
            }
            catch (ParseException pe)
            {
                d = null;
            }
            DatabaseReference thisRef = dbEvent.child(child);
            thisRef.setValue(d);
        }
    }

}
