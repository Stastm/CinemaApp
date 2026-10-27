package com.example.cinemaapp.models;

public class Session {
    private int id;
    private int movieId;
    private String date;
    private String time;
    private double price;

    public Session(int id, int movieId, String date, String time, double price) {
        this.id = id;
        this.movieId = movieId;
        this.date = date;
        this.time = time;
        this.price = price;
    }

    public Session(int movieId, String date, String time, double price) {
        this.movieId = movieId;
        this.date = date;
        this.time = time;
        this.price = price;
    }

    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public double getPrice() { return price; }

    public void setId(int id) { this.id = id; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setPrice(double price) { this.price = price; }
}