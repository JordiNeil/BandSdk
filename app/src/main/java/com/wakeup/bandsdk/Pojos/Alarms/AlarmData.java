package com.wakeup.bandsdk.Pojos.Alarms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.wakeup.bandsdk.Pojos.DataUser;

public class AlarmData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("timeInstant")
    @Expose
    private String timeInstant;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("procedimiento")
    @Expose
    private String procedimiento;
    @SerializedName("user")
    @Expose
    private DataUser dataUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimeInstant() {
        return timeInstant;
    }

    public void setTimeInstant(String timeInstant) {
        this.timeInstant = timeInstant;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public DataUser getUserData() {
        return dataUser;
    }

    public void setUserData(DataUser dataUser) {
        this.dataUser = dataUser;
    }
}
