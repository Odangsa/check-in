package com.example.check.model.stampboard;

public class StampRegisterRequest {
    private String userid;
    private String code;
    private String date;

    public StampRegisterRequest(String userid, String code, String date) {
        this.userid = userid;
        this.code = code;
        this.date = date;
    }

    // Getter and Setter
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}