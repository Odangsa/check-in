package com.example.check.model.bookDetail;


public class Library {
    String libname;
    String libaddr;
    double latitude;
    double longitude;
    Integer distance;  // Integer를 사용하여 null 값 처리

    public Library(String libname, String libaddr, double latitude, double longitude, Integer distance) {
        this.libname = libname;
        this.libaddr = libaddr;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public String getLibname() {
        return libname;
    }

    public String getLibaddr() {
        return libaddr;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Integer getDistance() {
        return distance;
    }

    // getters and setters
}