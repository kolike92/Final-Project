package com.example.link.buddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * Created by Sophia_ on 10/31/16.
 */

public class HomeActivity extends AppCompatActivity {
    private ImageButton ibtnProfile;
    private ListView lvActivities;
    private Button btnFilter;
    private Button btnCreatePost;
    private TextView tvDate;
    private
    String titles[] = new String[20];
    String descriptions[] = new String[20];
    long categories[] = new long[20];
    String eid[] = new String[20];
    ListAdapter lvAdapater;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ibtnProfile = (ImageButton) findViewById(R.id.ibtnProfile);
        lvActivities = (ListView) findViewById(R.id.lvActivities);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnCreatePost = (Button) findViewById(R.id.btnCreatePost);
        tvDate = (TextView) findViewById(R.id.tvDate);
        Firebase.setAndroidContext(this);



        Firebase ref = new Firebase("https://buddy-a4223.firebaseio.com/");





        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    for (DataSnapshot child_c : child.getChildren()) {
                        if ( count >= 20) {
                            break;
                        }
                        Log.d("Buddy data retrieve", child_c.getValue().toString());
                        titles[count] = (String) child_c.child("eventTitle").getValue();
                        descriptions[count]=(String) child_c.child("eventDetails").getValue();
                        categories[count] = (long) child_c.child("category").getValue();
                        eid[count] = (String) child_c.getKey();

                        /*
                        if ( child_c.getValue().equals("eventTitle")) {
                            titles[count] = child_c.getValue().toString();
                        }
                        */

                        /*
                        titles[count] = child_c.getKey();
                        descriptions[count] = child_c.getChildren().toString();
                        times[count] = child_c.getChildren().toString();
                        owners[count] = child_c.getChildren().toString();
                        */
                        count++;
                    }
                }

            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        lvAdapater = new EventListAdapter(this.getBaseContext(), titles, descriptions, categories);
        lvActivities.setAdapter(lvAdapater);


        lvActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String eventId = eid[position];
                Intent intent = new Intent(getBaseContext(), EventDetail.class);
                intent.putExtra("EventID", eventId);
                startActivity(intent);
            }
        });








    }



}

class EventListAdapter extends BaseAdapter {

    private String eventTitles[];
    private long categories[];
    private String eventDescriptions[];

    //private String createTimes[];
    //private String createdBy[];


    Context context;

    public EventListAdapter(Context aContext,String[] EventTitles, String[] EventDescriptions, long[] c) {
        context = aContext;
        eventTitles = EventTitles;
        eventDescriptions = EventDescriptions;
        categories = c;
        //createTimes = CreateTimes;
        //createdBy = CreatedBy;

    }

    @Override
    public int getCount() {
        return eventTitles.length;
    }
    @Override
    public long getItemId(int position){
        return position;
    }


     @Override
    public Object getItem(int position) {
        return position;
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
        TextView tvCreatedBy = (TextView) row.findViewById(R.id.tvCreatedBy);

        tvEventTitle.setText(eventTitles[position]);
        tvEventDescription.setText(eventDescriptions[position]);



        return row;


    }
}