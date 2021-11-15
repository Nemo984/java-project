package com.example.myapplication.News;

public class News {
    String urlnews;
    String urlimage;
    String title;

    public News(String title,String urlimage,String urlnews) {
        this.urlnews = urlnews;
        this.urlimage = urlimage;
        this.title = title;
    }

    public String getUrlnews() {
        return urlnews;
    }

    public void setUrlnews(String urlnews) {
        this.urlnews = urlnews;
    }

    public String getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(String urlimage) {
        this.urlimage = urlimage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
