package com.example.MAU.models;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String note_id;
    private Date date;
    private String description;
    private  String user_id;
    public Note() {}

    public Note(Date date, String description, String user_id) {
        this.date = date;
        this.description = description;
        this.user_id = user_id;
    }

    public String getNote_id() {
        return note_id;
    }

    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }

    public void setDate(Date date) {this.date = date;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getDate() {
        return date;
    }

}
