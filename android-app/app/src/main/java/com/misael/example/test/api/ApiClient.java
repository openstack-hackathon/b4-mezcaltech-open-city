package com.misael.example.test.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MISAEL on 11/09/2016.
 */
public class ApiClient {

    private Retrofit retrofit;
    private final static String  BASE_URL = "http://172.16.1.90:3000/";

    public ApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public BeaconService getBeaconService(){
        return this.retrofit.create(BeaconService.class);
    }
}
