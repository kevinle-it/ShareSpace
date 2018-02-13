package com.lmtri.sharespace.adapter;

/**
 * Created by lmtri on 12/22/2017.
 */

public class PlacePrediction {
    private String PlaceID;
    private CharSequence Description;

    public PlacePrediction(String PlaceID, CharSequence description) {
        this.PlaceID = PlaceID;
        this.Description = description;
    }

    public String getPlaceID() {
        return PlaceID;
    }

    public CharSequence getDescription() {
        return Description;
    }

    @Override
    public String toString() {
        return Description.toString();
    }
}
