package com.lmtri.sharespace.helper.busevent.housing.photo;

import com.lmtri.sharespace.api.model.HistoryHousingPhoto;

/**
 * Created by lmtri on 12/11/2017.
 */

public class TakeHousingPhotoEvent {
    private HistoryHousingPhoto HistoryHousingPhoto;

    public TakeHousingPhotoEvent(HistoryHousingPhoto historyHousingPhoto) {
        this.HistoryHousingPhoto = historyHousingPhoto;
    }

    public HistoryHousingPhoto getHistoryHousingPhoto() {
        return HistoryHousingPhoto;
    }
}
