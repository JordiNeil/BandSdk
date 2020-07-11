package com.wakeup.bandsdk.Pojos.Authenticate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JWTAuth {

    @SerializedName("id_token")
    @Expose
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "{" +
                "id_token='" + idToken + '\'' +
                '}';
    }
}
