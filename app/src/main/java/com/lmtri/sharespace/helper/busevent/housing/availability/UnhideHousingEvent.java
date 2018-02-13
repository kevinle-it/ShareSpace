package com.lmtri.sharespace.helper.busevent.housing.availability;

import com.lmtri.sharespace.api.model.Housing;

/**
 * Created by lmtri on 12/13/2017.
 */

public class UnhideHousingEvent {
    private Housing Housing;

    public UnhideHousingEvent(Housing housing) {
        this.Housing = housing;
    }

    public Housing getHousing() {
        return Housing;
    }
}
