package com.dmu.haeyagym;

public class CommunityItem {
    private String uid;
    private String title;
    private String date;
    private String region;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategoty(String categoty) {
        this.categoty = categoty;
    }


    public String getCategoty() {
        return categoty;
    }

    private String categoty;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
