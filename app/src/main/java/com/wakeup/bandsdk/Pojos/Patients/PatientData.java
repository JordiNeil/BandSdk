package com.wakeup.bandsdk.Pojos.Patients;

import android.service.autofill.UserData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.wakeup.bandsdk.Pojos.Conditions.ConditionData;
import com.wakeup.bandsdk.Pojos.DataUser;
import com.wakeup.bandsdk.Pojos.Ips.IpsData;

public class PatientData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("tipoIdentificacion")
    @Expose
    private String tipoIdentificacion;
    @SerializedName("identificacion")
    @Expose
    private Integer identificacion;
    @SerializedName("edad")
    @Expose
    private Integer edad;
    @SerializedName("sexo")
    @Expose
    private String sexo;
    @SerializedName("pesoKG")
    @Expose
    private Double pesoKG;
    @SerializedName("estaturaCM")
    @Expose
    private Integer estaturaCM;
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
    @SerializedName("medicacion")
    @Expose
    private String medicacion;
    @SerializedName("comentarios")
    @Expose
    private String comentarios;
    @SerializedName("condicion")
    @Expose
    private ConditionData conditionData;
    @SerializedName("ips")
    @Expose
    private IpsData ipsData;
    @SerializedName("user")
    @Expose
    private DataUser dataUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public Integer getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(Integer identificacion) {
        this.identificacion = identificacion;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Double getPesoKG() {
        return pesoKG;
    }

    public void setPesoKG(Double pesoKG) {
        this.pesoKG = pesoKG;
    }

    public Integer getEstaturaCM() {
        return estaturaCM;
    }

    public void setEstaturaCM(Integer estaturaCM) {
        this.estaturaCM = estaturaCM;
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

    public String getMedicacion() {
        return medicacion;
    }

    public void setMedicacion(String medicacion) {
        this.medicacion = medicacion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public ConditionData getCondicion() {
        return conditionData;
    }

    public void setCondicion(ConditionData conditionData) {
        this.conditionData = conditionData;
    }

    public IpsData getIps() {
        return ipsData;
    }

    public void setIps(IpsData ipsData) {
        this.ipsData = ipsData;
    }

    public DataUser getUser() {
        return dataUser;
    }

    public void setUser(DataUser dataUser) {
        this.dataUser = dataUser;
    }
}
