package com.zestedesavoir.zestwriter.view.dialogs;

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
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditContentDialog extends BaseDialog<Pair<String, Map<String, Object>>>{
	public static int MAX_TITLE_LENGTH = 80;
	public static int MAX_SUBTITLE_LENGTH = 200;

	public static ObservableList<TypeContent> typeOptions = FXCollections.observableArrayList(new TypeContent("ARTICLE", "Article"), new TypeContent("TUTORIAL","Tutoriel"));
    public static ObservableList<License> licOptions = FXCollections.observableArrayList(
        new License("CC BY", Configuration.getBundle().getString("ui.content.label.license.ccby")),
        new License("CC BY-SA", Configuration.getBundle().getString("ui.content.label.license.ccbysa")),
        new License("CC BY-ND", Configuration.getBundle().getString("ui.content.label.license.ccbynd")),
        new License("CC BY-NC", Configuration.getBundle().getString("ui.content.label.license.ccbync")),
        new License("CC BY-NC-SA", Configuration.getBundle().getString("ui.content.label.license.ccbyncsa")),
        new License("CC BY-NC-ND", Configuration.getBundle().getString("ui.content.label.license.ccbyncnd")),
        new License("Tous droits réservés", Configuration.getBundle().getString("ui.content.label.license.allright")),
        new License("CC 0", Configuration.getBundle().getString("ui.content.label.license.cc0"))
    );

	public EditContentDialog(Content defaultContent) {
		super(Configuration.getBundle().getString("ui.content.new.title"), Configuration.getBundle().getString("ui.content.new.header"));

		// Set the icon (must be included in the project).
		this.setGraphic(IconFactory.createAddFolderIcon());

	    // Set the button types.
	    ButtonType validButtonType = new ButtonType(Configuration.getBundle().getString("ui.dialog.save"), ButtonData.OK_DONE);
	    this.getDialogPane().getButtonTypes().addAll(validButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField title = new TextField(defaultContent.getTitle());
		title.textProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue.length() > MAX_TITLE_LENGTH) {
				title.setText(oldValue);
			}
		});
		TextArea subtitle = new TextArea(defaultContent.getDescription());
		subtitle.setWrapText(true);
		subtitle.setPrefHeight(80);
		subtitle.textProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.length() > MAX_SUBTITLE_LENGTH) {
                subtitle.setText(oldValue);
            }
        });
	    ComboBox<TypeContent> type = new ComboBox<>(typeOptions);
	    type.setValue(typeOptions.get(typeOptions.indexOf(new TypeContent(defaultContent.getType(), ""))));

	    ComboBox<License> license = new ComboBox<>(licOptions);
	    license.setValue(licOptions.get(licOptions.indexOf(new License(defaultContent.getLicence(), ""))));

	    grid.add(new Label(Configuration.getBundle().getString("ui.content.label.title")), 0, 0);
	    grid.add(title, 1, 0);
	    grid.add(new Label(Configuration.getBundle().getString("ui.content.label.description")), 0, 1);
	    grid.add(subtitle, 1, 1);
	    grid.add(new Label(Configuration.getBundle().getString("ui.content.label.type")), 0, 2);
	    grid.add(type, 1, 2);
	    grid.add(new Label(Configuration.getBundle().getString("ui.content.label.license")), 0, 3);
	    grid.add(license, 1, 3);

	    // Enable/Disable login button depending on whether a username was entered.
		this.getDialogPane().lookupButton(validButtonType);

		this.getDialogPane().setContent(grid);

		Platform.runLater(title::requestFocus);

		this.setResultConverter(dialogButton -> {
			if(dialogButton == validButtonType) {
				Map<String, Object> map = new HashMap<>();
				map.put("title",title.getText());
				map.put("description",subtitle.getText());
				map.put("type",type.getValue().getCode());
				map.put("licence",license.getValue().getCode());
				if(map.get("title").toString().isEmpty()) {
					CustomAlert alert = new CustomAlert(Alert.AlertType.ERROR);
					alert.setTitle(Configuration.getBundle().getString("ui.content.new.error.title.limit.title"));
					alert.setHeaderText(Configuration.getBundle().getString("ui.content.new.error.title.limit.header"));
					alert.setContentText(Configuration.getBundle().getString("ui.content.new.error.title.limit.text"));

					alert.showAndWait();
					return null;
				}

                if("".equals(getSlug(map.get("title").toString()))) {
                    CustomAlert alert = new CustomAlert(Alert.AlertType.ERROR);
                    alert.setTitle(Configuration.getBundle().getString("ui.content.new.error.title.slug.title"));
                    alert.setHeaderText(Configuration.getBundle().getString("ui.content.new.error.title.slug.header"));
                    alert.setContentText(Configuration.getBundle().getString("ui.content.new.error.title.slug.text"));

					alert.showAndWait();
					return null;
				}
				return new Pair<>("res", map);
			}
			return null;
		});
	}

	public static ObservableList<TypeContent> getTypeOptions() {
		return typeOptions;
	}

	public static ObservableList<License> getLicOptions() {
		return licOptions;
	}

	public static String getSlug(String input) {
		String nowhitespace = Constant.WHITESPACE.matcher(input).replaceAll("");
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
		String slug = Constant.NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}
}
