package com.wakeup.bandsdk.configVar;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigGeneral {

    public static final String URL_BASE="https://sura-4.herokuapp.com/api/";
    public static final String TYPE = "application/json; charset=utf-8";

    public static final Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ConfigGeneral.URL_BASE)
                .build();

}
