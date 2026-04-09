package com.staysync.controller;

import com.staysync.model.Guest;
import com.staysync.util.DataStore;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class GuestController {
    private final DataStore store = DataStore.getInstance();

    public ObservableList<Guest> getAllGuests() {
        return FXCollections.observableArrayList(store.getGuests());
    }

    public boolean addGuest(String name, String contact, String idProof) {
        if (name.isBlank() || contact.isBlank() || idProof.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "All fields are required.").showAndWait();
            return false;
        }
        // fix: use DataStore's atomic guest ID generator
        store.addGuest(new Guest(store.nextGuestId(), name.trim(), contact.trim(), idProof.trim()));
        return true;
    }

    public void removeGuest(int guestId) {
        store.removeGuest(guestId);
    }
}