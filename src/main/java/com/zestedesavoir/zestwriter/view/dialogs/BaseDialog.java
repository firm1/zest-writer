package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class BaseDialog<T> extends Dialog<T>{
	private GridPane gridPane = new GridPane();

	public BaseDialog(String title) {
		this.setTitle(title);
		this.setHeaderText(null);
		Stage stage= (Stage)this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("assets/static/icons/logo.png")));

		gridPane.setHgap(20);
		gridPane.setVgap(20);
		gridPane.setPadding(new Insets(20, 10, 10, 10));
	}

	public BaseDialog(String title, String header) {
		this(title);
		this.setHeaderText(header);
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void setGridPane(GridPane gridPane) {
		this.gridPane = gridPane;
	}
}
