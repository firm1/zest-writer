package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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

        optEditorFont.setValue("Arial");
        optEditorFontSize.setValue(10);
    }

    private void setDisplayOptions(){
        optDisplayTheme.getItems().add("Standard");
        optDisplayTheme.setValue("Standard");
    }

    private void setShortcutOptions(){

    }

    private void setAuthentificationOptions(){

    }

    private void setAdvancedOptions(){
        optAdvancedProtocol.getItems().addAll("https", "http");
        optAdvancedProtocol.setValue(config.getProtocol());
        optAdvancedHost.setText(config.getHost());
        optAdvancedPort.setText(config.getPort());
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
