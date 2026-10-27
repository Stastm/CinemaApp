package com.example.cinemaapp.models;

public class Seat {
    private int id;
    private int sessionId;
    private int rowNumber;
    private int seatNumber;
    private boolean isBooked;
    private boolean isSelected;

    public Seat(int id, int sessionId, int rowNumber, int seatNumber, boolean isBooked) {
        this.id = id;
        this.sessionId = sessionId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
        this.isSelected = false;
    }

    public int getId() { return id; }
    public int getSessionId() { return sessionId; }
    public int getRowNumber() { return rowNumber; }
    public int getSeatNumber() { return seatNumber; }
    public boolean isBooked() { return isBooked; }
    public boolean isSelected() { return isSelected; }

    public void setBooked(boolean booked) { isBooked = booked; }
    public void setSelected(boolean selected) { isSelected = selected; }
}