package com.victorbg.racofib.data.model.login;

public class LoginData {

    public String token;
    public long expirationTime;

    public LoginData(String token, long expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
    }

}
