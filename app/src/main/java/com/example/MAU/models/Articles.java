package com.example.MAU.models;

import java.io.Serializable;
import java.util.Date;

public class Articles implements Serializable{

    public Articles() {}
    private String article_id;
    private String title;
    private String description;

    public Articles(String title, String description, String articleText, String photo_URL) {
        this.title = title;
        this.description = description;
        this.articleText = articleText;
        this.photo_URL = photo_URL;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    private String articleText;
    private String photo_URL;

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto_URL() {
        return photo_URL;
    }

    public void setPhoto_URL(String photo_URL) {
        this.photo_URL = photo_URL;
    }
}
