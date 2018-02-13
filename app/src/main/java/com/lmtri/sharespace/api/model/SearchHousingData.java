package com.lmtri.sharespace.api.model;

import java.math.BigDecimal;

/**
 * Created by lmtri on 12/19/2017.
 */

public class SearchHousingData {
    private BigDecimal Latitude;    // [-90, 90]
    private BigDecimal Longitude;   // [-180, 180)

    private int Radius; // In Meters. [0, 8] km = [0, 8000] m

    private String Keywords;
    private boolean[] HouseTypes;    // [Any, Apartment, Private House, Street Front House, Private Room]

    private int MinPrice;   // -2 is Any. -1 is Deal. [Any, Deal, 500k, 1000k, 3000k, 5000k, 10000k, 40000k]
    private int MaxPrice;   // -2 is Any. -1 is Deal. [Any, Deal, 500k, 1000k, 3000k, 5000k, 10000k, 40000k]

    private float MinArea;  // -1 is Any. [Any, 30m, 50m, 80m, 100m, 150m, 200m, 250m, 300m, 500m]
    private float MaxArea;  // -1 is Any. [Any, 30m, 50m, 80m, 100m, 150m, 200m, 250m, 300m, 500m]
    private int NumPeople;  // -1 is Any. [Any, 1+, 2+, 3+, 4+, 5+]
    private int NumRoom;    // -1 is Any. [Any, 1+, 2+, 3+, 4+, 5+]
    private int NumBed;     // -1 is Any. [Any, 1+, 2+, 3+, 4+, 5+]
    private int NumBath;    // -1 is Any. [Any, 1+, 2+, 3+, 4+, 5+]

    private boolean[] Amenities;    // [Any, Allow Pet, Has Wifi, Has AC, Has Parking]

    private String MinTimeRestriction;  // "" is Any. [00:00, 23:59]
    private String MaxTimeRestriction;  // "" is Any. [00:00, 23:59]

    public SearchHousingData(BigDecimal latitude, BigDecimal longitude,
                             int radius, String keywords,
                             boolean[] houseTypes,
                             int minPrice, int maxPrice,
                             float minArea, float maxArea,
                             int numPeople, int numRoom, int numBed, int numBath,
                             boolean[] amenities,
                             String minTimeRestriction, String maxTimeRestriction) {
        Latitude = latitude;
        Longitude = longitude;
        Radius = radius;
        Keywords = keywords;
        HouseTypes = houseTypes;
        MinPrice = minPrice;
        MaxPrice = maxPrice;
        MinArea = minArea;
        MaxArea = maxArea;
        NumPeople = numPeople;
        NumRoom = numRoom;
        NumBed = numBed;
        NumBath = numBath;
        Amenities = amenities;
        MinTimeRestriction = minTimeRestriction;
        MaxTimeRestriction = maxTimeRestriction;
    }
}
