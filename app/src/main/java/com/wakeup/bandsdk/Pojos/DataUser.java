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

    public String loginget() {
        return login;
    }

    public void loginset(String login) {
        this.login = login;
    }

    public String firstNameget() {
        return firstName;
    }

    public void firstNameset(String firstName) {
        this.firstName = firstName;
    }

    public String lastNameget() {
        return lastName;
    }

    public  void lastNameset(String lastName) {
        this.lastName = lastName;
    }

    public  String emailget() {
        return email;
    }

    public void emailset( String email) {
        this.email = email;
    }

    public String imageUrlget() {
        return imageUrl;
    }

    public void imageUrlset(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Boolean activatedget() {
        return activated;
    }

    public void activatedset(Boolean activated) {
        this.activated = activated;
    }

    public String langKeyget() {
        return langKey;
    }

    public void langKeyset(String langKey) {
        this.langKey = langKey;
    }

    public String createdByget() {
        return createdBy;
    }

    public void createdByset(String createdBy) {
        this.createdBy = createdBy;
    }

    public String createdDateget() {
        return createdDate;
    }

    public void createdDateset(String createdDate) {
        this.createdDate = createdDate;
    }

    public String lastModifiedBy() {
        return imageUrl;
    }

    public void lastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String lastModifiedDateget() {
        return lastModifiedDate;
    }

    public void lastModifiedDateset(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }



}
