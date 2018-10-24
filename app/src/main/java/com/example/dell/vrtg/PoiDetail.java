package com.example.dell.vrtg;

/**
 * Created by DELL on 2/6/2018.
 */

public class PoiDetail {

    private double latitude;
    private double longitude;
    private double altitude;
    private String placeName;
    private String description;

    private PoiDetail(){

    }

    public PoiDetail(double latitude, double longitude, double altitude, String placeName, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.placeName = placeName;
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {return altitude;}

    public String getPlaceName() {
        return placeName;
    }

    public String getDescription() {
        return description;
    }

}
