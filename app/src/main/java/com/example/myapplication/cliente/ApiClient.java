package com.example.myapplication.cliente;

import com.example.myapplication.interfaz.ApiServices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
   private static final String BASE_URL = "https://ricohtesting.ricohservicesv.com/APP-INTERCONTINENTAL/";
 // private static final String BASE_URL = "http://192.168.0.2/API-JWT/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiServices getApiService() {
        return getClient().create(ApiServices.class);
    }
}