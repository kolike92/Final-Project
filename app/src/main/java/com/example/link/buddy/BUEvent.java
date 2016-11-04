package com.example.link.buddy;

import java.util.Date;

/**
 * Created by rebeccagraber on 11/1/16.
 */

public class BUEvent {
    private Date eventDate;
    private String eventTitle;
    private String eventDetails;
    private int category;
    private String location;

    public BUEvent()
    {

    }

    public BUEvent(Date d, String title, String details, int category, String location)
    {
        eventDate = d;
        eventTitle = title;
        eventDetails = details;
        this.category = category;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
