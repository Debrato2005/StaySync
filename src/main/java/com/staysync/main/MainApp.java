package com.staysync.main;

import java.util.List;

import com.staysync.model.Booking;
import com.staysync.model.Guest;
import com.staysync.model.Room;
import com.staysync.util.DataStore;
import com.staysync.util.PersistenceManager;
import com.staysync.view.BookingTab;
import com.staysync.view.CheckoutTab;
import com.staysync.view.DashboardTab;
import com.staysync.view.GuestTab;
import com.staysync.view.RoomTab;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        loadData();

        TabPane tabs = new TabPane();
        DashboardTab dashboardTab   = new DashboardTab();
        RoomTab roomTab             = new RoomTab();
        GuestTab guestTab           = new GuestTab();
        BookingTab bookingTab       = new BookingTab();
        CheckoutTab checkoutTab     = new CheckoutTab();

        tabs.getTabs().add(dashboardTab);
        tabs.getTabs().add(roomTab);
        tabs.getTabs().add(guestTab);
        tabs.getTabs().add(bookingTab);
        tabs.getTabs().add(checkoutTab);

        tabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
    if (newTab instanceof DashboardTab dt)   dt.refresh();
    if (newTab instanceof BookingTab bt)     bt.refreshDropdowns();
    if (newTab instanceof CheckoutTab ct)    ct.refreshTable();
});

        stage.setOnCloseRequest(e -> saveData());

        VBox root = new VBox(tabs);
        stage.setTitle("StaySync");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    private void loadData() {
        DataStore store = DataStore.getInstance();
        List<Room> rooms       = PersistenceManager.load("data/rooms.dat");
        List<Guest> guests     = PersistenceManager.load("data/guests.dat");
        List<Booking> bookings = PersistenceManager.load("data/bookings.dat");
        rooms.forEach(store::addRoom);
        guests.forEach(store::addGuest);
        bookings.forEach(store::addBooking);
    }

    private void saveData() {
        DataStore store = DataStore.getInstance();
        PersistenceManager.save(store.getRooms(),    "data/rooms.dat");
        PersistenceManager.save(store.getGuests(),   "data/guests.dat");
        PersistenceManager.save(store.getBookings(), "data/bookings.dat");
    }

    public static void main(String[] args) { launch(args); }
}