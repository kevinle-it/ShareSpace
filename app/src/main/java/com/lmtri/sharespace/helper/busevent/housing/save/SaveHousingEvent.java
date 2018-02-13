package com.lmtri.sharespace.helper.busevent.housing.save;

import com.lmtri.sharespace.api.model.SavedHousing;

/**
 * Created by lmtri on 11/30/2017.
 */

public class SaveHousingEvent {
    private SavedHousing SavedHousing;

    public SaveHousingEvent(SavedHousing savedHousing) {
        this.SavedHousing = savedHousing;
    }

    public SavedHousing getSavedHousing() {
        return SavedHousing;
    }
}
