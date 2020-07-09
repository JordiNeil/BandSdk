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

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaToma() {
        return fechaToma;
    }

    public void setFechaToma(String fechaToma) {
        this.fechaToma = fechaToma;
    }

    public int getOximetria() {
        return oximetria;
    }

    public void setOximetria(int oximetria) {
        this.oximetria = oximetria;
    }

    public int getPresionArterialDiastolica() {
        return presionArterialDiastolica;
    }

    public void setPresionArterialDiastolica(int presionArterialDiastolica) {
        this.presionArterialDiastolica = presionArterialDiastolica;
    }

    public int getPresionArterialSistolica() {
        return presionArterialSistolica;
    }

    public void setPresionArterialSistolica(int presionArterialSistolica) {
        this.presionArterialSistolica = presionArterialSistolica;
    }

    public int getRitmoCardiaco() {
        return ritmoCardiaco;
    }

    public void setRitmoCardiaco(int ritmoCardiaco) {
        this.ritmoCardiaco = ritmoCardiaco;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public DataUser getUserData() {
        return dataUser;
    }

    public void setUserData(DataUser dataUser) {
        this.dataUser = dataUser;
    }


}
