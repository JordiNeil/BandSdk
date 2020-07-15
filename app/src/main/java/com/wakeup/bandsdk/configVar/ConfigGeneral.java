package com.wakeup.bandsdk.configVar;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigGeneral {

    public static final String URL_BASE="https://sura-4.herokuapp.com/api/";
    public static final String TYPE = "application/json; charset=utf-8";

    public static  final String TOKENSHARED="storedJwtToken";
    public static final String STOREDUSERID = "userId";
    public static final String STOREDUSERLOGIN = "login";
    public static final String STOREDUSERFIRSTNAME = "firstName";
    public static final String STOREDUSERLASTNAME = "lastName";
    public static final String STOREDUSEREMAIL = "email";
    public static final String STOREDUSERACTIVATED = "activated";
    public static final String STOREDUSERLANGKEY = "langKey";
    public static final String STOREDUSERIMAGEURL = "imageUrl";

    public static  final String PUTEXTRASFISIOMETRIA= "fetchedPhysiometryData";






    public static  final String ERRORINPUTS="Credenciales inválidas. Intentalo de nuevo.";
    public static  final String ERRORGENERAL="Ha ocurrido un error. Por favor intentalo de nuevo.";
    public static  final String INPUTVACIO= "Este campo no debe estar vacío";
    public static  final String SENDCONNECTION= "No existe un dispositivo conectado";




    public static  final String preference_file_key="be4techSharedPrefs";


    public static final Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ConfigGeneral.URL_BASE)
                .build();

}
