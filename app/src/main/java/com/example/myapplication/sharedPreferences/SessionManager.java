package com.example.myapplication.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SUCURSAL = "sucursal";
    private static final String KEY_NAME = "Name";
    private static final String KEY_EMAIL = "Email";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String token, String username, String password,String sucursal, String name, String Email) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_SUCURSAL, sucursal);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_EMAIL,Email);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);

    }
    public String getSucursal() {
        return sharedPreferences.getString(KEY_SUCURSAL, null);
    }

    public String getName(){
        return  sharedPreferences.getString(KEY_NAME,null);
    }

    public  String getEmail(){
        return  sharedPreferences.getString(KEY_EMAIL,null);
    }

    public void clearSession() {
        String username = getUsername();
        String password = getPassword();
        String sucursal = getSucursal();
        String Name=getName();
        String Email=getEmail();
        editor.clear();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_SUCURSAL, sucursal);
        editor.putString(KEY_NAME,Name);
        editor.putString(KEY_EMAIL,Email);
        editor.apply();
    }


}
