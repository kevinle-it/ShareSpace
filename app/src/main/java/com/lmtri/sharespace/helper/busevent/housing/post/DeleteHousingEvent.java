package com.lmtri.sharespace.helper.busevent.housing.post;

import com.lmtri.sharespace.api.model.Housing;

/**
 * Created by lmtri on 12/3/2017.
 */

public class DeleteHousingEvent {
    private Housing Housing;

    public DeleteHousingEvent(Housing housing) {
        Housing = housing;
    }

    public Housing getHousing() {
        return Housing;
    }
}
