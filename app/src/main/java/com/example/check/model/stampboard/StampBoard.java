package com.example.check.model.stampboard;

import java.util.List;

public class StampBoard {
    private Long userid;
    private List<Transportation> transportation;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public List<Transportation> getTransportation() {
        return transportation;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StampBoard{")
                .append("userid='").append(userid).append('\'')
                .append(", transportation=[");
        if (transportation != null) {
            for (Transportation t : transportation) {
                sb.append(t.toString()).append(", ");
            }
            if (!transportation.isEmpty()) {
                sb.setLength(sb.length() - 2); // 마지막 ", " 제거
            }
        } else {
            sb.append("null");
        }
        sb.append("]}");
        return sb.toString();
    }

    public void setTransportation(List<Transportation> transportation) {
        this.transportation = transportation;
    }
}

