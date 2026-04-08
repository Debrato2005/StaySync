package com.staysync.model;

public class SuiteRoom extends Room {
    public SuiteRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, RoomType.SUITE, pricePerNight);
    }
    @Override
    public double calculateTariff(int nights) { return getPricePerNight() * nights * 1.40; }
}