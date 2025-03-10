package com.example.MAU.models;

public class User {
    private String email;
    private String uid;
    private String displayName;

    public User() {

    }
    public User(String email, String uid, String displayName) {
        this.email = email;
        this.uid = uid;
        this.displayName = displayName;
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

}
