package com.hnweb.ubercuts.vendor.bo;

import java.io.Serializable;

public class MyWorkModel implements Serializable{


    private String message_code;
    private String message;

    private String my_work_images_id;
    private String my_work_images_name;
    private String category_images_name;

    private String my_work_videos_id;
    private String my_work_videos_name;
    private String category_videos_name;
    private String my_work_videos_thumb;

    public String getMessage_code() {
        return message_code;
    }

    public void setMessage_code(String message_code) {
        this.message_code = message_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMy_work_images_id() {
        return my_work_images_id;
    }

    public void setMy_work_images_id(String my_work_images_id) {
        this.my_work_images_id = my_work_images_id;
    }

    public String getMy_work_images_name() {
        return my_work_images_name;
    }

    public void setMy_work_images_name(String my_work_images_name) {
        this.my_work_images_name = my_work_images_name;
    }

    public String getCategory_images_name() {
        return category_images_name;
    }

    public void setCategory_images_name(String category_images_name) {
        this.category_images_name = category_images_name;
    }

    public String getMy_work_videos_id() {
        return my_work_videos_id;
    }

    public void setMy_work_videos_id(String my_work_videos_id) {
        this.my_work_videos_id = my_work_videos_id;
    }

    public String getMy_work_videos_name() {
        return my_work_videos_name;
    }

    public void setMy_work_videos_name(String my_work_videos_name) {
        this.my_work_videos_name = my_work_videos_name;
    }

    public String getCategory_videos_name() {
        return category_videos_name;
    }

    public void setCategory_videos_name(String category_videos_name) {
        this.category_videos_name = category_videos_name;
    }

    public String getMy_work_videos_thumb() {
        return my_work_videos_thumb;
    }

    public void setMy_work_videos_thumb(String my_work_videos_thumb) {
        this.my_work_videos_thumb = my_work_videos_thumb;
    }
}
