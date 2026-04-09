package com.staysync.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.staysync.model.Booking;
import com.staysync.model.Guest;
import com.staysync.model.Room;

public class DataStore {
    private static volatile DataStore instance;
    private final List<Room>    rooms    = new ArrayList<>();
    private final List<Guest>   guests   = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final AtomicInteger bookingIdGen = new AtomicInteger(1);
    private final AtomicInteger guestIdGen   = new AtomicInteger(1);

    private DataStore() {}

    public static DataStore getInstance() {
        if (instance == null) {
            synchronized (DataStore.class) {
                if (instance == null) instance = new DataStore();
            }
        }
        return instance;
    }

    public synchronized void addRoom(Room r) {
        boolean exists = rooms.stream().anyMatch(x -> x.getRoomNumber() == r.getRoomNumber());
        if (!exists) rooms.add(r);
    }

    public synchronized void addGuest(Guest g) {
        boolean exists = guests.stream().anyMatch(x -> x.getGuestId() == g.getGuestId());
        if (!exists) {
            guests.add(g);
            if (g.getGuestId() >= guestIdGen.get()) guestIdGen.set(g.getGuestId() + 1);
        }
    }

    public synchronized int nextGuestId() {
        return guestIdGen.getAndIncrement();
    }

    private boolean hasOverlap(Room room, LocalDate checkIn, LocalDate checkOut) {
        return bookings.stream()
                .filter(b -> b.isActive() && b.getRoom().getRoomNumber() == room.getRoomNumber())
                .anyMatch(b -> !checkOut.isBefore(b.getCheckIn()) && !checkIn.isAfter(b.getCheckOut()));
    }

    public synchronized boolean bookRoom(int roomNumber, Guest guest,
                                          LocalDate checkIn, LocalDate checkOut) {
        if (guest == null || checkIn == null || checkOut == null) return false;
        if (checkOut.isBefore(checkIn)) return false;
        if (checkIn.isBefore(LocalDate.now())) return false;

        Room room = rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst().orElse(null);
        if (room == null) return false;
        if (hasOverlap(room, checkIn, checkOut)) return false;

        // fix: derive available flag from active bookings, not manual toggle
        room.setAvailable(false);
        bookings.add(new Booking(bookingIdGen.getAndIncrement(), guest, room, checkIn, checkOut));
        return true;
    }

    public synchronized boolean checkout(int bookingId) {
        Booking booking = bookings.stream()
                .filter(b -> b.getBookingId() == bookingId && b.isActive())
                .findFirst().orElse(null);
        if (booking == null) return false;
        booking.setCheckOut(LocalDate.now());
        booking.setActive(false);
        // fix: recompute available flag — only mark available if no other active booking exists
        Room room = booking.getRoom();
        boolean stillBooked = bookings.stream()
                .anyMatch(b -> b.isActive() && b.getRoom().getRoomNumber() == room.getRoomNumber());
        room.setAvailable(!stillBooked);
        return true;
    }

    public synchronized List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return rooms.stream()
                .filter(r -> !hasOverlap(r, checkIn, checkOut))
                .collect(Collectors.toList());
    }

    /** Recomputes the available flag for all rooms from active bookings. Call after load. */
    public synchronized void recomputeAvailability() {
        for (Room room : rooms) {
            boolean booked = bookings.stream()
                    .anyMatch(b -> b.isActive()
                            && b.getRoom().getRoomNumber() == room.getRoomNumber());
            room.setAvailable(!booked);
        }
    }

    public synchronized void removeGuest(int guestId) {
        guests.removeIf(g -> g.getGuestId() == guestId);
    }

    public List<Room>    getRooms()    { return Collections.unmodifiableList(rooms); }
    public List<Guest>   getGuests()   { return Collections.unmodifiableList(guests); }
    public List<Booking> getBookings() { return Collections.unmodifiableList(bookings); }

    public synchronized void addBooking(Booking b) {
        boolean exists = bookings.stream().anyMatch(x -> x.getBookingId() == b.getBookingId());
        if (!exists) {
            bookings.add(b);
            if (b.getBookingId() >= bookingIdGen.get()) bookingIdGen.set(b.getBookingId() + 1);
        }
    }
}