package com.example.tripplanner;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class TripData {
    public LocationData fromLocation,toLocation;
    public List<LocationData> route;
    public int day,month,year;

    public TripData(LocationData fromLocation, LocationData toLocation, List<LocationData> route, int day, int month, int year, String tripName) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.route = route;
        this.day = day;
        this.month = month;
        this.year = year;
        this.tripName = tripName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public TripData(LocationData fromLocation, LocationData toLocation, List<LocationData> route, String tripName) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.route = route;
        this.tripName = tripName;
    }

    public TripData() {
    }

    public String tripName;

    public LocationData getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(LocationData fromLocation) {
        this.fromLocation = fromLocation;
    }

    public LocationData getToLocation() {
        return toLocation;
    }

    public void setToLocation(LocationData toLocation) {
        this.toLocation = toLocation;
    }

    public List<LocationData> getRoute() {
        return route;
    }

    public void setRoute(List<LocationData> route) {
        this.route = route;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }
}
