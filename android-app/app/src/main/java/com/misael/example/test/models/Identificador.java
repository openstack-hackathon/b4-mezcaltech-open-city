package com.misael.example.test.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MISAEL on 11/09/2016.
 */
public class Identificador {
    @SerializedName("id")
    private String id;

    public void setId(String id) {
        this.id = id;
    }
}
