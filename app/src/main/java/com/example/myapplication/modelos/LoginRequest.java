package com.example.myapplication.modelos;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("Username")
    private String username;

    @SerializedName("password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
