package com.example.cinemaapp.models;

public class Review {
    private int id;
    private int userId;
    private int movieId;
    private float rating;
    private String comment;
    private String date;

    public Review(int id, int userId, int movieId, float rating, String comment, String date) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getMovieId() { return movieId; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId)    { this.userId = userId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public void setRating(float rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setDate(String date) { this.date = date; }
}