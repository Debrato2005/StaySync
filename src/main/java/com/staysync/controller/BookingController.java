package com.staysync.controller;

import java.time.LocalDate;

import com.staysync.model.Booking;
import com.staysync.model.Guest;
import com.staysync.model.Room;
import com.staysync.util.DataStore;
import com.staysync.util.PersistenceManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class BookingController {
    private final DataStore store = DataStore.getInstance();

    public ObservableList<Guest> getAllGuests() {
        return FXCollections.observableArrayList(store.getGuests());
    }

    public ObservableList<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || checkOut.isBefore(checkIn))
            return FXCollections.observableArrayList();
        return FXCollections.observableArrayList(store.getAvailableRooms(checkIn, checkOut));
    }

    public ObservableList<Booking> getAllBookings() {
        return FXCollections.observableArrayList(store.getBookings());
    }

    public boolean createBooking(Guest guest, Room room,
                                  LocalDate checkIn, LocalDate checkOut) {
        if (guest == null || room == null || checkIn == null || checkOut == null) {
            showAlert("All fields are required.");
            return false;
        }
        if (checkOut.isBefore(checkIn)) {
            showAlert("Check-out must be after check-in.");
            return false;
        }
        // fix: reject past check-in dates in UI layer too
        if (checkIn.isBefore(LocalDate.now())) {
            showAlert("Check-in date cannot be in the past.");
            return false;
        }
        boolean ok = store.bookRoom(room.getRoomNumber(), guest, checkIn, checkOut);
        if (!ok) {
            showAlert("Room is not available for the selected dates.");
            return false;
        }
        // fix: persist immediately after booking
        PersistenceManager.save(store.getRooms(),    "rooms.dat");
        PersistenceManager.save(store.getGuests(),   "guests.dat");
        PersistenceManager.save(store.getBookings(), "bookings.dat");
        return true;
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}