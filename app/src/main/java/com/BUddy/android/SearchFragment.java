package com.BUddy.android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private CheckBox cbFood, cbSports, cbStudyBreak, cbMovie, cbExploring, cbOther;
    private ArrayList<CheckBox> boxes;
    private EditText etSearch;
    private Button btnSearch;
    private ArrayList<BUEvent> events;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;


    public SearchFragment() {
        // Required empty public constructor
    }

    private int getIdFromCheckbox(CheckBox c)
    {
        if(c.getId() == R.id.checkFood) return EventCategory.FOOD.getId();
        if(c.getId() == R.id.checkSports) return EventCategory.SPORTS.getId();
        if(c.getId() == R.id.checkMovie) return EventCategory.MOVIE.getId();
        if(c.getId() == R.id.checkExploring) return EventCategory.EXPLORING.getId();
        if(c.getId() == R.id.checkStudyBreak) return EventCategory.STUDY_BREAK.getId();
        else return EventCategory.OTHER.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        cbFood = (CheckBox) v.findViewById(R.id.checkFood);
        cbSports = (CheckBox) v.findViewById(R.id.checkSports);
        cbStudyBreak = (CheckBox) v.findViewById(R.id.checkStudyBreak);
        cbMovie = (CheckBox) v.findViewById(R.id.checkMovie);
        cbExploring = (CheckBox) v.findViewById(R.id.checkExploring);
        cbOther = (CheckBox) v.findViewById(R.id.checkOther);

        boxes = new ArrayList<CheckBox>();
        boxes.add(cbFood);
        boxes.add(cbSports);
        boxes.add(cbStudyBreak);
        boxes.add(cbMovie);
        boxes.add(cbExploring);
        boxes.add(cbOther);


        etSearch = (EditText) v.findViewById(R.id.etSearch);
        btnSearch = (Button) v.findViewById(R.id.btnSearch);

        events = new ArrayList<BUEvent>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("events");


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String q_string = etSearch.getText().toString();
                Query q;
                for(CheckBox box: boxes)
                {
                    if(box.isChecked())
                    {
                        int id = getIdFromCheckbox(box);
                        q = dbRef.orderByChild("category").equalTo(id);
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                BUEvent e = dataSnapshot.getValue(BUEvent.class);
                                if(e.getEventTitle().contains(q_string)) events.add(e);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                for(BUEvent ev: events)
                {
                    Log.d("BUDDY", ev.getEventTitle());
                }
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

}
