package com.zestedesavoir.zestwriter.view.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class BaseDialog<T> extends Dialog<T>{

	GridPane gridPane = new GridPane();

	public BaseDialog(String title) {
		this.setTitle(title);
		this.setHeaderText(null);

		// Create the username and password labels and fields.

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
