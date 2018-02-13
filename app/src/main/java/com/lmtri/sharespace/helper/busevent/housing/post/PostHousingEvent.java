package com.lmtri.sharespace.helper.busevent.housing.post;

import com.lmtri.sharespace.api.model.Housing;

/**
 * Created by lmtri on 11/30/2017.
 */

public class PostHousingEvent {
    private Housing Housing;

    public PostHousingEvent(Housing housing) {
        this.Housing = housing;
    }

    public Housing getHousing() {
        return Housing;
    }
}
