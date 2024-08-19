package com.example.check.model.home.recentLib;


public class RecentLibrary {
    private String library;
    private int visitCount;

    public RecentLibrary(String library, int visitCount) {
        this.library = library;
        this.visitCount = visitCount;
    }

    public String getLibrary() {
        return library;
    }

    public int getVisitCount() {
        return visitCount;
    }
}