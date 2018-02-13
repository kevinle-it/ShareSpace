package com.lmtri.sharespace.api.model;

/**
 * Created by lmtri on 12/26/2017.
 */

public class FCMResponse {
    private int success;
    private int failure;

    public int getNumSuccess() {
        return success;
    }

    public int getNumFailure() {
        return failure;
    }
}
