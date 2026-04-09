package com.staysync.controller;

import java.util.function.Consumer;

import com.staysync.model.Booking;
import com.staysync.util.BillingThread;
import com.staysync.util.DataStore;
import com.staysync.util.PersistenceManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class CheckoutController {
    private final DataStore store = DataStore.getInstance();

    public ObservableList<Booking> getActiveBookings() {
        return store.getBookings().stream()
                .filter(Booking::isActive)
                .collect(FXCollections::observableArrayList,
                         ObservableList::add,
                         ObservableList::addAll);
    }

    public void checkout(Booking booking, boolean removeGuest, Consumer<Double> onBillReady) {
        if (booking == null) {
            new Alert(Alert.AlertType.ERROR, "Select a booking.").showAndWait();
            return;
        }
        boolean ok = store.checkout(booking.getBookingId());
        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Checkout failed.").showAndWait();
            return;
        }
        // fix: optionally remove guest on checkout
        if (removeGuest) {
            store.removeGuest(booking.getGuest().getGuestId());
        }
        // fix: persist immediately after checkout (no crash = data loss)
        persistAll();
        new BillingThread(booking, onBillReady).start();
    }

    private void persistAll() {
        PersistenceManager.save(store.getRooms(),    "rooms.dat");
        PersistenceManager.save(store.getGuests(),   "guests.dat");
        PersistenceManager.save(store.getBookings(), "bookings.dat");
    }
}