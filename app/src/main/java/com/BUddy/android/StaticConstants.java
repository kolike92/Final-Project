/**
 * Class: StaticConstants
 * @author NOGE
 * Useful constants used across activities
 */

package com.BUddy.android;

import java.text.SimpleDateFormat;

public class StaticConstants {

    //keys to bundles
    public static final String EID_KEY = "EID";
    public static final String UID_KEY = "UID";
    public static final String USER_KEY = "USER";
    public static final String EVENT_KEY = "EVENT";
    public static final String EMAIL_KEY = "EMAIL";
    public static final String FILTERED_KEY = "FILTERED";
    public static final String FILTERED_EVENTS_KEY = "FILTERED_EVENTS";
    public static final String JOINED_KEY = "JOINED";

    //tag for logging
    public static final String TAG = "BUDDY";

    //default date/time format
    public static final SimpleDateFormat SDF = new SimpleDateFormat("MM-dd-yyyy");
    public static final SimpleDateFormat STF = new SimpleDateFormat("HH:mm");



}
