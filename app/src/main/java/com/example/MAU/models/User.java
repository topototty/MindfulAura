package com.example.MAU.models;

public class User {
    private String email;
    private String uid;
    private String displayName;
    private String photo;

    public User() {

    }
    public User(String email, String uid, String displayName, String photo) {
        this.email = email;
        this.uid = uid;
        this.displayName = displayName;
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoto() { return photo; }

    public void setPhoto(String photo) { this.photo = photo; }


}
