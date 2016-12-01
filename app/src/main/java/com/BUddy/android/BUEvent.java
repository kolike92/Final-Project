package com.BUddy.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rebeccagraber on 11/1/16.
 */

public class BUEvent implements Parcelable{
    private Date eventDate;
    private String eventTitle;
    private String eventDetails;
    private  int maxParticipants;
    private int category;
    private String location;
    private List<String> participants;
    private String creator;
    private String firebaseId; //it's easier to store this inside the object

    private BUEvent(Parcel in) {
        participants = new ArrayList<>();
        eventTitle = in.readString();
        eventDetails = in.readString();
        maxParticipants = in.readInt();
        category = in.readInt();
        location = in.readString();
        in.readStringList(participants);
        creator = in.readString();
        firebaseId = in.readString();


    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BUEvent createFromParcel(Parcel in) {
            return new BUEvent(in);
        }

        @Override
        public BUEvent[] newArray(int size) {
            return new BUEvent[0];
        }

    };


    public BUEvent()
    {
        this.participants = new ArrayList<String>();
    }

    public BUEvent(Date d, String title, int number, String details, int category, String location, String creator)
    {
        eventDate = d;
        eventTitle = title;
        maxParticipants = number;
        eventDetails = details;
        this.category = category;
        this.location = location;
        participants = new ArrayList<String>();
        this.creator = creator;
    }



    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(eventTitle);
        dest.writeString(eventDetails);
        dest.writeInt(maxParticipants);
        dest.writeInt(category);
        dest.writeString(location);
        dest.writeStringList(participants);
        dest.writeString(creator);
        dest.writeString(firebaseId);
        /*
         private Date eventDate;
    private String eventTitle;
    private String eventDetails;
    private  int maxParticipants;
    private int category;
    private String location;
    private List<String> participants;
    private String creator;
    private String firebaseId; //it's easier to store this inside the object

         */
        if(eventDate != null) {
            dest.writeString(StaticConstants.SDF.format(eventDate));
        }
    }


}