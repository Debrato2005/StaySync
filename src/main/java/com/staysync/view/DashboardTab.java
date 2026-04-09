package com.staysync.view;

import com.staysync.util.DataStore;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardTab extends Tab {

    private final DataStore store = DataStore.getInstance();

    private final Label totalRoomsVal     = new Label();
    private final Label availableRoomsVal = new Label();
    private final Label occupiedRoomsVal  = new Label();
    private final Label totalGuestsVal    = new Label();
    private final Label activeBookingsVal = new Label();
    private final Label totalRevenueVal   = new Label();
    private final Label occupancyRateVal  = new Label();

    public DashboardTab() {
        setText("Dashboard");
        setClosable(false);
        setContent(buildLayout());

    }

    private VBox buildLayout() {
        Label heading = new Label("🏨  StaySync Dashboard");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Live hotel overview");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        HBox row1 = new HBox(20,
                createCard("Total Rooms",     totalRoomsVal,     "#2980b9", "#eaf4fb"),
                createCard("Available",       availableRoomsVal, "#27ae60", "#eafaf1"),
                createCard("Occupied",        occupiedRoomsVal,  "#e74c3c", "#fdf2f2")
        );

        HBox row2 = new HBox(20,
                createCard("Total Guests",    totalGuestsVal,    "#8e44ad", "#f5eef8"),
                createCard("Active Bookings", activeBookingsVal, "#e67e22", "#fef9e7"),
                createCard("Total Revenue",   totalRevenueVal,   "#16a085", "#e8f8f5")
        );

        HBox row3 = new HBox(20,
                createCard("Occupancy Rate",  occupancyRateVal,  "#2c3e50", "#f0f3f4")
        );

        row1.setPadding(new Insets(0));
        row2.setPadding(new Insets(0));
        row3.setPadding(new Insets(0));

        VBox layout = new VBox(20, heading, subtitle, row1, row2, row3);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #ffffff;");
        return layout;
    }

    private VBox createCard(String title, Label valueLabel,
                             String accentColor, String bgColor) {
        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        titleLabel.setStyle("-fx-text-fill: " + accentColor + ";");

        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label bar = new Label();
        bar.setMinHeight(4);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 2;");

        VBox card = new VBox(8, bar, titleLabel, valueLabel);
        card.setPadding(new Insets(16));
        card.setMinWidth(190);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: " + accentColor + "33;" +
            "-fx-border-width: 1.5;"
        );
        return card;
    }

    public void refresh() {
        long total     = store.getRooms().size();
        long available = store.getRooms().stream().filter(r -> r.isAvailable()).count();
        long occupied  = total - available;
        long guests    = store.getGuests().size();
        long active    = store.getBookings().stream().filter(b -> b.isActive()).count();
        double revenue = store.getBookings().stream()
                .filter(b -> !b.isActive())
                .mapToDouble(b -> b.calculateTotal())
                .sum();
        double occupancyRate = (total == 0) ? 0 : (occupied * 100.0 / total);

        totalRoomsVal.setText(String.valueOf(total));
        availableRoomsVal.setText(String.valueOf(available));
        occupiedRoomsVal.setText(String.valueOf(occupied));
        totalGuestsVal.setText(String.valueOf(guests));
        activeBookingsVal.setText(String.valueOf(active));
        totalRevenueVal.setText("₹" + String.format("%.2f", revenue));
        occupancyRateVal.setText(String.format("%.1f%%", occupancyRate));
    }
}