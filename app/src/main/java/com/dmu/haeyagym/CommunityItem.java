package com.dmu.haeyagym;

public class CommunityItem {
    private int brdid;
    private String uid;
    private String title;
    private String date;
    private String region;
    private String description;
    private String categoty;

    public CommunityItem(){
        this.title = "";
        this.date = "";
        this.region = "";
        this.description = "";
        this.categoty = "";
        this.uid = "";
        this.brdid = 0;
    }
    public CommunityItem(String title, String date, String region, String description, String categoty, String id, int brdid) {
        this.title = title;
        this.date = date;
        this.region = region;
        this.description = description;
        this.categoty = categoty;
        this.uid = id;
        this.brdid = brdid;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getBrdid() {
        return brdid;
    }

    public void setBrdid(int brdid) {
        this.brdid = brdid;
    }
}
