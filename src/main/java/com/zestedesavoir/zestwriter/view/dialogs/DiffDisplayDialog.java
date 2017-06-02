package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Constant;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.License;
import com.zestedesavoir.zestwriter.model.TypeContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class DiffDisplayDialog extends BaseDialog<Boolean>{
    String newContent;
    String oldContent;
    String titleContent;
    String titleExtract;
    File file;

	public DiffDisplayDialog(File file, String newContent, String titleContent, String titleExtract) {
		super("Résolution de conflit", "L'extrait présent en ligne est différent de celui que vous avez en local, voulez-vous écraser l'extrait en local ?");
		this.file = file;
		this.newContent = titleContent;
		this.titleContent = titleExtract;
        try {
            this.oldContent = IOUtils.toString(new FileInputStream(this.file), "UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        ;

	    // Set the button types.
	    ButtonType validButtonType = new ButtonType("Confirmer", ButtonData.OK_DONE);
	    this.getDialogPane().getButtonTypes().addAll(validButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20, 10, 10));
        Label l01 = new Label("Contenu : "+titleContent);
        Label l02 = new Label("Extrait : "+titleExtract);
		Label l1 = new Label("Ancien Contenu");
        Label l2 = new Label("Nouveau Contenu");
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
