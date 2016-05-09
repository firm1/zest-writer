package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.License;
import com.zestedesavoir.zestwriter.model.TypeContent;
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
        new License("CC BY", "Licence CC BY"),
        new License("CC BY-SA", "Licence CC BY-SA"),
        new License("CC BY-ND", "Licence CC BY-ND"),
        new License("CC BY-NC", "Licence CC BY-NC"),
        new License("CC BY-NC-SA", "Licence CC BY-NC-SA"),
        new License("CC BY-NC-ND", "Licence CC BY-NC-ND"),
        new License("Tous droits réservés", "Tout droits réservés"),
        new License("CC 0", "Licence CC 0")
    );

	public EditContentDialog(Content defaultContent) {
		super("Nouveau contenu", "Créez un nouveau contenus pour ZdS");

		// Set the icon (must be included in the project).
	    this.setGraphic(IconFactory.createAddFolderIcon());

	    // Set the button types.
	    ButtonType validButtonType = new ButtonType("Enregistrer", ButtonData.OK_DONE);
	    this.getDialogPane().getButtonTypes().addAll(validButtonType, ButtonType.CANCEL);

	    // Create the username and password labels and fields.
	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(20, 150, 10, 10));

	    TextField title = new TextField(defaultContent.getTitle());
	    TextField subtitle = new TextField(defaultContent.getDescription());
	    ComboBox<TypeContent> type = new ComboBox<>(typeOptions);
	    type.setValue((TypeContent) typeOptions.get(typeOptions.indexOf(new TypeContent(defaultContent.getType(), ""))));

	    ComboBox<License> license = new ComboBox<>(licOptions);
	    license.setValue((License) licOptions.get(licOptions.indexOf(new License(defaultContent.getLicence(), ""))));

	    grid.add(new Label("Titre du contenu :"), 0, 0);
	    grid.add(title, 1, 0);
	    grid.add(new Label("Description du contenu:"), 0, 1);
	    grid.add(subtitle, 1, 1);
	    grid.add(new Label("Type de contenu:"), 0, 2);
	    grid.add(type, 1, 2);
	    grid.add(new Label("Licence du contenu:"), 0, 3);
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
