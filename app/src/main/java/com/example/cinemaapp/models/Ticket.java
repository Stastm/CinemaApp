package com.example.cinemaapp.models;

public class Ticket {
    private int id;
    private int userId;
    private int sessionId;
    private int seatId;
    private double totalPrice;

    public Ticket(int id, int userId, int sessionId, int seatId, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.seatId = seatId;
        this.totalPrice = totalPrice;
    }

    public Ticket(int userId, int sessionId, int seatId, double totalPrice) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.seatId = seatId;
        this.totalPrice = totalPrice;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getSessionId() { return sessionId; }
    public int getSeatId() { return seatId; }
    public double getTotalPrice() { return totalPrice; }
}