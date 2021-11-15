package com.example.myapplication;

public class Timeline {
    private String Name;
    private String Date;

    public Timeline(String name, String date) {
        Name = name;
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public String getDate() {
        return Date;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDate(String date) {
        Date = date;
    }
}
