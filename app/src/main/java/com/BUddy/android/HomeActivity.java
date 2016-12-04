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


/**
 * Created by Sophia_ on 10/31/16.
 */

public class HomeActivity extends InnerActivity implements SearchFragment.DialogListener {
    private ImageButton ibtnProfile;
    private ListView lvActivities;
    private Button btnFilter, btnClearFilters;
    private Button btnCreatePost;


    private boolean isFiltered;

    ListAdapter lvAdapater;

    private ArrayList<BUEvent> events, filteredEvents;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;

    private FragmentManager fragMan;

    @Override
protected void onRestoreInstanceState(Bundle savedInstanceState)
{
    super.onRestoreInstanceState(savedInstanceState);
   //reload activities in case any were added
    isFiltered = savedInstanceState.getBoolean(StaticConstants.FILTERED_KEY);

    if(!isFiltered) {
        events.clear();
        long now = System.currentTimeMillis();
        showAllEvents();
    }
    else
    {
        filteredEvents = savedInstanceState.getParcelableArrayList(StaticConstants.FILTERED_EVENTS_KEY);
        lvAdapater = new EventListAdapter(getBaseContext(), filteredEvents, firebaseDatabase);
        lvActivities.setAdapter(lvAdapater);
    }
}


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(StaticConstants.TAG, "onCreate called in HomeActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        isFiltered = false;
        if(savedInstanceState != null)
        {
            isFiltered = savedInstanceState.getBoolean(StaticConstants.FILTERED_KEY);
            filteredEvents = savedInstanceState.getParcelableArrayList(StaticConstants.FILTERED_EVENTS_KEY);
        }

        fragMan = getFragmentManager();

        events = new ArrayList<BUEvent>();
        ibtnProfile = (ImageButton) findViewById(R.id.ibtnProfile);
        lvActivities = (ListView) findViewById(R.id.lvActivities);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnCreatePost = (Button) findViewById(R.id.btnCreatePost);
        Bundle b = getIntent().getExtras();
        btnClearFilters = (Button) findViewById(R.id.btnClearFilters);
        user = getIntent().getExtras().getParcelable(StaticConstants.USER_KEY);

        Firebase.setAndroidContext(this);


        firebaseDatabase = FirebaseDatabase.getInstance();

        if(!isFiltered) showAllEvents();
        else
        {
            lvAdapater = new EventListAdapter(getBaseContext(), filteredEvents, firebaseDatabase);
            lvActivities.setAdapter(lvAdapater);
        }

        btnClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFiltered = false;
                showAllEvents();
            }
        });


        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment f = new SearchFragment();
                FragmentTransaction ft = fragMan.beginTransaction();
                f.show(fragMan,"Search events");
            }
        });

        lvActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BUEvent e = events.get(position);
                String eventId = e.getFirebaseId();
                Intent intent = new Intent(getBaseContext(), EventDetail.class);
                intent.putExtra(StaticConstants.EID_KEY, eventId);
                intent.putExtra(StaticConstants.EVENT_KEY, e);
                intent.putExtra(StaticConstants.USER_KEY,user);
                startActivity(intent);
            }
        });

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createEvent = new Intent(getBaseContext(), CreateEventActivity.class);
                Bundle b = new Bundle();
                b.putParcelable(StaticConstants.USER_KEY,user);

                createEvent.putExtras(b);
                startActivity(createEvent);
            }
        });

        ibtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(getBaseContext(), Profile.class);
                Bundle b = new Bundle();
                b.putParcelable(StaticConstants.USER_KEY,user);

                profile.putExtras(b);
                startActivity(profile);
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState)
    {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putParcelableArrayList(StaticConstants.FILTERED_EVENTS_KEY, filteredEvents);
        saveInstanceState.putBoolean(StaticConstants.FILTERED_KEY, isFiltered);
    }


    @Override
    public void onFinishEditDialog(ArrayList<BUEvent> eventList, boolean cancel) {
        if(cancel) return; //do nothing
        filteredEvents = eventList;
        lvAdapater = new EventListAdapter(getBaseContext(), filteredEvents, firebaseDatabase);
        lvActivities.setAdapter(lvAdapater);
        isFiltered = true;
        if(filteredEvents.size() == 0) Toast.makeText(getBaseContext(),
                "No results found", Toast.LENGTH_LONG).show();


    }

    private void showAllEvents()
    {
        long now = System.currentTimeMillis();
        dbRef = firebaseDatabase.getReference("events");
        Query q = firebaseDatabase.getReference("events").orderByChild("eventDate/time").startAt(now);
        q.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Iterable<com.google.firebase.database.DataSnapshot> children = dataSnapshot.getChildren();
                    for(com.google.firebase.database.DataSnapshot d: children)
                    {
                        BUEvent eAdd = d.getValue(BUEvent.class);
                        if(eAdd.getFirebaseId() == null)
                        {
                            //old events may not have firebase Ids, add them now
                            eAdd.setFirebaseId(d.getKey());

                        }
                        events.add(eAdd);
                    }

                    lvAdapater = new EventListAdapter(getBaseContext(), events, firebaseDatabase);
                    lvActivities.setAdapter(lvAdapater);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

class EventListAdapter extends BaseAdapter {
    private ArrayList<BUEvent> buEvents;
    private FirebaseDatabase firebaseDatabase;

   // private TextView tvCreatedBy; //this is passed around




    Context context;

    public EventListAdapter(Context aContext, ArrayList<BUEvent> buEvents, FirebaseDatabase firebaseDatabase) {
        context = aContext;
        this.buEvents = buEvents;
        this.firebaseDatabase = firebaseDatabase;

    }

    @Override
    public int getCount() {
        return buEvents.size();
    }
    @Override
    public long getItemId(int position){
        return position;
    }


     @Override
    public Object getItem(int position) {
        return buEvents.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = (View) inflater.inflate(R.layout.listview_row, parent, false);
        }
        else {
            row = convertView;
        }

        TextView tvEventTitle = (TextView) row.findViewById(R.id.tvEventTitle);
        TextView tvEventDescription = (TextView) row.findViewById(R.id.tvEventDescription);
        TextView tvCreateTime = (TextView) row.findViewById(R.id.tvCreateTime);
        final TextView tvCreatedBy = (TextView) row.findViewById(R.id.tvEventOwner);

        try{
            BUEvent thisEvent = buEvents.get(position);
            tvEventTitle.setText(thisEvent.getEventTitle());
            tvEventDescription.setText(thisEvent.getEventDetails());

            DatabaseReference dbCreator = firebaseDatabase.getReference("users/" + thisEvent.getCreator());
            dbCreator.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        BuddyUser creator = dataSnapshot.getValue(BuddyUser.class);
                        if(creator != null) tvCreatedBy.setText(creator.getEmail());


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            tvCreateTime.setText(StaticConstants.SDF.format(thisEvent.getEventDate()));
        }
        catch (Exception e){}




        return row;


    }



}
