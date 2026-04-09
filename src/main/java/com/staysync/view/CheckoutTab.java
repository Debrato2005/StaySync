package com.staysync.view;

import com.staysync.controller.CheckoutController;
import com.staysync.model.Booking;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CheckoutTab extends Tab {

    private final CheckoutController controller = new CheckoutController();
    private TableView<Booking> table;

    public CheckoutTab() {
        setText("Checkout");
        setClosable(false);
        setContent(buildLayout());
    }

    private VBox buildLayout() {
        Button refreshBtn  = new Button("Refresh");
        Button checkoutBtn = new Button("Checkout Selected");

        // fix: checkbox to optionally remove guest on checkout
        CheckBox removeGuestBox = new CheckBox("Remove guest on checkout");

        refreshBtn.setOnAction(e -> refreshTable());

        checkoutBtn.setOnAction(e -> {
            Booking selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.ERROR, "Select a booking.").showAndWait();
                return;
            }

            // fix: confirmation dialog before checkout
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Checkout " + selected.getGuest().getName() + " from Room "
                    + selected.getRoom().getRoomNumber() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Checkout");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    controller.checkout(selected, removeGuestBox.isSelected(), total -> {
                        refreshTable();
                        showBill(selected, total);
                    });
                }
            });
        });

        HBox buttons = new HBox(10, refreshBtn, checkoutBtn, removeGuestBox);
        buttons.setPadding(new Insets(10));

        table = buildTable();
        refreshTable();

        VBox layout = new VBox(10, buttons, table);
        layout.setPadding(new Insets(15));
        return layout;
    }

    private TableView<Booking> buildTable() {
        TableView<Booking> tv = new TableView<>();

        TableColumn<Booking, Integer> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("bookingId"));

        TableColumn<Booking, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getGuest().getName()));

        TableColumn<Booking, String> roomCol = new TableColumn<>("Room No");
        roomCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getRoom().getRoomNumber())));

        TableColumn<Booking, String> inCol = new TableColumn<>("Check-in");
        inCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCheckIn().toString()));

        TableColumn<Booking, String> outCol = new TableColumn<>("Check-out");
        outCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCheckOut().toString()));

        tv.getColumns().addAll(idCol, guestCol, roomCol, inCol, outCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    private void showBill(Booking b, double total) {
        long nights = b.getNights();
        double effectiveRate = total / Math.max(1, nights);

        String msg = "Guest:       " + b.getGuest().getName()     + "\n" +
                     "Room:        " + b.getRoom().getRoomNumber() + "\n" +
                     "Type:        " + b.getRoom().getRoomType()   + "\n" +
                     "Check-in:    " + b.getCheckIn()              + "\n" +
                     "Check-out:   " + b.getCheckOut()             + "\n" +
                     "Nights:      " + nights                      + "\n" +
                     "Rate/Night:  ₹" + String.format("%.2f", effectiveRate) + "\n" +
                     "─────────────────────────\n" +
                     "Total Bill:  ₹" + String.format("%.2f", total);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setTitle("Checkout Bill");
        alert.setHeaderText("✅ Checkout Successful");
        alert.showAndWait();
    }

    public void refreshTable() {
        table.setItems(controller.getActiveBookings());
    }
}