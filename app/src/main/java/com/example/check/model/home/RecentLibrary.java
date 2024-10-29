package com.example.check.model.home;

import com.google.gson.annotations.SerializedName;

public class RecentLibrary {
    private String library;

    @SerializedName("visit_count")
    private int visitCount;

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}

