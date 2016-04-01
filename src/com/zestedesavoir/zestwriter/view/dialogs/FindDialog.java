/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.view.dialogs;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


/**
 *
 * Get a Line of line/colon entry
 */
public class FindDialog extends Dialog<List<Integer>>{
    private final GridPane grid;
    private final Label label;
    private final TextField textField;
    private String searchedText;

    public FindDialog(String currentText){
        super();
        searchedText = currentText;
        final DialogPane dialogPane = getDialogPane();
        this.setTitle("Terme à rechercher");
        this.setHeaderText(null);
        this.setContentText("Recherchez");
        // -- textfield
        this.textField = new TextField("");
        this.textField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setFillWidth(textField, true);
        
        // -- label
        label = new Label(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());

        
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);
        
        dialogPane.contentTextProperty().addListener(o -> updateGrid());
        
        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        updateGrid();
        
        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? findWordsInFile() : null;
        });
        
    }
    public String getPatern(){
        return textField.getText();
    }
    private List<Integer> findWordsInFile(){
        int lineNumber = 1;
        List<Integer> results = new LinkedList<>();
        String pattern = textField.getText().toLowerCase();

        int col = searchedText.indexOf(pattern);
        while(col != -1){
            results.add(col);
            col = searchedText.indexOf(pattern, col + 1);
        }

        return results;
    }
    private void updateGrid() {
        grid.getChildren().clear();
        
        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);
        getDialogPane().setContent(grid);
        
        Platform.runLater(() -> textField.requestFocus());
    }
    
}
