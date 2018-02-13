package com.lmtri.sharespace.helper.busevent.sharehousing.save;

import com.lmtri.sharespace.api.model.SavedShareHousing;

/**
 * Created by lmtri on 11/30/2017.
 */

public class UnsaveShareHousingEvent {
    private SavedShareHousing SavedShareHousing;

    public UnsaveShareHousingEvent(SavedShareHousing savedShareHousing) {
        this.SavedShareHousing = savedShareHousing;
    }

    public SavedShareHousing getSavedShareHousing() {
        return SavedShareHousing;
    }
}
