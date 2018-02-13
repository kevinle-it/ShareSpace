package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 8/18/2017.
 */

public class Note {
    private int HousingID;
    private String NoteName;
    private String Content;
    private Date DateTimeCreated;

    public Note(int housingID, String noteName, String content, Date dateTimeCreated) {
        HousingID = housingID;
        NoteName = noteName;
        Content = content;
        DateTimeCreated = dateTimeCreated;
    }

    public String getContent() {
        return Content;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
    }
}
