package com.wakeup.bandsdk.Pojos.Conditions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConditionData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("oximetriaReferencia")
    @Expose
    private Integer oximetriaReferencia;
    @SerializedName("temperaturaReferencia")
    @Expose
    private Double temperaturaReferencia;
    @SerializedName("ritmoCardiacoReferencia")
    @Expose
    private Integer ritmoCardiacoReferencia;
    @SerializedName("presionSistolicaReferencia")
    @Expose
    private Integer presionSistolicaReferencia;
    @SerializedName("presionDistolicaReferencia")
    @Expose
    private Integer presionDistolicaReferencia;
    @SerializedName("nombre")
    @Expose
    private String nombre;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOximetriaReferencia() {
        return oximetriaReferencia;
    }

    public void setOximetriaReferencia(Integer oximetriaReferencia) {
        this.oximetriaReferencia = oximetriaReferencia;
    }

    public Double getTemperaturaReferencia() {
        return temperaturaReferencia;
    }

    public void setTemperaturaReferencia(Double temperaturaReferencia) {
        this.temperaturaReferencia = temperaturaReferencia;
    }

    public Integer getRitmoCardiacoReferencia() {
        return ritmoCardiacoReferencia;
    }

    public void setRitmoCardiacoReferencia(Integer ritmoCardiacoReferencia) {
        this.ritmoCardiacoReferencia = ritmoCardiacoReferencia;
    }

    public Integer getPresionSistolicaReferencia() {
        return presionSistolicaReferencia;
    }

    public void setPresionSistolicaReferencia(Integer presionSistolicaReferencia) {
        this.presionSistolicaReferencia = presionSistolicaReferencia;
    }

    public Integer getPresionDistolicaReferencia() {
        return presionDistolicaReferencia;
    }

    public void setPresionDistolicaReferencia(Integer presionDistolicaReferencia) {
        this.presionDistolicaReferencia = presionDistolicaReferencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
