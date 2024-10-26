package com.example.check.model.home;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecentLibrariesWrapper {
    private Long userid;

    @SerializedName("recent_libraries")
    private List<RecentLibrary> recentLibraries;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public List<RecentLibrary> getRecentLibraries() {
        return recentLibraries;
    }

    public void setRecentLibraries(List<RecentLibrary> recentLibraries) {
        this.recentLibraries = recentLibraries;
    }
}
