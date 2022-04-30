package com.mananhirak.annadata.dbhelpers;

public class BUY_HISTORY {

    private String FOOD_ID;
    private String RETAILER_ID;
    private String FOOD_NAME;
    private int FOOD_WEIGHT;
    private int TOTAL_FOOD;
    private int FOOD_PRICE;
    private int FOOD_POINT;
    private String FOOD_INFO;
    private int ORDER_STATUS;
    private int RATING_FOOD;


    public static final int ORDER_CONFORM=1;
    public static final int ORDER_CANCEL=0;
    public static final int ORDER_PLACED=2;


    public BUY_HISTORY() {
    }

    public String getRETAILER_ID() {
        return RETAILER_ID;
    }

    public void setRETAILER_ID(String RETAILER_ID) {
        this.RETAILER_ID = RETAILER_ID;
    }

    public int getRATING_FOOD() {
        return RATING_FOOD;
    }

    public void setRATING_FOOD(int RATING_FOOD) {
        this.RATING_FOOD = RATING_FOOD;
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

    public int getFOOD_WEIGHT() {
        return FOOD_WEIGHT;
    }

    public void setFOOD_WEIGHT(int FOOD_WEIGHT) {
        this.FOOD_WEIGHT = FOOD_WEIGHT;
    }

    public int getTOTAL_FOOD() {
        return TOTAL_FOOD;
    }

    public void setTOTAL_FOOD(int TOTAL_FOOD) {
        this.TOTAL_FOOD = TOTAL_FOOD;
    }

    public int getFOOD_PRICE() {
        return FOOD_PRICE;
    }

    public void setFOOD_PRICE(int FOOD_PRICE) {
        this.FOOD_PRICE = FOOD_PRICE;
    }

    public int getFOOD_POINT() {
        return FOOD_POINT;
    }

    public void setFOOD_POINT(int FOOD_POINT) {
        this.FOOD_POINT = FOOD_POINT;
    }

    public String getFOOD_INFO() {
        return FOOD_INFO;
    }

    public void setFOOD_INFO(String FOOD_INFO) {
        this.FOOD_INFO = FOOD_INFO;
    }

    public int getORDER_STATUS() {
        return ORDER_STATUS;
    }

    public void setORDER_STATUS(int ORDER_STATUS) {
        this.ORDER_STATUS = ORDER_STATUS;
    }

    @Override
    public String toString() {
        return "BUY_HISTORY{" +
                "FOOD_ID='" + FOOD_ID + '\'' +
                ", RETAILER_ID='" + RETAILER_ID + '\'' +
                ", FOOD_NAME='" + FOOD_NAME + '\'' +
                ", FOOD_WEIGHT=" + FOOD_WEIGHT +
                ", TOTAL_FOOD=" + TOTAL_FOOD +
                ", FOOD_PRICE=" + FOOD_PRICE +
                ", FOOD_POINT=" + FOOD_POINT +
                ", FOOD_INFO='" + FOOD_INFO + '\'' +
                ", ORDER_STATUS=" + ORDER_STATUS +
                ", RATING_FOOD=" + RATING_FOOD +
                '}';
    }
}