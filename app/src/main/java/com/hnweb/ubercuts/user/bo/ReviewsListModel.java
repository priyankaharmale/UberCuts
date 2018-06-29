package com.hnweb.ubercuts.user.bo;

import java.io.Serializable;

public class ReviewsListModel implements Serializable{

    private String reviews_id;
    private String reviews;
    private String rating;
    private String created_date;
    private String user_name;
    private String user_image;

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    private String sender_id;

    public String getReviews_id() {
        return reviews_id;
    }

    public void setReviews_id(String reviews_id) {
        this.reviews_id = reviews_id;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
