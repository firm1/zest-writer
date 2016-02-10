package com.zestedesavoir.zestwriter.view;

import java.util.List;

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

public class TableController {
	private MdConvertController editorController;
	@FXML
	private TableView tableView;
	private ObservableList<ObservableList> datas = FXCollections.observableArrayList();

	public void setEditor(MdConvertController editorController) {
		this.editorController = editorController;
	}

	@FXML
	private void initialize() {

		addCol();

		addRow();

		tableView.setItems(datas);
	}

	@FXML
	private void HandleAddRowButtonAction(ActionEvent event) {
		addRow();
	}

	@FXML
	private void HandleAddColumnButtonAction(ActionEvent event) {
		addCol();
	}

	private void addCol() {
		TableColumn tc = new TableColumn();
		tc.setEditable(true);
		tc.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
				return new SimpleStringProperty(param.getValue().get(0).toString());
			}
		});
		tc.setCellFactory(TextFieldTableCell.forTableColumn());
		tc.setOnEditCommit(new EventHandler<CellEditEvent<ObservableList, String>>() {
			@Override
			public void handle(CellEditEvent<ObservableList, String> t) {

				List row = (ObservableList) t.getTableView().getItems().get(t.getTablePosition().getRow());
				row.set(t.getTablePosition().getColumn(), t.getNewValue());
			}
		});
		tc.setPrefWidth(100);
		TextField txf = new TextField();
		txf.setPrefWidth(100);
		txf.setPromptText("Colonne 1");
		tc.setGraphic(txf);
		tableView.getColumns().addAll(tc);
		postAddColumn();
	}

	private void addRow() {
		ObservableList<String> row = FXCollections.observableArrayList();
		for (Object header : tableView.getColumns()) {
			row.add("-");
		}
		datas.add(row);
	}

	private void postAddColumn() {

		for (ObservableList data : datas) {
			for (int i = 0; i < tableView.getColumns().size(); i++) {
				if (data.size() <= i) {
					data.add(i, "-");
				}
			}
		}
	}
}
