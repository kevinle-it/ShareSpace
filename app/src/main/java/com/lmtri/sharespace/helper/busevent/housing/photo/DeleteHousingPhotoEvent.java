package com.lmtri.sharespace.helper.busevent.housing.photo;

import com.lmtri.sharespace.api.model.HistoryHousingPhoto;

/**
 * Created by lmtri on 12/11/2017.
 */

public class DeleteHousingPhotoEvent {
    private HistoryHousingPhoto HistoryHousingPhoto;

    public DeleteHousingPhotoEvent(HistoryHousingPhoto historyHousingPhoto) {
        this.HistoryHousingPhoto = historyHousingPhoto;
    }

    public HistoryHousingPhoto getHistoryHousingPhoto() {
        return HistoryHousingPhoto;
    }
}
