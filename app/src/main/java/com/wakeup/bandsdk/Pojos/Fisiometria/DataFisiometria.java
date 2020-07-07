package com.wakeup.bandsdk.Pojos.Fisiometria;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.wakeup.bandsdk.Pojos.DataUser;

public class DataFisiometria {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("fechaRegistro")
    @Expose
    private String fechaRegistro;

    @SerializedName("fechaToma")
    @Expose
    private String fechaToma;

    @SerializedName("oximetria")
    @Expose
    private int oximetria;

    @SerializedName("presionArterialDiastolica")
    @Expose
    private int presionArterialDiastolica;

    @SerializedName("presionArterialSistolica")
    @Expose
    private int presionArterialSistolica;

    @SerializedName("ritmoCardiaco")
    @Expose
    private int ritmoCardiaco;

    @SerializedName("temperatura")
    @Expose
    private float temperatura;


    @SerializedName("user")
    @Expose
    private DataUser dataUser;


    public DataFisiometria(int id, String fechaRegistro, String fechaToma, int oximetria, int presionArterialDiastolica, int presionArterialSistolica, int ritmoCardiaco, float temperatura, DataUser dataUser) {
        super();
        this.id = id;
        this.fechaRegistro = fechaRegistro;
        this.fechaToma = fechaToma;
        this.oximetria = oximetria;
        this.presionArterialDiastolica = presionArterialDiastolica;
        this.presionArterialSistolica = presionArterialSistolica;
        this.ritmoCardiaco = ritmoCardiaco;
        this.temperatura = temperatura;
        this.dataUser = dataUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getfechaRegistro() {
        return fechaRegistro;
    }

    public void setfechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getfechaToma() {
        return fechaToma;
    }

    public void setfechaToma(String fechaToma) {
        this.fechaToma = fechaToma;
    }

    public int getoximetria() {
        return oximetria;
    }

    public void setoximetria(int oximetria) {
        this.oximetria = oximetria;
    }

    public int getpresionArterialDiastolica() {
        return presionArterialDiastolica;
    }

    public void setpresionArterialDiastolica(int presionArterialDiastolica) {
        this.presionArterialDiastolica = presionArterialDiastolica;
    }

    public int getpresionArterialSistolica() {
        return presionArterialSistolica;
    }

    public void setTotalCost(int presionArterialSistolica) {
        this.presionArterialSistolica = presionArterialSistolica;
    }

    public int getritmoCardiaco() {
        return ritmoCardiaco;
    }

    public void setritmoCardiaco(int ritmoCardiaco) {
        this.ritmoCardiaco = ritmoCardiaco;
    }

    public float gettemperatura() {
        return temperatura;
    }

    public void settemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public DataUser getdataUser() {
        return dataUser;
    }

    public void setdataUser(DataUser dataUser) {
        this.dataUser = dataUser;
    }


}
