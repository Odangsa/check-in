package com.example.check.model.stampboard;

import java.util.List;

public class StampBoard {
    private int userid;
    private List<Transportation> transportation;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public List<Transportation> getTransportation() {
        return transportation;
    }

    public void setTransportation(List<Transportation> transportation) {
        this.transportation = transportation;
    }
}

