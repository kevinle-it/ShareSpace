package com.lmtri.sharespace.api.service.housing.report;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IReportHousingCallback {
    void onReportComplete(Boolean isReported);
    void onReportFailure(Throwable t);
}
