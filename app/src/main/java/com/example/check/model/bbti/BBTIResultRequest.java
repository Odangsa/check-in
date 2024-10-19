package com.example.check.model.bbti;

public class BBTIResultRequest {
    private int userid;
    private int bbti;

    public BBTIResultRequest(int userid, int bbti) {
        this.userid = userid;
        this.bbti = bbti;
    }

    // Getters and setters
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
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