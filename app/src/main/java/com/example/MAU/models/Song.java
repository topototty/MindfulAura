package com.example.MAU.models;
public class Song {
    private String song_id;
    private String title;
    private String description;
    private String image_url;
    private String song_url;

    public Song(){}

    public Song(String title, String description, String image_url, String song_url) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.song_url = song_url;
    }

    public String getSong_id() {return song_id;}

    public void setSong_id(String song_id) {this.song_id = song_id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}
    public String getImage_url() {return image_url;}

    public void setImage_url(String image_url) {this.image_url = image_url;}

    public String getSong_url() {return song_url;}

    public void setSong_url(String song_url) {this.song_url = song_url;}
}
