package com.lmtri.sharespace.helper.busevent.sharehousing.photo;

import com.lmtri.sharespace.api.model.HistoryShareHousingPhoto;

/**
 * Created by lmtri on 12/11/2017.
 */

public class DeleteShareHousingPhotoEvent {
    private HistoryShareHousingPhoto HistoryShareHousingPhoto;

    public DeleteShareHousingPhotoEvent(HistoryShareHousingPhoto historyShareHousingPhoto) {
        this.HistoryShareHousingPhoto = historyShareHousingPhoto;
    }

    public HistoryShareHousingPhoto getHistoryShareHousingPhoto() {
        return HistoryShareHousingPhoto;
    }
}
