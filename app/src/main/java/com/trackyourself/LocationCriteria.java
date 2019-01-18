package com.trackyourself;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class LocationCriteria implements Parcelable {

    private String locationName;
    private String fromDate;
    private String toDate;


    public LocationCriteria() {

    }

    protected LocationCriteria(Parcel in) {
        locationName = in.readString();
    }

    public static final Creator<LocationCriteria> CREATOR = new Creator<LocationCriteria>() {
        @Override
        public LocationCriteria createFromParcel(Parcel in) {
            return new LocationCriteria(in);
        }

        @Override
        public LocationCriteria[] newArray(int size) {
            return new LocationCriteria[size];
        }
    };

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locationName);
    }
}
