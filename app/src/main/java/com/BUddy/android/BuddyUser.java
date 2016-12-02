package com.BUddy.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rebeccagraber on 11/18/16.
 */

public class BuddyUser implements Parcelable {
    private String name;
    private String phoneNum;
    private String email;
    private Date dob;
    private ArrayList<String> eids;
    private ArrayList<String> likes;
    private String fbId;
    private String firebaseId;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BuddyUser createFromParcel(Parcel in) {
            return new BuddyUser(in);
        }

        @Override
        public BuddyUser[] newArray(int size) {
            return new BuddyUser[0];
        }

    };



    public BuddyUser() {
        eids = new ArrayList<String>();
        likes = new ArrayList<String>();
    }


    public BuddyUser(String name, String phoneNum, String email, Date dob,
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public ArrayList<String> getEids() {
        return eids;
    }

    public void setEids(ArrayList<String> eids) {
        this.eids = eids;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private BuddyUser(Parcel in) {
        eids = new ArrayList<>();
        likes = new ArrayList<>();
        name = in.readString();
        phoneNum = in.readString();
        email = in.readString();
        String d = in.readString();
        try {
            dob = StaticConstants.SDF.parse(d);
        } catch (ParseException pe) {
            Log.e("BUDDY", "Error: unparseable date of birth: " + d);
        }
        in.readStringList(eids);
        in.readStringList(likes);
        fbId = in.readString();
        firebaseId = in.readString();


    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNum);
        dest.writeString(email);
        if (dob != null) {
            dest.writeString(StaticConstants.SDF.format(dob));
        } else {
            dest.writeString("");
        }
        dest.writeStringList(eids);
        dest.writeStringList(likes);
        dest.writeString(fbId);
        dest.writeString(firebaseId);
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

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

    public void removeEvent(String eid)
    {
        if(eids == null)
        {
            eids = new ArrayList<String>();
        }
        int i = -1;
        for(int j = 0; j < eids.size(); j++)
        {
            if(eids.get(j).equals(eid)) i = j;
        }
        if(i > -1) eids.remove(i);
    }


    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

}
