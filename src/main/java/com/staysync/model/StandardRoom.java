package com.staysync.model;

public class StandardRoom extends Room {
    public StandardRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, RoomType.STANDARD, pricePerNight);
    }
    @Override
    public double calculateTariff(int nights) { return getPricePerNight() * nights; }
}