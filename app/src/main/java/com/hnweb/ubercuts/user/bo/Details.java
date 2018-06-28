package com.hnweb.ubercuts.user.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC-21 on 05-Apr-18.
 */

public class Details {

    private String uId;
    private String uName;
    private String uEmail;
    private String distance;

    public String getuId() {
        return uId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    private String uBusinessName;
    private String uImg;
    private String experience;
    private String uStreet;
    private String uCity;
    private String uState;
    private String uCountry;
    private String uZipcode;
    private String latitude;
    private String longitude;
    private String serviceId;
    private String defaultPrice;
    private String todaysOffer;
    private String availableFrom;
    private String availableTo;
    private String categoryId;
    private String categoryName;
    private String subCategoryId;
    private String subCategoryName;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public String getUName() {
        return uName;
    }

    public void setUName(String uName) {
        this.uName = uName;
    }

    public String getUEmail() {
        return uEmail;
    }

    public void setUEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getUBusinessName() {
        return uBusinessName;
    }

    public void setUBusinessName(String uBusinessName) {
        this.uBusinessName = uBusinessName;
    }

    public String getUImg() {
        return uImg;
    }

    public void setUImg(String uImg) {
        this.uImg = uImg;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getUStreet() {
        return uStreet;
    }

    public void setUStreet(String uStreet) {
        this.uStreet = uStreet;
    }

    public String getUCity() {
        return uCity;
    }

    public void setUCity(String uCity) {
        this.uCity = uCity;
    }

    public String getUState() {
        return uState;
    }

    public void setUState(String uState) {
        this.uState = uState;
    }

    public String getUCountry() {
        return uCountry;
    }

    public void setUCountry(String uCountry) {
        this.uCountry = uCountry;
    }

    public String getUZipcode() {
        return uZipcode;
    }

    public void setUZipcode(String uZipcode) {
        this.uZipcode = uZipcode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(String defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public String getTodaysOffer() {
        return todaysOffer;
    }

    public void setTodaysOffer(String todaysOffer) {
        this.todaysOffer = todaysOffer;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(String availableTo) {
        this.availableTo = availableTo;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
