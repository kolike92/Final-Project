/**
 * Class HomeActivity
 * @author NOGE
 * Superclass: InnerActivity
 * Display the list of upcoming activities. Allow search and filtering.
 */

package com.BUddy.android;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class HomeActivity extends InnerActivity implements SearchFragment.DialogListener {
    private ImageButton ibtnProfile;
    private ListView lvActivities;
    private Button btnFilter, btnClearFilters;
    private Button btnCreatePost, btnRefreshResults;


    private boolean isFiltered;

    ListAdapter lvAdapater;

    private ArrayList<BUEvent> events, filteredEvents;
    private FirebaseDatabase firebaseDatabase;

    private FragmentManager fragMan;

    //if we had previously filtered, restore filter results (instead of reloading everything)
    //otherwise reload events
    @Override
protected void onRestoreInstanceState(Bundle savedInstanceState)
{
    super.onRestoreInstanceState(savedInstanceState);
   //reload activities in case any were added
    isFiltered = savedInstanceState.getBoolean(StaticConstants.FILTERED_KEY);

    if(!isFiltered) {
        showAllEvents();
    }
    else
    {
        //get saved event list
        filteredEvents = savedInstanceState.getParcelableArrayList(StaticConstants.FILTERED_EVENTS_KEY);
        lvAdapater = new EventListAdapter(getBaseContext(), filteredEvents, firebaseDatabase,user);
        lvActivities.setAdapter(lvAdapater);
    }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        isFiltered = false;
        if(savedInstanceState != null)
        {
            //if event was filtered and destroyed, show filtered events
            isFiltered = savedInstanceState.getBoolean(StaticConstants.FILTERED_KEY);
            filteredEvents = savedInstanceState.getParcelableArrayList(StaticConstants.FILTERED_EVENTS_KEY);
        }

        fragMan = getFragmentManager();

        events = new ArrayList<BUEvent>();

        //set up UI
        ibtnProfile = (ImageButton) findViewById(R.id.ibtnProfile);
        lvActivities = (ListView) findViewById(R.id.lvActivities);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnCreatePost = (Button) findViewById(R.id.btnCreatePost);
        btnRefreshResults = (Button) findViewById(R.id.btnRefresh);
        Bundle b = getIntent().getExtras();
        btnClearFilters = (Button) findViewById(R.id.btnClearFilters);

        //get user from bundle
        user = getIntent().getExtras().getParcelable(StaticConstants.USER_KEY);

        Firebase.setAndroidContext(this);


        firebaseDatabase = FirebaseDatabase.getInstance();

        //if event is filter, show from filteredEvents list instead of full events list
        if(!isFiltered) showAllEvents();
        else
        {
            lvAdapater = new EventListAdapter(getBaseContext(), filteredEvents, firebaseDatabase,user);
            lvActivities.setAdapter(lvAdapater);
        }

        //clear search results, show all upcoming events
        btnClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFiltered = false;
                showAllEvents();
            }
        });

        //pop out search fragment
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment f = new SearchFragment();
                FragmentTransaction ft = fragMan.beginTransaction();
                f.show(fragMan,"Search events");
            }
        });

        //on activity click, go to the EventDetail page
        lvActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BUEvent e = events.get(position);
                String eventId = e.getFirebaseId();
                Intent intent = new Intent(getBaseContext(), EventDetail.class);
                //pass eid and event for safety, always pass user
                intent.putExtra(StaticConstants.EID_KEY, eventId);
                intent.putExtra(StaticConstants.EVENT_KEY, e);
                intent.putExtra(StaticConstants.USER_KEY,user);
                startActivity(intent);
            }
        });

        //on click, go to CreateEventActivity
        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createEvent = new Intent(getBaseContext(), CreateEventActivity.class);
                Bundle b = new Bundle();
                //always pass the user
                b.putParcelable(StaticConstants.USER_KEY,user);
                createEvent.putExtras(b);
                startActivity(createEvent);
            }
        });

        //on click, go to user Profile
        ibtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(getBaseContext(), Profile.class);
                Bundle b = new Bundle();
                //always pass the user
                b.putParcelable(StaticConstants.USER_KEY,user);

                profile.putExtras(b);
                startActivity(profile);
            }
        });

        //on click, search for new events and display
        btnRefreshResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllEvents();
            }
        });


    }

    //if event is stopped, save filtered state
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState)
    {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putParcelableArrayList(StaticConstants.FILTERED_EVENTS_KEY, filteredEvents);
        saveInstanceState.putBoolean(StaticConstants.FILTERED_KEY, isFiltered);
    }


    //display results of filter fragment
    @Override
    public void onFinishEditDialog(ArrayList<BUEvent> eventList, boolean cancel) {
        if(cancel) return; //do nothing

        //store events in filteredEvents
        filteredEvents = eventList;

        //have adapter look at filteredEvents instead of events
        lvAdapater = new EventListAdapter(getBaseContext(), filteredEvents, firebaseDatabase, user);
        lvActivities.setAdapter(lvAdapater);
        isFiltered = true;

        //no events found
        if(filteredEvents.size() == 0) Toast.makeText(getBaseContext(),
                getString(R.string.no_results_found), Toast.LENGTH_LONG).show();


    }

    //show all upcoming events in DB (no filters)
    private void showAllEvents()
    {
        //empty events list
        events.clear();
        long now = System.currentTimeMillis();

        //get all events after now, sorted by date (increasing)
        Query q = firebaseDatabase.getReference("events").orderByChild("eventDate/time").startAt(now);
        q.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Iterable<com.google.firebase.database.DataSnapshot> children = dataSnapshot.getChildren();
                    for(com.google.firebase.database.DataSnapshot d: children)
                    {
                        //Each child is an event in the DB
                        BUEvent eAdd = d.getValue(BUEvent.class);
                        if(eAdd.getFirebaseId() == null)
                        {
                            //old events may not have firebase Ids, add them now
                            eAdd.setFirebaseId(d.getKey());

                        }
                        events.add(eAdd);
                    }

                    //after all events added, point the adapter to the events list
                    lvAdapater = new EventListAdapter(getBaseContext(), events, firebaseDatabase, user);
                    lvActivities.setAdapter(lvAdapater);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}



