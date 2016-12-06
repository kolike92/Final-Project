package com.BUddy.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Sophia_ on 10/31/16.
 */

public class Profile extends InnerActivity {

    private TextView tvNameShow;
    private EditText etName;
    private TextView tvPhoneNumShow;
    private EditText etPhoneNum;
    private TextView tvEmailShow;
    private EditText etEmail;
    private TextView tvDoBShow;
    private EditText etDoB;
    private TextView tvLikes;
    private TextView tvUpAct;
    private ListView lvUpAct;
    private TextView tvPstAct;
    private ListView lvPstAct;
    private TextView tvYourAct;
    private ListView lvYourAct;
    private TextView tvLiked;    private Button btnSave;
    private ListAdapter upActAdapter;
    private ListAdapter pstActAdapter;
    private ListAdapter yourActAdapter;

    private ArrayList<BUEvent> upEvents;
    private ArrayList<BUEvent> pstEvents;
    private ArrayList<BUEvent> yourEvents;




    /* events database connection setup*/
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbEvent = db.getReference("events/");
    private DatabaseReference dbUser = db.getReference("users/");


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*
         Set Reference
         */
        tvNameShow = (TextView) findViewById(R.id.tvNameShow);
        etName = (EditText) findViewById(R.id.etName);
        tvPhoneNumShow = (TextView) findViewById(R.id.tvPhoneNumShow);
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        tvEmailShow = (TextView) findViewById(R.id.tvEmailShow);
        etEmail = (EditText) findViewById(R.id.etEmail);
        tvDoBShow = (TextView) findViewById(R.id.tvDoBShow);
        etDoB = (EditText) findViewById(R.id.etDoB);
        tvLikes = (TextView) findViewById(R.id.tvLikes);
        tvUpAct = (TextView) findViewById(R.id.tvUpAct);
        lvUpAct = (ListView) findViewById(R.id.lvUpAct);
        tvPstAct = (TextView) findViewById(R.id.tvPstAct);
        lvPstAct = (ListView) findViewById(R.id.lvPstAct);
        tvYourAct = (TextView) findViewById(R.id.tvYourAct);
        lvYourAct = (ListView) findViewById(R.id.lvYourAct);

        btnSave = (Button) findViewById(R.id.btnSave);

        upEvents = new ArrayList<BUEvent>();
        yourEvents = new ArrayList<BUEvent>();
        pstEvents = new ArrayList<BUEvent>();



        user = getIntent().getExtras().getParcelable(StaticConstants.USER_KEY);

        /* fill in user information */
        try {
            etName.setText(user.getName());
        }
        catch (Exception e) {}
        try {
            etEmail.setText(user.getEmail());
        } catch (Exception e) {}
        try {
            etPhoneNum.setText(user.getPhoneNum());
        } catch (Exception e) {}
        try {
            etDoB.setText(user.getDob());
        } catch (Exception e) {}






        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        /* event database listener */
        dbEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                        /*
                         * add event to owned event list
                         */
                    if (child.child("creator").getValue().toString().equals(user.getFirebaseId())) {            // if event creator's ID is the user's  firebase ID
                        BUEvent eAdd = child.getValue(BUEvent.class);
                        yourEvents.add(eAdd);
                    }

                        /*
                         * find participate events
                         */

                    if (child.child("participants").exists()) {
                        String p = child.child("participants").getValue().toString();
                        Log.d("participants",p);
                        p = p.substring(1,(p.length()-1));
                        String[] participants_list = p.split(",");
                        Log.d("participants 0", participants_list[0]);
                        try {
                            if (Arrays.asList(participants_list).contains(user.getFirebaseId())) {          // if the user's fireabase ID is in the participants_list
                                BUEvent eAdd = child.getValue(BUEvent.class);
                                Date currentDate = new Date();                                              // current time
                                if (eAdd.getEventDate().before(currentDate))                                // if the event happened before current time
                                    pstEvents.add(eAdd);                                                    // add event to the past event list
                                else
                                    upEvents.add(eAdd);                                                     // else add event to the up coming event list
                            }
                        } catch (NullPointerException e) {
                            Log.d("Null Pointer Exception", "No Event Date");
                        }
                    }



                }
            }


            @Override
            public void onCancelled (DatabaseError databaseError){

            }

        });

        /*
         *
         *
         *
         * List View Adapter
         *
         *
         */
        /* Up Coming Events*/
        try {
            upActAdapter = new EventListAdapter(this.getBaseContext(), upEvents, FirebaseDatabase.getInstance(), user);
            lvUpAct.setAdapter(upActAdapter);
            lvUpAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BUEvent e = upEvents.get(position);
                    String eventId = e.getFirebaseId();
                    Intent intent = new Intent(getBaseContext(), EventDetail.class);
                    intent.putExtra(StaticConstants.EID_KEY, eventId);
                    intent.putExtra(StaticConstants.EVENT_KEY, e);
                    intent.putExtra(StaticConstants.USER_KEY, user);
                    startActivity(intent);
                }
            });
        }
        catch (NullPointerException e) {}
        /*
        Past Events
        */
        try {
            pstActAdapter = new EventListAdapter(getBaseContext(), pstEvents, FirebaseDatabase.getInstance(),user);
            lvPstAct.setAdapter(pstActAdapter);

            lvPstAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BUEvent e = pstEvents.get(position);
                    String eventId = e.getFirebaseId();
                    Intent intent = new Intent(getBaseContext(), EventDetail.class);
                    intent.putExtra(StaticConstants.EID_KEY, eventId);
                    intent.putExtra(StaticConstants.EVENT_KEY, e);
                    intent.putExtra(StaticConstants.USER_KEY, user);
                    startActivity(intent);
                }
            });
        } catch (NullPointerException e) {}

        /*
         * owned Events
         */
        try {
            yourActAdapter = new EventListAdapter(getBaseContext(), yourEvents, FirebaseDatabase.getInstance(),user);
            lvYourAct.setAdapter(yourActAdapter);


            lvYourAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BUEvent e = yourEvents.get(position);
                    String eventId = e.getFirebaseId();
                    Intent intent = new Intent(getBaseContext(), EventDetail.class);
                    intent.putExtra(StaticConstants.EID_KEY, eventId);
                    intent.putExtra(StaticConstants.EVENT_KEY, e);
                    intent.putExtra(StaticConstants.USER_KEY, user);
                    startActivity(intent);
                }
            });
        } catch (NullPointerException e) {}





        // Click on btnSave: save change in personal info to db
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Query q = dbUser.orderByKey().equalTo(user.getFirebaseId());

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // something wrong
                        } else {
                            // update
                            Log.d("firebase",dbUser.child(user.getFirebaseId()).child("name").toString());
                            user.setName(etName.getText().toString());
                            dbUser.child(user.getFirebaseId()).child("name").setValue(etName.getText().toString());
                            user.setPhoneNum(etName.getText().toString());
                            dbUser.child(user.getFirebaseId()).child("phoneNum").setValue(etPhoneNum.getText().toString());
                            dbUser.child(user.getFirebaseId()).child("dob").setValue(etDoB.getText().toString());
                            Toast.makeText(getApplicationContext(),"Your information has been saved.",Toast.LENGTH_SHORT).show();
                        }



                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });






    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Profile Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


}
