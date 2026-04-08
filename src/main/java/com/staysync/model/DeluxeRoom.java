package com.staysync.model;

public class DeluxeRoom extends Room {
    public DeluxeRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, RoomType.DELUXE, pricePerNight);
    }
    @Override
    public double calculateTariff(int nights) { return getPricePerNight() * nights * 1.20; }
}