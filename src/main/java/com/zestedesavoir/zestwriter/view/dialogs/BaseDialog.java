package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.view.com.CustomDialog;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BaseDialog<T> extends CustomDialog<T> {
	private GridPane gridPane = new GridPane();

	public BaseDialog(MainApp mainApp, String title) {
		super(mainApp.getPrimaryStage());
		this.setTitle(title);
		this.setHeaderText(null);
		Stage stage= (Stage)this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("assets/static/icons/logo.png")));

		gridPane.setHgap(20);
		gridPane.setVgap(20);
		gridPane.setPadding(new Insets(20, 10, 10, 10));
	}

	public BaseDialog(MainApp mainApp, String title, String header) {
		this(mainApp, title);
		this.setHeaderText(header);
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void setGridPane(GridPane gridPane) {
		this.gridPane = gridPane;
	}
}
