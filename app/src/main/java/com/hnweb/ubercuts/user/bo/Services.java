package com.hnweb.ubercuts.user.bo;

public class Services {
    String servicesName, id,ref_id_category,ref_id_sub_category,default_price,todays_offer,category_name,sub_category_name;

    public String getRef_id_category() {
        return ref_id_category;
    }

    public void setRef_id_category(String ref_id_category) {
        this.ref_id_category = ref_id_category;
    }

    public String getRef_id_sub_category() {
        return ref_id_sub_category;
    }

    public void setRef_id_sub_category(String ref_id_sub_category) {
        this.ref_id_sub_category = ref_id_sub_category;
    }

    public String getDefault_price() {
        return default_price;
    }

    public void setDefault_price(String default_price) {
        this.default_price = default_price;
    }

    public String getTodays_offer() {
        return todays_offer;
    }

    public void setTodays_offer(String todays_offer) {
        this.todays_offer = todays_offer;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getServicesName() {
        return servicesName;
    }

    public void setServicesName(String servicesName) {
        this.servicesName = servicesName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return servicesName;
    }

}
