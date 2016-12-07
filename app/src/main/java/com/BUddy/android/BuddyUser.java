/**
 * Class: BuddyUser.java
 * Implements: Parcelable (needed to use in bundles)
 * @author NOGE
 * Stores information about an app user: name, phone number, email, birthday, events attending,
 * events liked, Facebook id, Firebase id
 */
package com.BUddy.android;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;



public class BuddyUser implements Parcelable {
    private String name;
    private String phoneNum;
    private String email;
    private String dob;
    private ArrayList<String> eids;
    private ArrayList<String> likes;
    private String fbId;
    private String firebaseId; //Firebase GUID

    //Static creator object needed for Parcelable
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BuddyUser createFromParcel(Parcel in) {
            return new BuddyUser(in);
        }

        @Override
        public BuddyUser[] newArray(int size) {
            return new BuddyUser[0];
        }

    };

    /**
     * Constructor
     */
    public BuddyUser() {
        eids = new ArrayList<String>();
        likes = new ArrayList<String>();
    }

    /**
     * Constructor
     * @param name
     * @param phoneNum
     * @param email
     * @param dob
     * @param eids
     * @param likes
     * @param fbId
     */
    public BuddyUser(String name, String phoneNum, String email, String dob,
                     ArrayList<String> eids, ArrayList<String> likes, String fbId)
    {
        this.name = name;
        this.phoneNum = phoneNum;
        this.email = email;
        this.dob = dob;
        this.eids = eids;
        this.likes = likes;
        this.fbId = fbId;
    }


    /**
     * Return the name of the user
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Set user name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return user email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set user email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Return event list
     * @return List of event firebase ids
     */
    public ArrayList<String> getEids() {
        return eids;
    }

    /**
     * Set event list
     * @param eids
     */
    public void setEids(ArrayList<String> eids) {
        this.eids = eids;
    }

    /**
     * Return liked events
     * @return List of event firebase ids
     */
    public ArrayList<String> getLikes() {
        return likes;
    }

    /**
     * Set liked event list
     * @param likes
     */
    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    /**
     * Get user phone number
     * @return phone number
     */
    public String getPhoneNum() {
        return phoneNum;
    }

    /**
     * Set user phone number
     * @param phoneNum
     */
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    /**
     * Return user birthday
     * @return formatted date string
     */
    public String getDob() {
        return dob;
    }

    /**
     * Set user birthday
     * @param dob
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    //needed for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Create from parcel
     * @param in
     */
    private BuddyUser(Parcel in) {
        eids = new ArrayList<>();
        likes = new ArrayList<>();
        name = in.readString();
        phoneNum = in.readString();
        email = in.readString();

        dob = in.readString();
        in.readStringList(eids);
        in.readStringList(likes);
        fbId = in.readString();
        firebaseId = in.readString();


    }

    /**
     * Write to parcel (from Parcelable)
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNum);
        dest.writeString(email);
        dest.writeString(dob);
        dest.writeStringList(eids);
        dest.writeStringList(likes);
        dest.writeString(fbId);
        dest.writeString(firebaseId);
    }

    /**
     * Return Facebook id
     * @return Facebook userid
     */
    public String getFbId() {
        return fbId;
    }

    /**
     * Set Facebook Id
     * @param fbId
     */
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    /**
     * Add event to attending events
     * @param eid Event firebase id
     */
    public void addEvent(String eid)
    {
        if(eids == null)
        {
            eids = new ArrayList<String>();
        }
        boolean exists = false;
        //arraylist.contains() doesn't seem to work here
        for(int i = 0; i < eids.size(); i++)
        {
            if(eids.get(i).equals(eid)) exists = true;
        }
        if(!exists) eids.add(eid);
    }

    /**
     * Remove event from attending events
     * @param eid Event firebase id
     */
    public void removeEvent(String eid)
    {
        if(eids == null)
        {
            eids = new ArrayList<String>();
        }
        while(eids.remove(eid))
        {
            continue;
        }
    }

    /**
     * Add event to liked events list
     * @param eid Event firebase id
     */
    public void addLike(String eid)
    {
        if(likes == null)
        {
            likes = new ArrayList<String>();
        }
        boolean exists = false;
        //arraylist.contains() doesn't seem to work here
        for(int i = 0; i < likes.size(); i++)
        {
            if(likes.get(i).equals(eid)) exists = true;
        }
        if(!exists) likes.add(eid);
    }

    /**
     * Remove event from liked list
     * @param eid Event firebase id
     */
    public void removeLike(String eid)
    {
        if(likes == null)
        {
            likes = new ArrayList<String>();
        }
        while(likes.remove(eid))
        {
            continue;
        }
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

}
