package com.victorbg.racofib.data.model.login;

public class LoginData {

    public String token;
    public long expirationTime;
    public String username;

    public LoginData(String token, long expirationTime, String username) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.username = username;
    }

}
