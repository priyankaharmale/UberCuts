package com.hnweb.ubercuts.vendor.bo;

/**
 * Created by PC-21 on 04-Apr-18.
 */

public class Category {

    private String message_code;
    private String message;
    private String category_id;
    private String category_name;

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

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public String toString() {
        return category_name;
    }

    public static class SubCategory{

        private String sub_category_id;
        private String sub_category_name;
        private String ref_id_category;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        boolean isSelected;

        public String getSub_category_id() {
            return sub_category_id;
        }

        public void setSub_category_id(String sub_category_id) {
            this.sub_category_id = sub_category_id;
        }

        public String getSub_category_name() {
            return sub_category_name;
        }

        public void setSub_category_name(String sub_category_name) {
            this.sub_category_name = sub_category_name;
        }

        public String getRef_id_category() {
            return ref_id_category;
        }

        public void setRef_id_category(String ref_id_category) {
            this.ref_id_category = ref_id_category;
        }

        @Override
        public String toString() {
            return sub_category_name;
        }
    }
}
