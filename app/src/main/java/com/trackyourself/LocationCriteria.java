package com.trackyourself;

import java.util.Date;

public class LocationCriteria {

    private String locationName;
    private Date fromDate;
    private Date toDate;


    public LocationCriteria(String locationName, Date fromDate, Date toDate) {
        this.locationName = locationName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
