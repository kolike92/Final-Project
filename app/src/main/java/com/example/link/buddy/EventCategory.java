package com.example.link.buddy;

/**
 * Created by rebeccagraber on 11/1/16.
 */

public enum EventCategory {
        FOOD(1,"Food"),
        SPORTS(2,"Sports"),
        STUDY_BREAK(3,"Study Break"),
        MOVIE(4, "Movie"),
        EXPLORING(5,"Exploring"),
        OTHER(6,"Other");

    private final int id;
    private final String name;

    EventCategory(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getId() {
        return id;
    }

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

    public static EventCategory getByName(String name)
    {
        switch (name)
        {
            case "Food": return FOOD;
            case "Sports": return SPORTS;
            case "Study Break": return STUDY_BREAK;
            case "Movie": return MOVIE;
            case "Exploring": return EXPLORING;
            default: return OTHER;
        }
    }





}
