package com.lmtri.sharespace.adapter;

import com.google.android.gms.location.places.Place;

/**
 * Created by lmtri on 12/22/2017.
 */

public class MyPlace {
    private Place Place;
    private String Description;

    public MyPlace(Place place, String description) {
        Place = place;
        Description = description;
    }

    public Place getPlace() {
        return Place;
    }

    public String getDescription() {
        return Description;
    }
}
