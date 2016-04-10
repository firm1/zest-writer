package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class OptionsDialog{
    private MainApp mainApp;
    private Stage optionsWindow;
    private Configuration config;

    @FXML private Hyperlink optionGeneral;
    @FXML private Hyperlink optionEditor;
    @FXML private Hyperlink optionDisplay;
    @FXML private Hyperlink optionShortcut;
    @FXML private Hyperlink optionAuthentification;
    @FXML private Hyperlink optionAdvanced;

    @FXML private AnchorPane optionGeneralPane;
    @FXML private AnchorPane optionEditorPane;
    @FXML private AnchorPane optionDisplayPane;
    @FXML private AnchorPane optionShortcutPane;
    @FXML private AnchorPane optionAuthentificationPane;
    @FXML private AnchorPane optionAdvancedPane;

    @FXML private ComboBox<String> optEditorFont;
    @FXML private ComboBox<Integer> optEditorFontSize;
    @FXML private ComboBox<String> optDisplayTheme;
    @FXML private TextField optAuthentificationUsername;
    @FXML private TextField optAuthentificationPassword;
    @FXML private ComboBox<String> optAdvancedProtocol;
    @FXML private TextField optAdvancedHost;
    @FXML private TextField optAdvancedPort;

    public enum EditorFonts{
        Arial("Arial"),
        ComicSansMs("Comic Sans MS");

        protected String fontName;

        EditorFonts(String fontName){
            this.fontName = fontName;
        }
    }

    public enum EditorFontsSize{
        Size10(10),
        Size11(11),
        Size12(12),
        Size14(14),
        Size16(16),
        Size18(18);

        protected double fontSize;

        EditorFontsSize(double fontSize){
            this.fontSize = fontSize;
        }
    }

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        this.config = this.mainApp.getConfig();

        setGeneralOptions();
        setEditorOptions();
        setDisplayOptions();
        setShortcutOptions();
        setAuthentificationOptions();
        setAdvancedOptions();
    }

    public void setWindow(Stage window){
        this.optionsWindow = window;
    }

    @FXML private void initialize(){
        hideAllPane();
        optionGeneralPane.setVisible(true);
    }

    @FXML private void HandleSaveButtonAction(){
        config.setEditorFont(optEditorFont.getValue());
        config.setEditorFontSize(String.valueOf(optEditorFontSize.getValue()));

        config.setDisplayTheme(optDisplayTheme.getValue());

        config.setAuthentificationUsername(optAuthentificationUsername.getText());
        config.setAuthentificationPassword(optAuthentificationPassword.getText());

        config.setAdvancedServerProtocol(optAdvancedProtocol.getValue());
        config.setAdvancedServerHost(optAdvancedHost.getText());
        config.setAdvancedServerPort(optAdvancedPort.getText());

        config.saveConfFile();
        optionsWindow.close();
    }

    @FXML private void HandleCancelButtonAction(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer l'annulation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment annuler ? Les modifications apportés ne seront pas enregistré.");
        Stage stage= (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("static/icons/logo.png")));

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){
            if(result.get() == ButtonType.OK){
                optionsWindow.close();
            }
        }
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

    @FXML private void HandleHyperlinkShortcutLabel(){
        hideAllPane();
        optionShortcutPane.setVisible(true);

        resetHyperlinkColor();
        optionShortcut.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkAuthentificationLabel(){
        hideAllPane();
        optionAuthentificationPane.setVisible(true);

        resetHyperlinkColor();
        optionAuthentification.setTextFill(Color.BLACK);
    }

    @FXML private void HandleHyperlinkAdvancedLabel(){
        hideAllPane();
        optionAdvancedPane.setVisible(true);

        resetHyperlinkColor();
        optionAdvanced.setTextFill(Color.BLACK);
    }

    private void setGeneralOptions(){
    }

    private void setEditorOptions(){
        for(EditorFonts font : EditorFonts.values()){
            optEditorFont.getItems().add(font.fontName);
        }
        for(EditorFontsSize font : EditorFontsSize.values()){
            optEditorFontSize.getItems().add((int)font.fontSize);
        }

        optEditorFont.setValue(config.getEditorFont());
        optEditorFontSize.setValue((int)config.getEditorFontsize());
    }

    private void setDisplayOptions(){
        optDisplayTheme.getItems().add("Standard");
        optDisplayTheme.setValue(config.getDisplayTheme());
    }

    private void setShortcutOptions(){

    }

    private void setAuthentificationOptions(){
        optAuthentificationUsername.setText(config.getAuthentificationUsername());
        optAuthentificationPassword.setText(config.getAuthentificationPassword());
    }

    private void setAdvancedOptions(){
        optAdvancedProtocol.getItems().addAll("https", "http");
        optAdvancedProtocol.setValue(config.getAdvancedServerProtocol());
        optAdvancedHost.setText(config.getAdvancedServerHost());
        optAdvancedPort.setText(config.getAdvancedServerPort());
    }

    private void resetHyperlinkColor(){
        optionGeneral.setTextFill(Color.web("#656565"));
        optionEditor.setTextFill(Color.web("#656565"));
        optionDisplay.setTextFill(Color.web("#656565"));
        optionShortcut.setTextFill(Color.web("#656565"));
        optionAuthentification.setTextFill(Color.web("#656565"));
        optionAdvanced.setTextFill(Color.web("#656565"));
    }

    private void hideAllPane(){
        optionGeneralPane.setVisible(false);
        optionEditorPane.setVisible(false);
        optionDisplayPane.setVisible(false);
        optionShortcutPane.setVisible(false);
        optionAuthentificationPane.setVisible(false);
        optionAdvancedPane.setVisible(false);
    }
}