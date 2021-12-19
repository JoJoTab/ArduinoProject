package com.example.arduinoproject.model;

public class UserModel {
    private String userid;
    private String uid;
    private String usernm;
    private String pushToken;
    private String roomnm;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsernm() {
        return usernm;
    }

    public void setUsernm(String usernm) {
        this.usernm = usernm;
    }

    public String getToken() {
        return pushToken;
    }

    public void setToken(String token) {
        this.pushToken = token;
    }

    public String getRoomnm() { return roomnm; }

    public void setRoomnm(String roomnm) {
        this.roomnm = roomnm;
    }
}