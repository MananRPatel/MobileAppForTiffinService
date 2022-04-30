package com.mananhirak.annadata.dbhelpers;

import com.google.firebase.firestore.GeoPoint;

public class DELIVERY_BOY {

    private String BUYER_ID;
    private String FOOD_ID;
    private String FOOD_NAME;
    private int TOTAL_ORDER;
    private int FOOD_POINT;
    private int FOOD_MONEY;
    private String DELIVERY_TIME;
    private boolean CONFORM_DELIVERY;
    private boolean CANCEL_DELIVERY;
    private String BUYER_HISTORY_ID;
    private GeoPoint DELIVERY_ADDRESS;

    public GeoPoint getDELIVERY_ADDRESS() {
        return DELIVERY_ADDRESS;
    }

    public void setDELIVERY_ADDRESS(GeoPoint DELIVERY_ADDRESS) {
        this.DELIVERY_ADDRESS = DELIVERY_ADDRESS;
    }

    public DELIVERY_BOY() {
        this.CANCEL_DELIVERY=false;
    }

    public String getBUYER_ID() {
        return BUYER_ID;
    }

    public void setBUYER_ID(String BUYER_ID) {
        this.BUYER_ID = BUYER_ID;
    }


    public String getFOOD_ID() {
        return FOOD_ID;
    }

    public void setFOOD_ID(String FOOD_ID) {
        this.FOOD_ID = FOOD_ID;
    }

    public String getFOOD_NAME() {
        return FOOD_NAME;
    }

    public void setFOOD_NAME(String FOOD_NAME) {
        this.FOOD_NAME = FOOD_NAME;
    }

    public int getTOTAL_ORDER() {
        return TOTAL_ORDER;
    }

    public void setTOTAL_ORDER(int TOTAL_ORDER) {
        this.TOTAL_ORDER = TOTAL_ORDER;
    }

    public int getFOOD_POINT() {
        return FOOD_POINT;
    }

    public void setFOOD_POINT(int FOOD_POINT) {
        this.FOOD_POINT = FOOD_POINT;
    }

    public int getFOOD_MONEY() {
        return FOOD_MONEY;
    }

    public void setFOOD_MONEY(int FOOD_MONEY) {
        this.FOOD_MONEY = FOOD_MONEY;
    }



    public String getDELIVERY_TIME() {
        return DELIVERY_TIME;
    }

    public void setDELIVERY_TIME(String DELIVERY_TIME) {
        this.DELIVERY_TIME = DELIVERY_TIME;
    }

    public boolean getCONFORM_DELIVERY() {
        return CONFORM_DELIVERY;
    }

    public void setCONFORM_DELIVERY(boolean CONFORM_DELIVERY) {
        this.CONFORM_DELIVERY = CONFORM_DELIVERY;
    }


    public boolean getCANCEL_DELIVERY() {
        return CANCEL_DELIVERY;
    }

    public void setCANCEL_DELIVERY(boolean CANCEL_DELIVERY) {
        this.CANCEL_DELIVERY = CANCEL_DELIVERY;
    }

    public String getBUYER_HISTORY_ID() {
        return BUYER_HISTORY_ID;
    }

    public void setBUYER_HISTORY_ID(String BUYER_HISTORY_ID) {
        this.BUYER_HISTORY_ID = BUYER_HISTORY_ID;
    }

    @Override
    public String toString() {
        return "DELIVERY_BOY{" +
                "BUYER_ID='" + BUYER_ID + '\'' +
                ", FOOD_ID='" + FOOD_ID + '\'' +
                ", FOOD_NAME='" + FOOD_NAME + '\'' +
                ", TOTAL_ORDER=" + TOTAL_ORDER +
                ", FOOD_POINT=" + FOOD_POINT +
                ", FOOD_MONEY=" + FOOD_MONEY +
                ", DELIVERY_TIME='" + DELIVERY_TIME + '\'' +
                ", CONFORM_DELIVERY=" + CONFORM_DELIVERY +
                ", CANCEL_DELIVERY=" + CANCEL_DELIVERY +
                ", BUYER_HISTORY_ID='" + BUYER_HISTORY_ID + '\'' +
                ", DELIVERY_ADDRESS=" + DELIVERY_ADDRESS +
                '}';
    }
}