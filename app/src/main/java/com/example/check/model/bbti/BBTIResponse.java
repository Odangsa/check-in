package com.example.check.model.bbti;

import com.google.gson.annotations.SerializedName;

public class BBTIResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("bbti")
    private String bbti;

    public boolean isSuccess() {
        return success;
    }

    public String getBbti() {
        return bbti;
    }
}