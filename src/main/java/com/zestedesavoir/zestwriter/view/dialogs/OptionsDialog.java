package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Lang;
import com.zestedesavoir.zestwriter.utils.Theme;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;

import java.io.File;
import java.util.Optional;

public class OptionsDialog{
    private MainApp mainApp;
    private Stage optionsWindow;
    private Configuration config;

    private String optEditorFont;
    private double optEditorFontSize;
    private boolean optEditorToolbarView;
    private boolean optEditorLinenoView;
    private boolean optEditorRenderView;
    private boolean optSmartEditor;

    @FXML private RadioButton optEditorToolbarViewYes;
    @FXML private RadioButton optEditorToolbarViewNo;
    @FXML private RadioButton optEditorLinenoViewYes;
    @FXML private RadioButton optEditorLinenoViewNo;
    @FXML private RadioButton optEditorRenderViewYes;
    @FXML private RadioButton optEditorRenderViewNo;
    @FXML private RadioButton optSmartEditorYes;
    @FXML private RadioButton optSmartEditorNo;
    @FXML private Button optEditorFontButton;
    @FXML private ComboBox<Theme> optDisplayTheme;
    @FXML private ComboBox<Lang> optDisplayLang;
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
    @FXML private Label workspacepath;


    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        this.config = MainApp.getConfig();

        setGeneralOptions();
        setEditorOptions();
        setDisplayOptions();
        setShortcutOptions();
        setAuthentificationOptions();
        setAdvancedOptions();

        workspacepath.setText(config.getWorkspacePath());
    }

    public void setWindow(Stage window){
        this.optionsWindow = window;
    }

    @FXML private void HandleSaveButtonAction(){
        config.setEditorFont(optEditorFont);
        config.setEditorFontSize(String.valueOf(optEditorFontSize));
        config.setEditorToolbarView(optEditorToolbarView);
        config.setEditorLinenoView(optEditorLinenoView);
        config.setEditorRenderView(optEditorRenderView);
        config.setEditorSmart(""+optSmartEditor);

        config.setDisplayTheme(optDisplayTheme.getValue().getFilename());
        config.setDisplayLang(optDisplayLang.getValue().getLocale().toString());

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
        config.loadWorkspace();
        optionsWindow.close();
    }

    @FXML private void HandleCancelButtonAction(){
        Alert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Configuration.bundle.getString("ui.options.cancel.title"));
        alert.setHeaderText(Configuration.bundle.getString("ui.options.cancel.header"));
        alert.setContentText(Configuration.bundle.getString("ui.options.cancel.text"));
        alert.initOwner(optionsWindow);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){
            if(result.get() == ButtonType.OK){
                optionsWindow.close();
            }
        }
    }

    @FXML private void HandleResetButtonAction(){
        Alert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Configuration.bundle.getString("ui.options.reset.title"));
        alert.setHeaderText(Configuration.bundle.getString("ui.options.reset.header"));
        alert.setContentText(Configuration.bundle.getString("ui.options.reset.text"));
        alert.getButtonTypes().setAll(new ButtonType(Configuration.bundle.getString("ui.yes"), ButtonBar.ButtonData.YES), new ButtonType(Configuration.bundle.getString("ui.no"), ButtonBar.ButtonData.NO));
        alert.initOwner(optionsWindow);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){
            if(result.get().getButtonData() == ButtonBar.ButtonData.YES){
                resetOptions();
            }
        }
    }

    @FXML private void HandleGeneralBrowseAction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(Configuration.bundle.getString("ui.options.workspace"));
        directoryChooser.setInitialDirectory(MainApp.defaultHome);

        File directory = directoryChooser.showDialog(null);

        if(directory != null && directory.exists()){
            config.setWorkspacePath(directory.getAbsolutePath());
        }
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
        optEditorToolbarView = true;
    }

    @FXML private void HandleEditorToolbarViewNo(){
        optEditorToolbarView = false;
    }

    @FXML private void HandleEditorRenderViewYes(){
        optEditorRenderView = true;
    }

    @FXML private void HandleEditorRenderViewNo(){
        optEditorRenderView = false;
    }

    @FXML private void HandleEditorLinenoViewYes(){
        optEditorLinenoView = true;
    }

    @FXML private void HandleEditorLinenoViewNo(){
        optEditorLinenoView = false;
    }

    @FXML private void HandleSmartEditorYes(){
        optSmartEditor = true;
    }

    @FXML private void HandleSmartEditorNo(){
        optSmartEditor = false;
    }

    private void setGeneralOptions(){
    }

    private void setEditorOptions(){
        optEditorFontButton.setText(config.getEditorFont() + " - " + config.getEditorFontsize());

        optEditorFont = config.getEditorFont();
        optEditorFontSize = config.getEditorFontsize();
        optEditorToolbarView = config.isEditorToolbarView();
        optEditorLinenoView = config.isEditorLinenoView();
        optEditorRenderView = config.isEditorRenderView();
        optSmartEditor = config.getEditorSmart();

        optEditorToolbarViewYes.setSelected(optEditorToolbarView);
        optEditorToolbarViewNo.setSelected(!optEditorToolbarView);

        optEditorLinenoViewYes.setSelected(optEditorLinenoView);
        optEditorLinenoViewNo.setSelected(!optEditorLinenoView);

        optEditorRenderViewYes.setSelected(optEditorRenderView);
        optEditorRenderViewNo.setSelected(!optEditorRenderView);

        if(optSmartEditor)
            optSmartEditorYes.setSelected(true);
        else
            optSmartEditorNo.setSelected(true);

        optEditorRenderViewYes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(optEditorRenderViewYes.isSelected()){
                Alert alert = new CustomAlert(Alert.AlertType.WARNING);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.change_render.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.change_render.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.change_render.text"));
                alert.initOwner(optionsWindow);

                alert.showAndWait();
            }
        });
    }

    private void setDisplayOptions(){
        optDisplayTheme.getItems().addAll(Theme.themeAvailable);
        optDisplayTheme.setValue(Theme.getThemeFromFileName(config.getDisplayTheme()));

        optDisplayTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Alert alert = new CustomAlert(Alert.AlertType.WARNING);
            alert.setTitle(Configuration.bundle.getString("ui.dialog.change_theme.title"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.change_theme.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.change_theme.text"));
            alert.initOwner(optionsWindow);

            alert.showAndWait();
        });

        optDisplayLang.getItems().addAll(Lang.langAvailable);
        optDisplayLang.setValue(Lang.getLangFromCode(config.getDisplayLang()));
        optDisplayLang.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Alert alert = new CustomAlert(Alert.AlertType.WARNING);
            alert.setTitle(Configuration.bundle.getString("ui.dialog.change_lang.title"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.change_lang.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.change_lang.text"));
            alert.initOwner(optionsWindow);

            alert.showAndWait();
        });

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
