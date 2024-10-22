package com.example.check.model.login;

public class LoginRequest {
    Long userid;
    String nickname;

    public LoginRequest(Long userId, String nickname) {
        this.userid = userId;
        this.nickname = nickname;
    }


    @Override
    public String toString() {
        return "LoginRequest{" +
                "userid=" + userid +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
