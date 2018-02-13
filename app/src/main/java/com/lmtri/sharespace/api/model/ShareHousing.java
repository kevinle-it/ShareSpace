package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 8/22/2017.
 */

public class ShareHousing {
    private int ID;
    private Housing Housing;
    private User Creator;
    private boolean IsAvailable;
    private int PricePerMonthOfOne;
    private String Description;
    private int NumOfView;
    private int NumOfSaved;
    private int RequiredNumOfPeople;
    private String RequiredGender;
    private String RequiredWorkType;
    private boolean AllowSmoking;
    private boolean AllowAlcohol;
    private boolean HasPrivateKey;
    private Date DateTimeCreated;

    public ShareHousing(int ID, Housing housing, User creator, boolean isAvailable,
                        int pricePerMonthOfOne, String description,
                        int numOfView, int numOfSaved,
                        int requiredNumOfPeople, String requiredGender,
                        String requiredWorkType, boolean allowSmoking,
                        boolean allowAlcohol, boolean hasPrivateKey,
                        Date dateTimeCreated) {
        this.ID = ID;
        Housing = housing;
        Creator = creator;
        IsAvailable = isAvailable;
        PricePerMonthOfOne = pricePerMonthOfOne;
        Description = description;
        NumOfView = numOfView;
        NumOfSaved = numOfSaved;
        RequiredNumOfPeople = requiredNumOfPeople;
        RequiredGender = requiredGender;
        RequiredWorkType = requiredWorkType;
        AllowSmoking = allowSmoking;
        AllowAlcohol = allowAlcohol;
        HasPrivateKey = hasPrivateKey;
        DateTimeCreated = dateTimeCreated;
    }

    public int getID() {
        return ID;
    }

    public Housing getHousing() {
        return Housing;
    }

    public User getCreator() {
        return Creator;
    }

    public boolean isAvailable() {
        return IsAvailable;
    }

    public void setAvailability(boolean available) {
        IsAvailable = available;
    }

    public int getPricePerMonthOfOne() {
        return PricePerMonthOfOne;
    }

    public String getDescription() {
        return Description;
    }

    public int getNumOfView() {
        return NumOfView;
    }

    public int getNumOfSaved() {
        return NumOfSaved;
    }

    public void setNumOfSaved(int numOfSaved) {
        NumOfSaved = numOfSaved;
    }

    public int getRequiredNumOfPeople() {
        return RequiredNumOfPeople;
    }

    public String getRequiredGender() {
        return RequiredGender;
    }

    public String getRequiredWorkType() {
        return RequiredWorkType;
    }

    public boolean isAllowSmoking() {
        return AllowSmoking;
    }

    public boolean isAllowAlcohol() {
        return AllowAlcohol;
    }

    public boolean hasPrivateKey() {
        return HasPrivateKey;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
    }
}
