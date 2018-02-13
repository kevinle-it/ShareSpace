package com.lmtri.sharespace.api.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lmtri on 6/14/2017.
 */

public class Housing {
    private int ID;
    private ArrayList<String> PhotoURLs;    // 1st element is Housing's Profile Image.
    private String Title;
    private User Owner;
    private int Price;
    private boolean IsAvailable;
    private String HouseType;
    private Date DateTimeCreated;
    private int NumOfView;
    private int NumOfSaved;
    private int NumOfPeople;
    private int NumOfRoom;
    private int NumOfBed;
    private int NumOfBath;
    private boolean AllowPet;
    private boolean HasWifi;
    private boolean HasAC;
    private boolean HasParking;
    private String TimeRestriction;
    private float Area;
    private BigDecimal Latitude;
    private BigDecimal Longitude;
    private String AddressHouseNumber;
    private String AddressStreet;
    private String AddressWard;
    private String AddressDistrict;
    private String AddressCity;
    private String Description;
    private Note CurrentUserNote;
    private ArrayList<String> CurrentUserPhotoURLs;
    private Date CurrentUserAppointment;
    private int NumOfComment;

    public Housing(int ID, ArrayList<String> photoURLs,
                   String title, User owner, int price,
                   boolean isAvailable, String houseType,
                   Date dateTimeCreated, int numOfView,
                   int numOfSaved, int numOfPeople,
                   int numOfRoom, int numOfBed,
                   int numOfBath, boolean allowPet,
                   boolean hasWifi, boolean hasAC,
                   boolean hasParking, String timeRestriction,
                   float area, BigDecimal latitude,
                   BigDecimal longitude, String addressHouseNumber,
                   String addressStreet, String addressWard,
                   String addressDistrict, String addressCity,
                   String description, int numOfComment) {
        this.ID = ID;
        PhotoURLs = photoURLs;
        Title = title;
        Owner = owner;
        Price = price;
        IsAvailable = isAvailable;
        HouseType = houseType;
        DateTimeCreated = dateTimeCreated;
        NumOfView = numOfView;
        NumOfSaved = numOfSaved;
        NumOfPeople = numOfPeople;
        NumOfRoom = numOfRoom;
        NumOfBed = numOfBed;
        NumOfBath = numOfBath;
        AllowPet = allowPet;
        HasWifi = hasWifi;
        HasAC = hasAC;
        HasParking = hasParking;
        TimeRestriction = timeRestriction;
        Area = area;
        Latitude = latitude;
        Longitude = longitude;
        AddressHouseNumber = addressHouseNumber;
        AddressStreet = addressStreet;
        AddressWard = addressWard;
        AddressDistrict = addressDistrict;
        AddressCity = addressCity;
        Description = description;
        NumOfComment = numOfComment;
    }

    public int getID() {
        return ID;
    }

    public ArrayList<String> getPhotoURLs() {
        return PhotoURLs;
    }

    public void setPhotoURLs(ArrayList<String> photoURLs) {
        PhotoURLs = photoURLs;
    }

    public String getTitle() {
        return Title;
    }

    public User getOwner() {
        return Owner;
    }

    public int getPrice() {
        return Price;
    }

    public boolean isAvailable() {
        return IsAvailable;
    }

    public void setAvailability(boolean isAvailable) {
        IsAvailable = isAvailable;
    }

    public String getHouseType() {
        return HouseType;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
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

    public int getNumOfPeople() {
        return NumOfPeople;
    }

    public int getNumOfRoom() {
        return NumOfRoom;
    }

    public int getNumOfBed() {
        return NumOfBed;
    }

    public int getNumOfBath() {
        return NumOfBath;
    }

    public boolean isAllowPet() {
        return AllowPet;
    }

    public boolean hasWifi() {
        return HasWifi;
    }

    public boolean hasAC() {
        return HasAC;
    }

    public boolean hasParking() {
        return HasParking;
    }

    public String getTimeRestriction() {
        return TimeRestriction;
    }

    public float getArea() {
        return Area;
    }

    public BigDecimal getLatitude() {
        return Latitude;
    }

    public BigDecimal getLongitude() {
        return Longitude;
    }

    public String getAddressHouseNumber() {
        return AddressHouseNumber;
    }

    public String getAddressStreet() {
        return AddressStreet;
    }

    public String getAddressWard() {
        return AddressWard;
    }

    public String getAddressDistrict() {
        return AddressDistrict;
    }

    public String getAddressCity() {
        return AddressCity;
    }

    public String getDescription() {
        return Description;
    }

    public Note getCurrentUserNote() {
        return CurrentUserNote;
    }

    public void setCurrentUserNote(Note currentUserNote) {
        CurrentUserNote = currentUserNote;
    }

    public ArrayList<String> getCurrentUserPhotoURLs() {
        return CurrentUserPhotoURLs;
    }

    public void setCurrentUserPhotoURLs(ArrayList<String> currentUserPhotoURLs) {
        CurrentUserPhotoURLs = currentUserPhotoURLs;
    }

    public Date getCurrentUserAppointment() {
        return CurrentUserAppointment;
    }

    public void setCurrentUserAppointment(Date currentUserAppointment) {
        CurrentUserAppointment = currentUserAppointment;
    }

    public int getNumOfComment() {
        return NumOfComment;
    }
}
