package com.zestedesavoir.zestwriter.view;

import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

class ZRow {
    private SimpleListProperty<String> row = new SimpleListProperty<>();

    public ZRow() {
        super();
    }

    public ZRow(int n) {
        super();
        List<String> lst = new ArrayList<>();
        for(int i=0; i<n; i++) {
            lst.add(" - ");
        }
        ObservableList<String> observableList = FXCollections.observableArrayList(lst);
        this.row = new SimpleListProperty<>(observableList);

    }

    public ZRow(ObservableList<String> row) {
        super();
        this.row.set(row);
    }

    public List<String> getRow() {
        return row.get();
    }

    public void setRow(ObservableList<String> row) {
        this.row.set(row);
    }

}
public class TableController {
    private final ObservableList<ZRow> datas = FXCollections.observableArrayList(new ZRow(1));

    @FXML private TableView<ZRow> tableView;

    @FXML private void initialize() {
        tableView.setItems(datas);

        addCol();
        addRow();
    }

    @FXML private void HandleAddRowButtonAction(ActionEvent event) {
        addRow();
    }

    @FXML private void HandleAddColumnButtonAction(ActionEvent event) {
        addCol();
    }

    private void addCol() {
        TableColumn tc = new TableColumn();
        tc.setEditable(true);
        tc.setCellValueFactory(new Callback<CellDataFeatures<ZRow, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<ZRow, String> param) {
                return new SimpleStringProperty(param.getValue().getRow().get(0));
            }
        });

        tc.setCellFactory(TextFieldTableCell.forTableColumn());
        tc.setOnEditCommit(t -> {
            CellEditEvent<ZRow, String> evt = (CellEditEvent<ZRow, String>) t;
            List<String> row = evt.getTableView().getItems().get(evt.getTablePosition().getRow()).getRow();
            row.set(evt.getTablePosition().getColumn(), evt.getNewValue());
        });
        tc.setPrefWidth(150);
        TextField txf = new TextField();
        txf.setPrefWidth(150);
        txf.setPromptText(Configuration.getBundle().getString("ui.dialog.table_editor.colon") +
                (tableView.getColumns().size()+1));
        tc.setGraphic(txf);
        tableView.getColumns().addAll(tc);
        postAddColumn();
    }

    private void addRow() {
        ZRow row = new ZRow(tableView.getColumns().size());
        datas.add(row);
    }

    private void postAddColumn() {

        for (ZRow data : datas) {
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                if(data.getRow() != null) {
                    if (data.getRow().size() <= i) {
                        data.getRow().add(i, " - ");
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(ZRow data:datas) {
            sb.append(data.getRow()).append("\n");
        }
        return sb.toString();
    }
}
