package com.staysync.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.staysync.model.Booking;
import com.staysync.model.Guest;
import com.staysync.model.Room;

public class DataStore {
    private static DataStore instance;
    private final List<Room> rooms         = new ArrayList<>();
    private final List<Guest> guests       = new ArrayList<>();
    private final List<Booking> bookings   = new ArrayList<>();
    private final AtomicInteger bookingIdGen = new AtomicInteger(1);

    private DataStore() {}

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public synchronized void addRoom(Room r) {
        rooms.add(r);
    }

    public synchronized void addGuest(Guest g) {
        guests.add(g);
    }

    public synchronized boolean bookRoom(int roomNumber, Guest guest,
                                          LocalDate checkIn, LocalDate checkOut) {
        if (guest == null) return false;
        if (checkOut.isBefore(checkIn)) return false;
        Room room = rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber && r.isAvailable())
                .findFirst().orElse(null);
        if (room == null) return false;
        room.setAvailable(false);
        bookings.add(new Booking(bookingIdGen.getAndIncrement(),
                                 guest, room, checkIn, checkOut));
        return true;
    }

    public synchronized boolean checkout(int bookingId) {
        Booking booking = bookings.stream()
                .filter(b -> b.getBookingId() == bookingId && b.isActive())
                .findFirst().orElse(null);
        if (booking == null) return false;
        booking.setActive(false);
        booking.getRoom().setAvailable(true);
        return true;
    }

    public List<Room> getRooms()       { return Collections.unmodifiableList(rooms); }
    public List<Guest> getGuests()     { return Collections.unmodifiableList(guests); }
    public List<Booking> getBookings() { return Collections.unmodifiableList(bookings); }
    public synchronized void addBooking(Booking b) {
    bookings.add(b);
    int id = b.getBookingId();
    if (id >= bookingIdGen.get()) bookingIdGen.set(id + 1);
}
}