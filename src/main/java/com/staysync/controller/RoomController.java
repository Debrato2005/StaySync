package com.staysync.controller;

import com.staysync.model.DeluxeRoom;
import com.staysync.model.Room;
import com.staysync.model.RoomType;
import com.staysync.model.StandardRoom;
import com.staysync.model.SuiteRoom;
import com.staysync.util.DataStore;
import com.staysync.util.PersistenceManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class RoomController {
    private final DataStore store = DataStore.getInstance();

    public ObservableList<Room> getAllRooms() {
        return FXCollections.observableArrayList(store.getRooms());
    }

    public ObservableList<Room> getAvailableRooms() {
        return store.getRooms().stream()
                .filter(Room::isAvailable)
                .collect(FXCollections::observableArrayList,
                         ObservableList::add,
                         ObservableList::addAll);
    }

    public boolean addRoom(String roomNumStr, RoomType type, String priceStr) {
        try {
            int num = Integer.parseInt(roomNumStr.trim());
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) {
                new Alert(Alert.AlertType.ERROR, "Price must be greater than 0.").showAndWait();
                return false;
            }
            if (store.getRooms().stream().anyMatch(r -> r.getRoomNumber() == num)) {
                new Alert(Alert.AlertType.ERROR, "Room " + num + " already exists.").showAndWait();
                return false;
            }
            Room room = switch (type) {
                case STANDARD -> new StandardRoom(num, price);
                case DELUXE   -> new DeluxeRoom(num, price);
                case SUITE    -> new SuiteRoom(num, price);
            };
            store.addRoom(room);
            persist();
            return true;
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid room number or price.").showAndWait();
            return false;
        }
    }

    public boolean updateRoomPrice(int roomNumber, String priceStr) {
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) {
                new Alert(Alert.AlertType.ERROR, "Price must be greater than 0.").showAndWait();
                return false;
            }
            // fix: return false if room doesn't exist instead of silently succeeding
            Room room = store.getRooms().stream()
                    .filter(r -> r.getRoomNumber() == roomNumber)
                    .findFirst().orElse(null);
            if (room == null) {
                new Alert(Alert.AlertType.ERROR, "Room not found.").showAndWait();
                return false;
            }
            room.setPricePerNight(price);
            persist();
            return true;
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid price.").showAndWait();
            return false;
        }
    }

    private void persist() {
        PersistenceManager.save(store.getRooms(), "rooms.dat");
    }
}