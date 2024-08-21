package com.example.check.model.home;

import java.util.List;

public class RecentLibrariesWrapper {
    private int userid;
    private List<RecentLibrary> recent_libraries;


    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }

    public List<RecentLibrary> getRecent_libraries() {
        return recent_libraries;
    }
    // 게터, 세터
}
