/**
 * Class: BUEvent
 * Implements: Parcelable (needed to use in bundles)
 * @author NOGE
 * Stores information about an event: title, date, details, max participants, category, location, current participants,
 * creator, firebaseId, number of likes
 */

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
    private String firebaseId; //For easier DB access
    private int likes;

    /**
     * Create from parcel (needed for Parcelable)
     * @param in
     */
    private BUEvent(Parcel in) {
        participants = new ArrayList<>();
        eventTitle = in.readString();
        eventDetails = in.readString();
        maxParticipants = in.readInt();
        category = in.readInt();
        location = in.readString();
        in.readStringList(participants);
        likes = in.readInt();
        creator = in.readString();
        firebaseId = in.readString();
        try
        {
            String d = in.readString();
            if(d!=null) eventDate = StaticConstants.SDF.parse(d);
            else Log.e(StaticConstants.TAG, "Null event date for event " + firebaseId);
        }

        catch (ParseException pe){ eventDate = null;}

    }


    //static creator needed for Parcelable
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BUEvent createFromParcel(Parcel in) {
            return new BUEvent(in);
        }

        @Override
        public BUEvent[] newArray(int size) {
            return new BUEvent[0];
        }

    };


    /**
     * Constructor
     */
    public BUEvent()
    {
        this.participants = new ArrayList<String>();
    }

    /**
     * Constructor (event date as date)
     * @param d
     * @param title
     * @param number
     * @param details
     * @param category
     * @param location
     * @param creator
     * @param likes
     */
    public BUEvent(Date d, String title, int number, String details, int category, String location, String creator, int likes)
    {
        eventDate = d;
        eventTitle = title;
        maxParticipants = number;
        eventDetails = details;
        this.category = category;
        this.location = location;
        participants = new ArrayList<String>();
        this.creator = creator;
        this.likes = likes;
    }

    /**
     * Constructor (event date as string)
     * @param d
     * @param title
     * @param number
     * @param details
     * @param category
     * @param location
     * @param creator
     * @param likes
     */
    public BUEvent(String d, String title, int number, String details, int category, String location, String creator, int likes)
    {
        try {
            eventDate = StaticConstants.SDF.parse(d);
        }
        catch (ParseException pe)
        {
            eventDate = null;
        }
        eventTitle = title;
        maxParticipants = number;
        eventDetails = details;
        this.category = category;
        this.location = location;
        participants = new ArrayList<String>();
        this.creator = creator;
        this.likes = likes;
    }


    /**
     * Return event date
     * @return
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Set event date
     * @param eventDate
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Return category id (see EventCategory.java)
     * @return
     */
    public int getCategory() {
        return category;
    }

    /**
     * Set category id (see EventCategory.java)
     * @param category
     */
    public void setCategory(int category) {
        this.category = category;
    }

    /**
     * Return event details
     * @return
     */
    public String getEventDetails() {
        return eventDetails;
    }

    /**
     * Set event details
     * @param eventDetails
     */
    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    /**
     * Return event title
     * @return
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Set event title
     * @param eventTitle
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Return the maximum number of participants
     * @return
     */
    public int getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * Set the maximum number of participants
     * @param maxParticipants
     */
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /**
     * Return event location
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set event location
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Return list of participants
     * @return list of BuddyUser firebase ids
     */
    public List<String> getParticipants() {
        return participants;
    }

    /**
     * Return number of likes
     * @return
     */
    public int getLikes() { return likes;}

    /**
     * Set participants
     * @param participants list of BuddyUser firebase ids
     */
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    /**
     * Set number of likes
     * @param likes
     */
    public void setLikes(int likes) { this.likes = likes;}

    /**
     * Return event creator
     * @return Creator's BuddyUser firebase id
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Set creator
     * @param creator Creator's BuddyUser firebase id
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Return firebase GUID
     * @return
     */
    public String getFirebaseId() {
        return firebaseId;
    }

    /**
     * Set firebase GUID
     * @param firebaseId
     */
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    //needed for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write object to parcel (needed for Parcelable)
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(eventTitle);
        dest.writeString(eventDetails);
        dest.writeInt(maxParticipants);
        dest.writeInt(category);
        dest.writeString(location);
        dest.writeStringList(participants);
        dest.writeInt(likes);
        dest.writeString(creator);
        dest.writeString(firebaseId);


        if(eventDate != null) {
            dest.writeString(StaticConstants.SDF.format(eventDate));
        }
    }

    /**
     * Add participant to event
     * @param uid Participant BuddyUser firebase id
     */
    public void addParticipant(String uid)
    {
        if (participants == null) participants = new ArrayList<String>();
        participants.add(uid);
    }

    /**
     * Remove participant from event
     * @param uid Participant BuddyUser firebase id
     */

    public void removeParticipant(String uid)
    {
        if (participants == null) participants = new ArrayList<String>();
        while(participants.remove(uid))
        {
            continue;
        }
    }

    /**
     * Increment likes
     */
    public void addLike()
    {
        likes = likes+1;
    }

    /**
     * Decrement likes
     */
    public void removeLike()
    {
        likes = likes-1;
    }


}