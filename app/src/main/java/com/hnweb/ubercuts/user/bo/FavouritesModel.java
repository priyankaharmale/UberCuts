package com.hnweb.ubercuts.user.bo;

import java.io.Serializable;
import java.util.ArrayList;

public class FavouritesModel implements Serializable{

    private String favourite_id;
    private String u_id;
    private String u_name;
    private String u_img;
    private String experience;
    private String rating;
    private ArrayList<String> arrayList;

    public String getFavourite_id() {
        return favourite_id;
    }

    public void setFavourite_id(String favourite_id) {
        this.favourite_id = favourite_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_img() {
        return u_img;
    }

    public void setU_img(String u_img) {
        this.u_img = u_img;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }
}
