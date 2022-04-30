package com.mananhirak.annadata.dbhelpers;

import java.util.List;

public class MARKET {

    private String RETAILER_ID;
    private String FOOD_NAME;
    private int FOOD_PRICE;
    private int FOOD_POINT;
    private int FOOD_WEIGHT;
    private String FOOD_INFO;
    private int TOTAL_FOOD;
    private String FOOD_TIME;
    private String FOOD_DATE;
    private boolean NO_FOOD_LEFT;
    private int LEFT_ORDER;
    private String PHOTO_URL;
    private String CITY_NAME;
    private String TIFFIN_LINK;
    private List<Integer> ALL_RATING;
    private List<String> ALL_REVIEW;

    public MARKET() {
    }

    public List<String> getALL_REVIEW() {
        return ALL_REVIEW;
    }

    public void setALL_REVIEW(List<String> ALL_REVIEW) {
        this.ALL_REVIEW = ALL_REVIEW;
    }

    public List<Integer> getALL_RATING() {
        return ALL_RATING;
    }

    public void setALL_RATING(List<Integer> ALL_RATING) {
        this.ALL_RATING = ALL_RATING;
    }

    public String getTIFFIN_LINK() {
        return TIFFIN_LINK;
    }

    public void setTIFFIN_LINK(String TIFFIN_LINK) {
        this.TIFFIN_LINK = TIFFIN_LINK;
    }

    public String getRETAILER_ID() {
        return RETAILER_ID;
    }

    public void setRETAILER_ID(String RETAILER_ID) {
        this.RETAILER_ID = RETAILER_ID;
    }

    public String getFOOD_NAME() {
        return FOOD_NAME;
    }

    public void setFOOD_NAME(String FOOD_NAME) {
        this.FOOD_NAME = FOOD_NAME;
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

    public int getFOOD_WEIGHT() {
        return FOOD_WEIGHT;
    }

    public void setFOOD_WEIGHT(int FOOD_WEIGHT) {
        this.FOOD_WEIGHT = FOOD_WEIGHT;
    }

    public String getFOOD_INFO() {
        return FOOD_INFO;
    }

    public void setFOOD_INFO(String FOOD_INFO) {
        this.FOOD_INFO = FOOD_INFO;
    }

    public int getTOTAL_FOOD() {
        return TOTAL_FOOD;
    }

    public void setTOTAL_FOOD(int TOTAL_FOOD) {
        this.TOTAL_FOOD = TOTAL_FOOD;
    }

    public String getFOOD_TIME() {
        return FOOD_TIME;
    }

    public void setFOOD_TIME(String FOOD_TIME) {
        this.FOOD_TIME = FOOD_TIME;
    }

    public String getFOOD_DATE() {
        return FOOD_DATE;
    }

    public void setFOOD_DATE(String FOOD_DATE) {
        this.FOOD_DATE = FOOD_DATE;
    }

    public boolean getNO_FOOD_LEFT() {
        return NO_FOOD_LEFT;
    }

    public void setNO_FOOD_LEFT(boolean NO_FOOD_LEFT) {
        this.NO_FOOD_LEFT = NO_FOOD_LEFT;
    }

    public int getLEFT_ORDER() {
        return LEFT_ORDER;
    }

    public void setLEFT_ORDER(int LEFT_ORDER) {
        this.LEFT_ORDER = LEFT_ORDER;
    }

    public String getPHOTO_URL() {
        return PHOTO_URL;
    }

    public void setPHOTO_URL(String PHOTO_URL) {
        this.PHOTO_URL = PHOTO_URL;
    }

    public String getCITY_NAME() {
        return CITY_NAME;
    }

    public void setCITY_NAME(String CITY_NAME) {
        this.CITY_NAME = CITY_NAME;
    }

    @Override
    public String toString() {
        return "MARKET{" +
                "RETAILER_ID='" + RETAILER_ID + '\'' +
                ", FOOD_NAME='" + FOOD_NAME + '\'' +
                ", FOOD_PRICE=" + FOOD_PRICE +
                ", FOOD_POINT=" + FOOD_POINT +
                ", FOOD_WEIGHT=" + FOOD_WEIGHT +
                ", FOOD_INFO='" + FOOD_INFO + '\'' +
                ", TOTAL_FOOD=" + TOTAL_FOOD +
                ", FOOD_TIME='" + FOOD_TIME + '\'' +
                ", FOOD_DATE='" + FOOD_DATE + '\'' +
                ", NO_FOOD_LEFT=" + NO_FOOD_LEFT +
                ", LEFT_ORDER=" + LEFT_ORDER +
                ", PHOTO_URL='" + PHOTO_URL + '\'' +
                ", CITY_NAME='" + CITY_NAME + '\'' +
                ", TIFFIN_LINK='" + TIFFIN_LINK + '\'' +
                ", ALL_RATING=" + ALL_RATING +
                ", ALL_REVIEW=" + ALL_REVIEW +
                '}';
    }
}
