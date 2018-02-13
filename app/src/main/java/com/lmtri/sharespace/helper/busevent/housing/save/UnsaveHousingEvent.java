package com.lmtri.sharespace.helper.busevent.housing.save;

import com.lmtri.sharespace.api.model.SavedHousing;

/**
 * Created by lmtri on 11/30/2017.
 */

public class UnsaveHousingEvent {
    private SavedHousing SavedHousing;

    public UnsaveHousingEvent(SavedHousing savedHousing) {
        this.SavedHousing = savedHousing;
    }

    public SavedHousing getSavedHousing() {
        return SavedHousing;
    }
}
