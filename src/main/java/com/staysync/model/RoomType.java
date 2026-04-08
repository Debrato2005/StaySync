package com.staysync.model;

public enum RoomType {
    STANDARD(2000), DELUXE(3500), SUITE(5000);

    private final int basePrice;
    RoomType(int basePrice) { this.basePrice = basePrice; }
    public int getBasePrice() { return basePrice; }
    public double calculateCost(int nights) { return basePrice * nights; }
}