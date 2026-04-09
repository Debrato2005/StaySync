package com.staysync.view;

import com.staysync.controller.RoomController;
import com.staysync.model.Room;
import com.staysync.model.RoomType;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RoomTab extends Tab {

    private final RoomController controller = new RoomController();
    private TableView<Room> table;
    private Spinner<Integer> roomNumSpinner;
    private ComboBox<RoomType> typeBox;
    private TextField priceField;

    public RoomTab() {
        setText("Rooms");
        setClosable(false);
        setContent(buildLayout());
    }

    private VBox buildLayout() {
        roomNumSpinner = new Spinner<>(100, 999, 101);
        roomNumSpinner.setEditable(true);
        roomNumSpinner.setPrefWidth(200);

        typeBox = new ComboBox<>();
        typeBox.getItems().addAll(RoomType.values());
        typeBox.setPromptText("Select Room Type");
        typeBox.setPrefWidth(200);

        priceField = new TextField();
        priceField.setPromptText("Price per Night");
        priceField.setPrefWidth(200);
        priceField.setEditable(true); // fix: allow manual price override

        typeBox.setOnAction(e -> {
            RoomType selected = typeBox.getValue();
            if (selected != null) priceField.setText(String.valueOf(selected.getBasePrice()));
        });

        Button addBtn     = new Button("Add Room");
        Button viewAllBtn = new Button("View All");
        Button availBtn   = new Button("Show Available");
        // fix: edit room button
        Button editBtn    = new Button("Edit Price");

        addBtn.setOnAction(e -> {
            if (typeBox.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Select a room type.").showAndWait();
                return;
            }
            boolean ok = controller.addRoom(
                    String.valueOf(roomNumSpinner.getValue()),
                    typeBox.getValue(),
                    priceField.getText()
            );
            if (ok) {
                refreshTable();
                typeBox.setValue(null);
                priceField.clear();
            }
        });

        viewAllBtn.setOnAction(e -> table.setItems(controller.getAllRooms()));
        availBtn.setOnAction(e -> table.setItems(controller.getAvailableRooms()));

        // fix: edit selected room's price
        editBtn.setOnAction(e -> {
            Room selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.ERROR, "Select a room to edit.").showAndWait();
                return;
            }
            TextInputDialog dialog = new TextInputDialog(
                    String.valueOf(selected.getPricePerNight()));
            dialog.setTitle("Edit Room Price");
            dialog.setHeaderText("Room " + selected.getRoomNumber());
            dialog.setContentText("New price per night:");
            dialog.showAndWait().ifPresent(input -> {
                boolean ok = controller.updateRoomPrice(selected.getRoomNumber(), input);
                if (ok) refreshTable();
            });
        });

        ColumnConstraints col1 = new ColumnConstraints(100);
        ColumnConstraints col2 = new ColumnConstraints(200);

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(10));
        form.getColumnConstraints().addAll(col1, col2);
        form.addRow(0, new Label("Room No:"),     roomNumSpinner);
        form.addRow(1, new Label("Type:"),        typeBox);
        form.addRow(2, new Label("Price/Night:"), priceField);

        HBox buttons = new HBox(10, addBtn, editBtn, viewAllBtn, availBtn);
        buttons.setPadding(new Insets(10));

        table = buildTable();
        refreshTable();

        VBox layout = new VBox(10, form, buttons, table);
        layout.setPadding(new Insets(15));
        return layout;
    }

    private TableView<Room> buildTable() {
        TableView<Room> tv = new TableView<>();

        TableColumn<Room, Integer> numCol = new TableColumn<>("Room No");
        numCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<Room, Double> priceCol = new TableColumn<>("Price/Night");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));

        TableColumn<Room, Boolean> availCol = new TableColumn<>("Available");
        availCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        tv.getColumns().addAll(numCol, typeCol, priceCol, availCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    public void refreshTable() {
        table.setItems(controller.getAllRooms());
    }
}