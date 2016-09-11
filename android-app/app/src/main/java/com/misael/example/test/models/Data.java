package com.misael.example.test.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MISAEL on 11/09/2016.
 *
 */
public class Data {

    @SerializedName("area")
    private String area;
    @SerializedName("nivelPeligro")
    private String nivelPeligro;

    public String getArea() {
        return area;
    }

    public String getNivelPeligro() {
        return nivelPeligro;
    }
}
