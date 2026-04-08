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

    int nextId = store.getGuests().stream()
            .mapToInt(Guest::getGuestId)
            .max()
            .orElse(0) + 1;

    store.addGuest(new Guest(nextId, name.trim(), contact.trim(), idProof.trim()));
    return true;
}
    }
