package com.misael.example.test.api;

import com.misael.example.test.models.Beacon;
import com.misael.example.test.models.Identificador;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by MISAEL on 11/09/2016.
 */
public interface BeaconService {

    @POST("beaconInfo")
    Call<Beacon> getBeaconInfo(@Body Identificador identificador);
}
