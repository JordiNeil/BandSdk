package com.wakeup.bandsdk.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataUser {

    @SerializedName("id")
    @Expose
    private int id;


    @SerializedName("login")
    @Expose
    private String login;


    @SerializedName("firstName")
    @Expose
    private String firstName;


    @SerializedName("lastName")
    @Expose
    private String lastName;


    @SerializedName("email")
    @Expose
    private String email;


    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;


    @SerializedName("activated")
    @Expose
    private Boolean activated;


    @SerializedName("langKey")
    @Expose
    private String langKey;


    @SerializedName("createdBy")
    @Expose
    private String createdBy;


    @SerializedName("createdDate")
    @Expose
    private String createdDate;


    @SerializedName("lastModifiedBy")
    @Expose
    private String lastModifiedBy;


    @SerializedName("lastModifiedDate")
    @Expose
    private String lastModifiedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public  void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public  String getEmail() {
        return email;
    }

    public void setEmail( String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "User: {" +
                "id =" + id + '\'' +
                ", login ='" + login + '\'' +
                ", firstName ='" + firstName + '\'' +
                ", lastName =" + lastName + '\'' +
                ", email =" + email + '\'' +
                ", imageUrl =" + imageUrl + '\'' +
                ", activated =" + activated + '\'' +
                ", langKey =" + langKey + '\'' +
                ", createdBy =" + createdBy + '\'' +
                ", createdDate =" + createdDate + '\'' +
                ", lastModifiedBy =" + lastModifiedBy + '\'' +
                ", lastModifiedDate =" + lastModifiedDate + '\'' +
                '}';
    }
}
