package com.zds.zw.view.dialogs;

import com.zds.zw.utils.Configuration;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DiffDisplayDialog extends BaseDialog<Boolean>{
    String newContent;
    String oldContent;
    String titleContent;
    String titleExtract;
    File file;

    public DiffDisplayDialog(File file, String newContent, String titleContent, String titleExtract) {
		super(Configuration.getBundle().getString("ui.dialog.download.compare.window.title"), Configuration.getBundle().getString("ui.dialog.download.compare.window.header"));
		this.file = file;
		this.newContent = newContent;
		this.titleContent = titleContent;
        this.titleExtract = titleExtract;
        try {
            if(this.file.exists()) {
                this.oldContent = IOUtils.toString(new FileInputStream(this.file), "UTF-8");
            }
        } catch (IOException e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error(e.getMessage(), e);
        }

        // Set the button types.
	    ButtonType validButtonType = new ButtonType(Configuration.getBundle().getString("ui.dialog.download.compare.button.confirm"), ButtonData.OK_DONE);
	    this.getDialogPane().getButtonTypes().addAll(validButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20, 10, 10));
        Label l01 = new Label(Configuration.getBundle().getString("ui.dialog.download.compare.content_name") + " : "+titleContent);
        Label l02 = new Label(Configuration.getBundle().getString("ui.dialog.download.compare.extract_name") + " : "+titleExtract);
		Label l1 = new Label(Configuration.getBundle().getString("ui.dialog.download.compare.old_content"));
        Label l2 = new Label(Configuration.getBundle().getString("ui.dialog.download.compare.new_content"));
		TextArea textArea1 = new TextArea();
		textArea1.setEditable(false);
        textArea1.setWrapText(true);
        textArea1.setPrefHeight(500);
        textArea1.setText(oldContent);
        ScrollPane scrollPane1 = new ScrollPane();
        scrollPane1.setContent(textArea1);
        scrollPane1.setFitToWidth(true);
		TextArea textArea2 = new TextArea();
        textArea2.setEditable(false);
        textArea2.setWrapText(true);
        textArea2.setPrefHeight(500);
        textArea2.setText(newContent);
        ScrollPane scrollPane2 = new ScrollPane();
        scrollPane2.setContent(textArea2);
        scrollPane2.setFitToWidth(true);


        grid.add(l01, 0, 0,2, 1);
        grid.add(l02, 0, 1, 2, 1);
		grid.add(l1, 0, 2);
	    grid.add(l2, 1, 2);
        grid.add(scrollPane1, 0, 3);
        grid.add(scrollPane2, 1, 3);

	    // Enable/Disable login button depending on whether a username was entered.
		this.getDialogPane().lookupButton(validButtonType);

		this.getDialogPane().setContent(grid);

		Platform.runLater(textArea1::requestFocus);

		this.setResultConverter(dialogButton -> {
			if(dialogButton == validButtonType) {
                return true;
			}
			return false;
		});
	}
}
