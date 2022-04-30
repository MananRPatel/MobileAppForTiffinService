package com.mananhirak.annadata.dbhelpers;

import com.google.firebase.firestore.GeoPoint;

public class FOOD_USERS {

    private String USER_NAME;
    private String ID;
    private String ADDRESS;
    private String PHOTO_URL;
    private String TIFFIN_LINK;
    private String FCM_TOKEN;
    private GeoPoint USER_LOCATION;

    public FOOD_USERS() {
    }


    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getPHOTO_URL() {
        return PHOTO_URL;
    }

    public void setPHOTO_URL(String PHOTO_URL) {
        this.PHOTO_URL = PHOTO_URL;
    }

    public String getFCM_TOKEN() {
        return FCM_TOKEN;
    }

    public void setFCM_TOKEN(String FCM_TOKEN) {
        this.FCM_TOKEN = FCM_TOKEN;
    }

    public GeoPoint getUSER_LOCATION() {
        return USER_LOCATION;
    }

    public void setUSER_LOCATION(GeoPoint USER_LOCATION) {
        this.USER_LOCATION = USER_LOCATION;
    }

    public String getTIFFIN_LINK() {
        return TIFFIN_LINK;
    }

    public void setTIFFIN_LINK(String TIFFIN_LINK) {
        this.TIFFIN_LINK = TIFFIN_LINK;
    }

    @Override
    public String toString() {
        return "FOOD_USERS{" +
                "USER_NAME='" + USER_NAME + '\'' +
                ", ID='" + ID + '\'' +
                ", ADDRESS='" + ADDRESS + '\'' +
                ", PHOTO_URL='" + PHOTO_URL + '\'' +
                ", TIFFIN_LINK='" + TIFFIN_LINK + '\'' +
                ", FCM_TOKEN='" + FCM_TOKEN + '\'' +
                ", USER_LOCATION=" + USER_LOCATION +
                '}';
    }
}
