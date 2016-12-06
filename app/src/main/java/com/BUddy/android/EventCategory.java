/**
 * Enum: EventCategory
 * @author NOGE
 * An enum for the types of events: Food, Sports, Study Break, Movie, Exploring, Other
 */

package com.BUddy.android;

import android.content.Context;

/**
 * Created by rebeccagraber on 11/1/16.
 */

public enum EventCategory {
        FOOD(1,R.string.food),
        SPORTS(2,R.string.sports),
        STUDY_BREAK(3,R.string.study),
        MOVIE(4, R.string.movie),
        EXPLORING(5,R.string.explore),
        OTHER(6,R.string.other);

    private final int id;
    private final int resId;

    /**
     * Constructor
     * @param id
     * @param resId
     */
    EventCategory(int id, int resId)
    {
        this.id = id;
        this.resId = resId;
    }

    /**
     * Get category string
     * @param c
     * @return
     */
    public String getName(Context c)
    {
        return c.getString(resId);
    }

    /**
     * Get category id
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Get category by category id
     * @param id
     * @return
     */
    public static EventCategory getById(int id)
    {
        switch (id)
        {
            case 1: return FOOD;
            case 2: return SPORTS;
            case 3: return STUDY_BREAK;
            case 4: return MOVIE;
            case 5: return EXPLORING;
            default: return OTHER;
        }
    }

    /**
     * Get event category by name (must match string in R.string)
     * @param c Context for getting string values
     * @param name
     * @return
     */
    public static EventCategory getByName(Context c, String name)
    {
        // can't use switches with non constants
        if(c.getString(R.string.food).equals(name)) return FOOD;
        if(c.getString(R.string.sports).equals(name)) return SPORTS;
        if(c.getString(R.string.study).equals(name)) return STUDY_BREAK;
        if(c.getString(R.string.movie).equals(name)) return MOVIE;
        if(c.getString(R.string.explore).equals(name)) return EXPLORING;
        else return OTHER;
    }





}
