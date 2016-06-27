package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.License;
import com.zestedesavoir.zestwriter.model.TypeContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class EditContentDialog extends BaseDialog<Pair<String, Map<String, Object>>>{
	public static ObservableList<TypeContent> typeOptions = FXCollections.observableArrayList(new TypeContent("ARTICLE", "Article"), new TypeContent("TUTORIAL","Tutoriel"));
    public static ObservableList<License> licOptions = FXCollections.observableArrayList(
        new License("CC BY", Configuration.bundle.getString("ui.content.label.license.ccby")),
        new License("CC BY-SA", Configuration.bundle.getString("ui.content.label.license.ccbysa")),
        new License("CC BY-ND", Configuration.bundle.getString("ui.content.label.license.ccbynd")),
        new License("CC BY-NC", Configuration.bundle.getString("ui.content.label.license.ccbync")),
        new License("CC BY-NC-SA", Configuration.bundle.getString("ui.content.label.license.ccbyncsa")),
        new License("CC BY-NC-ND", Configuration.bundle.getString("ui.content.label.license.ccbyncnd")),
        new License("Tous droits réservés", Configuration.bundle.getString("ui.content.label.license.allright")),
        new License("CC 0", Configuration.bundle.getString("ui.content.label.license.cc0"))
    );

	public EditContentDialog(Content defaultContent) {
		super(Configuration.bundle.getString("ui.content.new.title"), Configuration.bundle.getString("ui.content.new.header"));

		// Set the icon (must be included in the project).
	    this.setGraphic(IconFactory.createAddFolderIcon());

	    // Set the button types.
	    ButtonType validButtonType = new ButtonType(Configuration.bundle.getString("ui.dialog.save"), ButtonData.OK_DONE);
	    this.getDialogPane().getButtonTypes().addAll(validButtonType, ButtonType.CANCEL);

	    // Create the username and password labels and fields.
	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(20, 150, 10, 10));

	    TextField title = new TextField(defaultContent.getTitle());
		title.setId("title");

	    TextField subtitle = new TextField(defaultContent.getDescription());
		subtitle.setId("subtitle");

	    ComboBox<TypeContent> type = new ComboBox<>(typeOptions);
	    type.setValue(typeOptions.get(typeOptions.indexOf(new TypeContent(defaultContent.getType(), ""))));

	    ComboBox<License> license = new ComboBox<>(licOptions);
	    license.setValue(licOptions.get(licOptions.indexOf(new License(defaultContent.getLicence(), ""))));

	    grid.add(new Label(Configuration.bundle.getString("ui.content.label.title")), 0, 0);
	    grid.add(title, 1, 0);
	    grid.add(new Label(Configuration.bundle.getString("ui.content.label.description")), 0, 1);
	    grid.add(subtitle, 1, 1);
	    grid.add(new Label(Configuration.bundle.getString("ui.content.label.type")), 0, 2);
	    grid.add(type, 1, 2);
	    grid.add(new Label(Configuration.bundle.getString("ui.content.label.license")), 0, 3);
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
	            return new Pair<>("res", map);
	        }
	        return null;
	    });
	}
}
