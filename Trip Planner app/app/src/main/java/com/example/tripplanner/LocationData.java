package com.example.tripplanner;

public class LocationData {
    public String address,locality;
    public double latitude,longitude;

    public LocationData(String address, String locality, double latitude, double longitude) {
        this.address = address;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationData() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
