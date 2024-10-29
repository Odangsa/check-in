package com.example.check.model.bookDetail;

public class LibraryModel {
    private String libname;
    private String libaddr;
    private double latitude;
    private double longitude;
    private String distance;

    // Getters and Setters
    public String getLibname() {
        return libname;
    }

    public void setLibname(String libname) {
        this.libname = libname;
    }

    public String getLibaddr() {
        return libaddr;
    }

    public void setLibaddr(String libaddr) {
        this.libaddr = libaddr;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}