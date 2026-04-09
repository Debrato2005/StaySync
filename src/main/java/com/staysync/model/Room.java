package com.staysync.model;

import java.io.Serializable;

public abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private int roomNumber;
    private RoomType roomType;
    private double pricePerNight;
    private boolean available = true;

    public Room(int roomNumber, RoomType roomType, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
    }

    public abstract double calculateTariff(int nights);

    public int getRoomNumber()       { return roomNumber; }
    public RoomType getRoomType()    { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable()     { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setPricePerNight(double price)  { this.pricePerNight = price; } // fix: for edit room

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ") - ₹" + pricePerNight;
    }
}