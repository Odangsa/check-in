package com.example.check.model.bookDetail;

public class Library {
    private String libname;
    private String libaddr;
    private double latitude;
    private double longitude;
    private Integer distance;

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

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}