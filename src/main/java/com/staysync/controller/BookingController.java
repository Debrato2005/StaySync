package com.staysync.controller;

import java.time.LocalDate;

import com.staysync.model.Booking;
import com.staysync.model.Guest;
import com.staysync.model.Room;
import com.staysync.util.DataStore;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class BookingController {
    private final DataStore store = DataStore.getInstance();

    public ObservableList<Guest> getAllGuests() {
        return FXCollections.observableArrayList(store.getGuests());
    }

    public ObservableList<Room> getAvailableRooms() {
        return store.getRooms().stream()
                .filter(Room::isAvailable)
                .collect(FXCollections::observableArrayList,
                         ObservableList::add,
                         ObservableList::addAll);
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
        if (!checkOut.isAfter(checkIn)) {
            showAlert("Check-out must be after check-in.");
            return false;
        }
        boolean ok = store.bookRoom(room.getRoomNumber(), guest, checkIn, checkOut);
        if (!ok) {
            showAlert("Room is no longer available.");
            return false;
        }
        return true;
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}