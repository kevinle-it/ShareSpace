package com.lmtri.sharespace.api.model;

/**
 * Created by lmtri on 8/15/2017.
 */

public class User {
    private int UserID;
    private String UID;
    private String ProfilePhotoURL;
    private String FirstName;
    private String LastName;
    private String Email;
    private String DOB;
    private String PhoneNumber;
    private String Gender;
    private String AddressHouseNumber;
    private String AddressStreet;
    private String AddressWard;
    private String AddressDistrict;
    private String AddressCity;
    private String SchoolName;
    private int StartSchoolYear;
    private String WorkType;
    private String Hometown;
    private String Description;
    private int NumOfNote;
    private String DeviceTokens;

    public User(String firstName, String lastName,
                String email, String DOB,
                String phoneNumber, String gender,
                int numOfNote, String deviceTokens) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        this.DOB = DOB;
        PhoneNumber = phoneNumber;
        Gender = gender;
        NumOfNote = numOfNote;
        DeviceTokens = deviceTokens;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUID() {
        return UID;
    }

    public String getProfilePhotoURL() {
        return ProfilePhotoURL;
    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        ProfilePhotoURL = profilePhotoURL;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAddressHouseNumber() {
        return AddressHouseNumber;
    }

    public void setAddressHouseNumber(String addressHouseNumber) {
        AddressHouseNumber = addressHouseNumber;
    }

    public String getAddressStreet() {
        return AddressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        AddressStreet = addressStreet;
    }

    public String getAddressWard() {
        return AddressWard;
    }

    public void setAddressWard(String addressWard) {
        AddressWard = addressWard;
    }

    public String getAddressDistrict() {
        return AddressDistrict;
    }

    public void setAddressDistrict(String addressDistrict) {
        AddressDistrict = addressDistrict;
    }

    public String getAddressCity() {
        return AddressCity;
    }

    public void setAddressCity(String addressCity) {
        AddressCity = addressCity;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public int getStartSchoolYear() {
        return StartSchoolYear;
    }

    public void setStartSchoolYear(int startSchoolYear) {
        StartSchoolYear = startSchoolYear;
    }

    public String getWorkType() {
        return WorkType;
    }

    public void setWorkType(String workType) {
        WorkType = workType;
    }

    public String getHometown() {
        return Hometown;
    }

    public void setHometown(String hometown) {
        Hometown = hometown;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getNumOfNote() {
        return NumOfNote;
    }

    public void setNumOfNote(int numOfNote) {
        NumOfNote = numOfNote;
    }

    public String getDeviceTokens() {
        return DeviceTokens;
    }
}
