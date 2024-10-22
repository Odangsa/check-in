package com.example.check.model.bbti;

import android.util.Log;

public class BBTIResultRequest {
    private Long userid;
    private int bbti;

    public BBTIResultRequest(Long userid, int bbti) {
        this.userid = userid;
        this.bbti = bbti;
    }

    // Getters and setters
    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public int getBbti() {
        return bbti;
    }

    public void setBbti(int bbti) {
        this.bbti = bbti;
    }

    @Override
    public String toString() {
        return "BBTIResultRequest{" +
                "userid=" + userid +
                ", bbti=" + bbti +
                '}';
    }
}