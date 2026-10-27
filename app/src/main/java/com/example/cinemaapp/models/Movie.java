package com.example.cinemaapp.models;

public class Movie {
    private int id;
    private String title;
    private String description;
    private int duration;
    private String poster;
    private float rating;

    public Movie(int id, String title, String description, int duration, String poster) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.poster = poster;
        this.rating = 0f;
    }

    public Movie(String title, String description, int duration, String poster) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.poster = poster;
        this.rating = 0f;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }
    public String getPoster() { return poster; }
    public float getRating() { return rating; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setPoster(String poster) { this.poster = poster; }
    public void setRating(float rating) { this.rating = rating; }
}