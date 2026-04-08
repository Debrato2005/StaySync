package com.staysync.view;

import com.staysync.controller.GuestController;
import com.staysync.model.Guest;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuestTab extends Tab {

    private final GuestController controller = new GuestController();
    private TableView<Guest> table;

    public GuestTab() {
        setText("Guests");
        setClosable(false);
        setContent(buildLayout());
    }

    private VBox buildLayout() {
        TextField nameField = new TextField();
        nameField.setPromptText("Guest Name");
        nameField.setPrefWidth(200);
        nameField.setEditable(true);

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Number");
        contactField.setPrefWidth(200);
        contactField.setEditable(true);

        TextField idField = new TextField();
        idField.setPromptText("Aadhaar / PAN");
        idField.setPrefWidth(200);
        idField.setEditable(true);

        Button addBtn  = new Button("Register Guest");
        Button viewBtn = new Button("View All");

        addBtn.setOnAction(e -> {
            boolean ok = controller.addGuest(
                    nameField.getText(),
                    contactField.getText(),
                    idField.getText()
            );
            if (ok) {
                refreshTable();
                nameField.clear();
                contactField.clear();
                idField.clear();
            }
        });

        viewBtn.setOnAction(e -> refreshTable());

        ColumnConstraints col1 = new ColumnConstraints(100);
        ColumnConstraints col2 = new ColumnConstraints(200);

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(10));
        form.getColumnConstraints().addAll(col1, col2);
        form.addRow(0, new Label("Name:"),     nameField);
        form.addRow(1, new Label("Contact:"),  contactField);
        form.addRow(2, new Label("ID Proof:"), idField);

        HBox buttons = new HBox(10, addBtn, viewBtn);
        buttons.setPadding(new Insets(10));

        table = buildTable();
        refreshTable();

        VBox layout = new VBox(10, form, buttons, table);
        layout.setPadding(new Insets(15));
        return layout;
    }

    private TableView<Guest> buildTable() {
        TableView<Guest> tv = new TableView<>();

        TableColumn<Guest, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("guestId"));

        TableColumn<Guest, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Guest, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        TableColumn<Guest, String> idProofCol = new TableColumn<>("ID Proof");
        idProofCol.setCellValueFactory(new PropertyValueFactory<>("idProof"));

        tv.getColumns().addAll(idCol, nameCol, contactCol, idProofCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    public void refreshTable() {
        table.setItems(controller.getAllGuests());
    }
}