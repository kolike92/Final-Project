/**
 * Class: EventListAdapter
 * @author NOGE
 * Superclass: BaseAdapter
 * Adapter for displaying a list of events
 */


package com.BUddy.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class EventListAdapter extends BaseAdapter {

    private ArrayList<BUEvent> buEvents;
    private FirebaseDatabase firebaseDatabase;
    private BuddyUser user;

    Context context;

    /**
     * Constructor
     * @param aContext
     * @param buEvents
     * @param firebaseDatabase
     * @param user
     */
    public EventListAdapter(Context aContext, ArrayList<BUEvent> buEvents, FirebaseDatabase firebaseDatabase, BuddyUser user) {
        context = aContext;
        this.buEvents = buEvents;
        this.firebaseDatabase = firebaseDatabase;
        this.user = user;

    }

    @Override
    public int getCount() {
        return buEvents.size();
    }

    @Override
    public long getItemId(int position) {
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
        } else {
            row = convertView;
        }

        //set row View with event details
        TextView tvEventTitle = (TextView) row.findViewById(R.id.tvEventTitle);
        TextView tvEventDescription = (TextView) row.findViewById(R.id.tvEventDescription);
        TextView tvCreateTime = (TextView) row.findViewById(R.id.tvCreateTime);
        TextView tvLiked = (TextView) row.findViewById(R.id.tvLiked);
        final TextView tvCreatedBy = (TextView) row.findViewById(R.id.tvEventOwner);

        try {
            BUEvent thisEvent = buEvents.get(position);
            tvEventTitle.setText(thisEvent.getEventTitle());
            tvEventDescription.setText(thisEvent.getEventDetails());
            if (user.getLikes().contains(thisEvent.getFirebaseId())) {
                tvLiked.setVisibility(View.VISIBLE);
            }

            //get creator name for display
            //reference the list of user objects, look for specific userid
            DatabaseReference dbCreator = firebaseDatabase.getReference("users/" + thisEvent.getCreator());
            dbCreator.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //we found a matching id (i.e. we found the creator)
                        BuddyUser creator = dataSnapshot.getValue(BuddyUser.class);
                        if (creator != null) tvCreatedBy.setText(creator.getEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //show formatted event date
            tvCreateTime.setText(StaticConstants.SDF.format(thisEvent.getEventDate()));
        } catch (Exception e) {
        }


        return row;


    }
}


