package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.view.com.IconFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.FontSelectorDialog;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class OptionsDialog{
    private MainApp mainApp;
    private Stage optionsWindow;
    private Configuration config;

    private String optEditorFont;
    private double optEditorFontSize;
    private String optEditorToolbarView;

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

    @FXML private RadioButton optEditorToolbarViewYes;
    @FXML private RadioButton optEditorToolbarViewNo;
    @FXML private Button optEditorFontButton;
    @FXML private ComboBox<String> optDisplayTheme;
    @FXML private RadioButton optDisplayWindowMaximizeYes;
    @FXML private RadioButton optDisplayWindowMaximizeNo;
    @FXML private RadioButton optDisplayWindowDimensionYes;
    @FXML private RadioButton optDisplayWindowDimensionNo;
    @FXML private RadioButton optDisplayWindowPositionYes;
    @FXML private RadioButton optDisplayWindowPositionNo;
    @FXML private TextField optAuthentificationUsername;
    @FXML private TextField optAuthentificationPassword;
    @FXML private ComboBox<String> optAdvancedProtocol;
    @FXML private TextField optAdvancedHost;
    @FXML private TextField optAdvancedPort;


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
        config.setEditorFont(optEditorFont);
        config.setEditorFontSize(String.valueOf(optEditorFontSize));
        config.setEditorToolbarView(optEditorToolbarView);

        config.setDisplayTheme(optDisplayTheme.getValue());

        if(optDisplayWindowMaximizeYes.isSelected())
            config.setDisplayWindowMaximize("true");
        else
            config.setDisplayWindowMaximize("false");

        if(optDisplayWindowDimensionYes.isSelected())
            config.setDisplayWindowStandardDimension("true");
        else
            config.setDisplayWindowStandardDimension("false");

        if(optDisplayWindowPositionYes.isSelected())
            config.setDisplayWindowPersonnalPosition("true");
        else
            config.setDisplayWindowPersonnalPosition("false");

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
        IconFactory.addAlertLogo(alert);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){
            if(result.get() == ButtonType.OK){
                optionsWindow.close();
            }
        }
    }

    @FXML private void HandleResetButtonAction(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la réinitialisation");
        alert.setHeaderText("Réinitialisation");
        alert.setContentText("Attention, une réinitialisation est irréversible, souhaitez-vous vraiment réinitialiser vos options ?");
        alert.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES), new ButtonType("Non", ButtonBar.ButtonData.NO));
        IconFactory.addAlertLogo(alert);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){
            if(result.get().getButtonData() == ButtonBar.ButtonData.YES){
                resetOptions();
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

    @FXML private void HandleGeneralBrowseAction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Espace de travail");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File directory = directoryChooser.showDialog(null);

        if(directory != null && directory.exists()){
            config.setWorkspacePath(directory.getAbsolutePath());
        }
    }

    @FXML private void HandleGeneralShowAction(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        IconFactory.addAlertLogo(alert);
        alert.setTitle("Chemin de votre espace de travail");
        alert.setHeaderText("Chemin de votre espace de travail");
        alert.setContentText(config.getWorkspacePath());
        alert.showAndWait();
    }

    @FXML private void HandleEditorFontChoice(){
        Dialog<Font> fontSelector = new FontSelectorDialog(new Font(config.getEditorFont(), config.getEditorFontsize()));
        Optional<Font> result = fontSelector.showAndWait();

        if(result.isPresent()){
            Font newFont = result.get();

            optEditorFont = newFont.getName();
            optEditorFontSize = newFont.getSize();
            optEditorFontButton.setText(optEditorFont + " - " + optEditorFontSize);
        }
    }

    @FXML private void HandleDisplayWindowMaximizeYes(){
        optDisplayWindowDimensionYes.setDisable(true);
        optDisplayWindowDimensionNo.setDisable(true);
        optDisplayWindowPositionYes.setDisable(true);
        optDisplayWindowPositionNo.setDisable(true);
    }

    @FXML private void HandleDisplayWindowMaximizeNo(){
        optDisplayWindowDimensionYes.setDisable(false);
        optDisplayWindowDimensionNo.setDisable(false);
        optDisplayWindowPositionYes.setDisable(false);
        optDisplayWindowPositionNo.setDisable(false);
    }

    @FXML private void HandleEditorToolbarViewYes(){
        optEditorToolbarView = "yes";
    }

    @FXML private void HandleEditorToolbarViewNo(){
        optEditorToolbarView = "no";
    }

    private void setGeneralOptions(){
    }

    private void setEditorOptions(){
        optEditorFontButton.setText(config.getEditorFont() + " - " + config.getEditorFontsize());

        optEditorFont = config.getEditorFont();
        optEditorFontSize = config.getEditorFontsize();
        optEditorToolbarView = config.getEditorToolbarView();

        if(optEditorToolbarView.toLowerCase().equals("no"))
            optEditorToolbarViewNo.setSelected(true);
        else
            optEditorToolbarViewYes.setSelected(true);
    }

    private void setDisplayOptions(){
        optDisplayTheme.getItems().add("Standard");
        optDisplayTheme.setValue(config.getDisplayTheme());

        if(config.isDisplayWindowMaximize())
            optDisplayWindowMaximizeYes.setSelected(true);
        else
            optDisplayWindowMaximizeNo.setSelected(true);

        if(config.isDisplayWindowPersonnalDimension())
            optDisplayWindowDimensionYes.setSelected(true);
        else
            optDisplayWindowDimensionNo.setSelected(true);

        if(config.isDisplayWindowPersonnalPosition())
            optDisplayWindowPositionYes.setSelected(true);
        else
            optDisplayWindowPositionNo.setSelected(true);


        if(config.isDisplayWindowMaximize()){
            optDisplayWindowDimensionYes.setDisable(true);
            optDisplayWindowDimensionNo.setDisable(true);
            optDisplayWindowPositionYes.setDisable(true);
            optDisplayWindowPositionNo.setDisable(true);
        }
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

    private void resetOptions(){
        config.resetAllOptions();

        setGeneralOptions();
        setEditorOptions();
        setDisplayOptions();
        setShortcutOptions();
        setAuthentificationOptions();
        setAdvancedOptions();
    }
}
