package org.softwire.training.api.models;

import java.time.ZonedDateTime;

/**
 * The LocationStatusReportApiModel is a version of the LocationStatusReport storage model.
 *
 * This version uses a zoned date time for the report time to allow the offset to be specified by API clients.
 */
public class LocationStatusReportApiModel {

    private int reportId;
    private int locationId;
    private String callSign;
    private byte status;
    private ZonedDateTime reportTime;
    private String reportBody;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public ZonedDateTime getReportTime() {
        return reportTime;
    }

    public void setReportTime(ZonedDateTime reportTime) {
        this.reportTime = reportTime;
    }

    public String getReportBody() {
        return reportBody;
    }

    public void setReportBody(String reportBody) {
        this.reportBody = reportBody;
    }
}
