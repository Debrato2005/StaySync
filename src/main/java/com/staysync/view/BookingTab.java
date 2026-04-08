package com.staysync.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.staysync.controller.BookingController;
import com.staysync.model.Booking;
import com.staysync.model.Guest;
import com.staysync.model.Room;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class BookingTab extends Tab {

    private final BookingController controller = new BookingController();
    private TableView<Booking> table;
    private ComboBox<Guest> guestBox;
    private ComboBox<Room> roomBox;

    public BookingTab() {
        setText("Bookings");
        setClosable(false);
        setContent(buildLayout());
    }

    private VBox buildLayout() {
        guestBox = new ComboBox<>();
        guestBox.setPromptText("Select Guest");
        guestBox.setMaxWidth(Double.MAX_VALUE);

        roomBox = new ComboBox<>();
        roomBox.setPromptText("Select Available Room");
        roomBox.setMaxWidth(Double.MAX_VALUE);

        DatePicker checkInPicker  = new DatePicker();
        DatePicker checkOutPicker = new DatePicker();
        applyDateFormat(checkInPicker);
        applyDateFormat(checkOutPicker);

        checkInPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        checkOutPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        Button refreshFormBtn = new Button("Refresh Lists");
        Button bookBtn        = new Button("Book Room");

        refreshFormBtn.setOnAction(e -> refreshDropdowns());
        bookBtn.setOnAction(e -> {
            boolean ok = controller.createBooking(
                    guestBox.getValue(),
                    roomBox.getValue(),
                    checkInPicker.getValue(),
                    checkOutPicker.getValue()
            );
            if (ok) {
                refreshTable();
                refreshDropdowns();
                guestBox.setValue(null);
                roomBox.setValue(null);
                checkInPicker.setValue(null);
                checkOutPicker.setValue(null);
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(10));
        form.addRow(0, new Label("Guest:"),     guestBox);
        form.addRow(1, new Label("Room:"),      roomBox);
        form.addRow(2, new Label("Check-in:"),  checkInPicker);
        form.addRow(3, new Label("Check-out:"), checkOutPicker);

        HBox buttons = new HBox(10, refreshFormBtn, bookBtn);
        buttons.setPadding(new Insets(10));

        table = buildTable();
        refreshTable();
        refreshDropdowns();

        VBox layout = new VBox(10, form, buttons, table);
        layout.setPadding(new Insets(15));
        return layout;
    }

    private void applyDateFormat(DatePicker dp) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dp.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? fmt.format(date) : "";
            }
            @Override
            public LocalDate fromString(String s) {
                return (s != null && !s.isBlank()) ? LocalDate.parse(s, fmt) : null;
            }
        });
        dp.setPromptText("dd/MM/yyyy");
    }

    private TableView<Booking> buildTable() {
        TableView<Booking> tv = new TableView<>();

        TableColumn<Booking, Integer> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

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

        TableColumn<Booking, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));

        tv.getColumns().addAll(idCol, guestCol, roomCol, inCol, outCol, activeCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    public void refreshTable() {
        table.setItems(controller.getAllBookings());
    }

    public void refreshDropdowns() {
        guestBox.setItems(controller.getAllGuests());
        roomBox.setItems(controller.getAvailableRooms());
    }
}