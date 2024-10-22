package com.example.check.model.stampboard;


// StampRegisterRequest.java
public class StampRegisterRequest {
    private String libraryname;
    private String type;

    public StampRegisterRequest(String libraryname, String type) {
        this.libraryname = libraryname;
        this.type = type;
    }
}
