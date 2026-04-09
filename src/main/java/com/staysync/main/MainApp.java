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
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final String USERNAME = "staysync";
    private static final String PASSWORD = "240911734";

    @Override
    public void start(Stage stage) {
        if (!showLogin(stage)) return;
        loadData();

        TabPane tabs = new TabPane();
        DashboardTab dashboardTab = new DashboardTab();
        RoomTab      roomTab      = new RoomTab();
        GuestTab     guestTab     = new GuestTab();
        BookingTab   bookingTab   = new BookingTab();
        CheckoutTab  checkoutTab  = new CheckoutTab();

        tabs.getTabs().addAll(dashboardTab, roomTab, guestTab, bookingTab, checkoutTab);
        dashboardTab.refresh(); // fix: refresh after data is loaded

        tabs.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n instanceof DashboardTab dt) dt.refresh();
            if (n instanceof BookingTab bt)   bt.refreshDropdowns();
            if (n instanceof CheckoutTab ct)  ct.refreshTable();
        });

        MenuBar menuBar = buildMenuBar(stage);
        stage.setOnCloseRequest(e -> saveData());

        VBox root = new VBox(menuBar, tabs);
        stage.setTitle("StaySync — Hotel Management");
        stage.setScene(new Scene(root, 900, 620));
        stage.show();
    }

    /** Shows login dialog. Returns true if authenticated, false if cancelled. */
    private boolean showLogin(Stage owner) {
        Stage loginStage = new Stage();

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(260);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(260);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(260);
        loginBtn.setStyle(
            "-fx-background-color: #2980b9; -fx-text-fill: white;" +
            "-fx-font-size: 13; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        boolean[] authenticated = {false};

        Runnable doLogin = () -> {
            if (userField.getText().trim().equals(USERNAME)
                    && passField.getText().trim().equals(PASSWORD)) {
                authenticated[0] = true;
                loginStage.close();
            } else {
                errorLabel.setText("Invalid username or password.");
                passField.clear();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());

        Label title    = new Label("StaySync");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #2c3e50;");
        Label subtitle = new Label("Hotel Management System");
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        VBox card = new VBox(12, title, subtitle, new Separator(),
                new Label("Username"), userField,
                new Label("Password"), passField,
                errorLabel, loginBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(30));
        card.setMaxWidth(320);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 12;" +
            "-fx-border-radius: 12; -fx-border-color: #dce1e7; -fx-border-width: 1.5;"
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #eaf1fb;");
        root.setPadding(new Insets(40));

        loginStage.setTitle("StaySync — Login");
        loginStage.setScene(new Scene(root, 420, 440));
        loginStage.setResizable(false);
        // close app if login window is closed without logging in
        loginStage.setOnCloseRequest(e -> Platform.exit());
        loginStage.showAndWait();

        return authenticated[0];
    }

    private MenuBar buildMenuBar(Stage stage) {
        MenuItem saveItem = new MenuItem("Save Data");
        saveItem.setOnAction(e -> {
            saveData();
            new Alert(Alert.AlertType.INFORMATION, "Data saved successfully.").showAndWait();
        });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> { saveData(); Platform.exit(); });
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(saveItem, new SeparatorMenuItem(), exitItem);

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> {
            Alert about = new Alert(Alert.AlertType.INFORMATION,
                    "StaySync — Hotel Management System\nVersion 1.0\n\n" +
                    "Manages rooms, guests, bookings, and checkouts.");
            about.setTitle("About StaySync");
            about.setHeaderText(null);
            about.showAndWait();
        });
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(fileMenu, helpMenu);
    }

    private void loadData() {
        DataStore store = DataStore.getInstance();
        List<Room>    rooms    = PersistenceManager.load("rooms.dat");
        List<Guest>   guests   = PersistenceManager.load("guests.dat");
        List<Booking> bookings = PersistenceManager.load("bookings.dat");
        rooms.forEach(store::addRoom);
        guests.forEach(store::addGuest);
        bookings.forEach(store::addBooking);
        store.recomputeAvailability();
    }

    private void saveData() {
        DataStore store = DataStore.getInstance();
        PersistenceManager.save(store.getRooms(),    "rooms.dat");
        PersistenceManager.save(store.getGuests(),   "guests.dat");
        PersistenceManager.save(store.getBookings(), "bookings.dat");
    }

    public static void main(String[] args) { launch(args); }
}