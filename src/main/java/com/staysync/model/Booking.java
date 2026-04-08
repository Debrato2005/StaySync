package com.staysync.model;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    private int bookingId;
    private Guest guest;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean active = true;

    public Booking(int bookingId, Guest guest, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public long getNights() {
        return Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
    }

    public double calculateTotal() {
    return room.calculateTariff((int) getNights());
}
    public int getBookingId() { return bookingId; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}