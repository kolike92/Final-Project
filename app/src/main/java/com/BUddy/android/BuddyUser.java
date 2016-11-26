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

public class BuddyUser implements Parcelable{
    private String uid;
    private String name;
    private String phoneNum;
    private String email;
    private Date dob;
    private ArrayList<String> eids;
    private ArrayList<String> likes;
    private String fbId;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

    public BuddyUser()
    {
        eids = new ArrayList<String>();
        likes = new ArrayList<String>();
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    private BuddyUser (Parcel in)
    {
        eids = new ArrayList<>();
        likes = new ArrayList<>();
        uid = in.readString();
        name = in.readString();
        phoneNum = in.readString();
        email = in.readString();
        String d = in.readString();
        try {
            dob = sdf.parse(d);
        }
        catch (ParseException pe)
        {
            Log.e("BUDDY", "Error: unparseable date of birth: " + d);
        }
        in.readStringList(eids);
        in.readStringList(likes);
        fbId = in.readString();


    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(phoneNum);
        dest.writeString(email);
        dest.writeString(sdf.format(dob));
        dest.writeStringList(eids);
        dest.writeStringList(likes);
        dest.writeString(fbId);
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
}
