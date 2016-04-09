package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class OptionsDialog{
    private MainApp mainApp;
    private Stage optionsWindow;

    @FXML private Hyperlink optionGeneral;
    @FXML private Hyperlink optionEditor;
    @FXML private Hyperlink optionDisplay;
    @FXML private Hyperlink optionColorFont;
    @FXML private Hyperlink optionShortcut;
    @FXML private Hyperlink optionShortcutEditor;
    @FXML private Hyperlink optionAuthentification;

    @FXML private AnchorPane optionGeneralPane;
    @FXML private AnchorPane optionEditorPane;
    @FXML private AnchorPane optionDisplayPane;
    @FXML private AnchorPane optionColorFontPane;
    @FXML private AnchorPane optionShortcutPane;
    @FXML private AnchorPane optionShortcutEditorPane;
    @FXML private AnchorPane optionAuthentificationPane;

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    public void setWindow(Stage window){
        this.optionsWindow = window;
    }

    @FXML private void initialize(){
        hideAllPane();
        optionGeneralPane.setVisible(true);
    }

    @FXML private void HandleSaveButtonAction(){
        optionsWindow.close();
    }

    @FXML private void HandleCancelButtonAction(){
        optionsWindow.close();
    }


    @FXML private void HandleHyperlinkGeneralLabel(){
        hideAllPane();
        optionGeneralPane.setVisible(true);

        resetHyperlinkColor();
        optionGeneral.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkEditorLabel(){
        hideAllPane();
        optionEditorPane.setVisible(true);

        resetHyperlinkColor();
        optionEditor.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkDisplayLabel(){
        hideAllPane();
        optionDisplayPane.setVisible(true);

        resetHyperlinkColor();
        optionDisplay.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkColorFontLabel(){
        hideAllPane();
        optionColorFontPane.setVisible(true);

        resetHyperlinkColor();
        optionColorFont.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkShortcutLabel(){
        hideAllPane();
        optionShortcutPane.setVisible(true);

        resetHyperlinkColor();
        optionShortcut.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkShortcutEditorLabel(){
        hideAllPane();
        optionShortcutEditorPane.setVisible(true);

        resetHyperlinkColor();
        optionShortcutEditor.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkAuthentificationLabel(){
        hideAllPane();
        optionAuthentificationPane.setVisible(true);

        resetHyperlinkColor();
        optionAuthentification.setTextFill(Color.BLACK);
    }


    private void resetHyperlinkColor(){
        optionGeneral.setTextFill(Color.web("#656565"));
        optionEditor.setTextFill(Color.web("#656565"));
        optionDisplay.setTextFill(Color.web("#656565"));
        optionColorFont.setTextFill(Color.web("#656565"));
        optionShortcut.setTextFill(Color.web("#656565"));
        optionShortcutEditor.setTextFill(Color.web("#656565"));
        optionAuthentification.setTextFill(Color.web("#656565"));
    }

    private void hideAllPane(){
        optionGeneralPane.setVisible(false);
        optionEditorPane.setVisible(false);
        optionDisplayPane.setVisible(false);
        optionColorFontPane.setVisible(false);
        optionShortcutPane.setVisible(false);
        optionShortcutEditorPane.setVisible(false);
        optionAuthentificationPane.setVisible(false);
    }
}
