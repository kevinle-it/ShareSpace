package com.lmtri.sharespace.helper.busevent.sharehousing.save;

import com.lmtri.sharespace.api.model.SavedShareHousing;

/**
 * Created by lmtri on 11/30/2017.
 */

public class SaveShareHousingEvent {
    private SavedShareHousing SavedShareHousing;

    public SaveShareHousingEvent(SavedShareHousing savedShareHousing) {
        this.SavedShareHousing = savedShareHousing;
    }

    public SavedShareHousing getSavedShareHousing() {
        return SavedShareHousing;
    }
}
