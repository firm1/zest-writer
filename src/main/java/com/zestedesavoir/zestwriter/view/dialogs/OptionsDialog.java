package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.License;
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
    private Stage optionsWindow;

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
    @FXML private ComboBox<License> optWritingLicense;
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
    @FXML private TextField optAdvancedPandocUrl;
    @FXML private Label workspacepath;

    @FXML public void initialize() {
        setEditorOptions();
        setDisplayOptions();
        setAuthentificationOptions();
        setAdvancedOptions();

        workspacepath.setText(MainApp.getConfig().getWorkspacePath());
    }

    public void setWindow(Stage window){
        this.optionsWindow = window;
    }

    @FXML private void handleSaveButtonAction(){
        MainApp.getConfig().setEditorFont(optEditorFont);
        MainApp.getConfig().setEditorFontSize(String.valueOf(optEditorFontSize));
        MainApp.getConfig().setEditorToolbarView(optEditorToolbarView);
        MainApp.getConfig().setEditorLinenoView(optEditorLinenoView);
        MainApp.getConfig().setEditorRenderView(optEditorRenderView);
        MainApp.getConfig().setEditorSmart(Boolean.toString(optSmartEditor));

        MainApp.getConfig().setDisplayTheme(optDisplayTheme.getValue().getFilename());
        MainApp.getConfig().setDisplayLang(optDisplayLang.getValue().getLocale().toString());
        MainApp.getConfig().setWritingLicense(optWritingLicense.getValue().getCode());

        MainApp.getConfig().setWorkspacePath(workspacepath.getText());

        if(optDisplayWindowMaximizeYes.isSelected())
            MainApp.getConfig().setDisplayWindowMaximize("true");
        else
            MainApp.getConfig().setDisplayWindowMaximize("false");

        if(optDisplayWindowDimensionYes.isSelected())
            MainApp.getConfig().setDisplayWindowStandardDimension("true");
        else
            MainApp.getConfig().setDisplayWindowStandardDimension("false");

        if(optDisplayWindowPositionYes.isSelected())
            MainApp.getConfig().setDisplayWindowPersonnalPosition("true");
        else
            MainApp.getConfig().setDisplayWindowPersonnalPosition("false");

        MainApp.getConfig().setAuthentificationUsername(optAuthentificationUsername.getText());
        MainApp.getConfig().setAuthentificationPassword(optAuthentificationPassword.getText());

        MainApp.getConfig().setAdvancedServerProtocol(optAdvancedProtocol.getValue());
        MainApp.getConfig().setAdvancedServerHost(optAdvancedHost.getText());
        MainApp.getConfig().setAdvancedServerPort(optAdvancedPort.getText());
        MainApp.getConfig().setAdvancedServerPandoc(optAdvancedPandocUrl.getText());

        MainApp.getConfig().saveConfFile();
        MainApp.getConfig().loadWorkspace();
        optionsWindow.close();
    }

    @FXML private void handleCancelButtonAction(){
        Alert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Configuration.getBundle().getString("ui.options.cancel.title"));
        alert.setHeaderText(Configuration.getBundle().getString("ui.options.cancel.header"));
        alert.setContentText(Configuration.getBundle().getString("ui.options.cancel.text"));
        alert.initOwner(optionsWindow);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK){
            optionsWindow.close();
        }
    }

    @FXML private void handleResetButtonAction(){
        Alert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Configuration.getBundle().getString("ui.options.reset.title"));
        alert.setHeaderText(Configuration.getBundle().getString("ui.options.reset.header"));
        alert.setContentText(Configuration.getBundle().getString("ui.options.reset.text"));
        alert.getButtonTypes().setAll(new ButtonType(Configuration.getBundle().getString("ui.yes"), ButtonBar.ButtonData.YES), new ButtonType(Configuration.getBundle().getString("ui.no"), ButtonBar.ButtonData.NO));
        alert.initOwner(optionsWindow);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES){
            resetOptions();
        }
    }

    @FXML private void handleGeneralBrowseAction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(Configuration.getBundle().getString("ui.options.workspace"));
        directoryChooser.setInitialDirectory(MainApp.getDefaultHome());

        File directory = directoryChooser.showDialog(null);

        if(directory != null && directory.exists()){
            workspacepath.setText(directory.getAbsolutePath());
        }
    }

    @FXML private void handleEditorFontChoice(){
        Dialog<Font> fontSelector = new FontSelectorDialog(new Font(MainApp.getConfig().getEditorFont(), MainApp.getConfig().getEditorFontsize()));
        Optional<Font> result = fontSelector.showAndWait();

        if(result.isPresent()){
            Font newFont = result.get();

            optEditorFont = newFont.getName();
            optEditorFontSize = newFont.getSize();
            optEditorFontButton.setText(optEditorFont + " - " + optEditorFontSize);
        }
    }

    @FXML private void handleDisplayWindowMaximizeYes(){
        optDisplayWindowDimensionYes.setDisable(true);
        optDisplayWindowDimensionNo.setDisable(true);
        optDisplayWindowPositionYes.setDisable(true);
        optDisplayWindowPositionNo.setDisable(true);
    }

    @FXML private void handleDisplayWindowMaximizeNo(){
        optDisplayWindowDimensionYes.setDisable(false);
        optDisplayWindowDimensionNo.setDisable(false);
        optDisplayWindowPositionYes.setDisable(false);
        optDisplayWindowPositionNo.setDisable(false);
    }

    @FXML private void handleEditorToolbarViewYes(){
        optEditorToolbarView = true;
    }

    @FXML private void handleEditorToolbarViewNo(){
        optEditorToolbarView = false;
    }

    @FXML private void handleEditorRenderViewYes(){
        optEditorRenderView = true;
    }

    @FXML private void handleEditorRenderViewNo(){
        optEditorRenderView = false;
    }

    @FXML private void handleEditorLinenoViewYes(){
        optEditorLinenoView = true;
    }

    @FXML private void handleEditorLinenoViewNo(){
        optEditorLinenoView = false;
    }

    @FXML private void handleSmartEditorYes(){
        optSmartEditor = true;
    }

    @FXML private void handleSmartEditorNo(){
        optSmartEditor = false;
    }

    private void setEditorOptions(){
        optEditorFontButton.setText(MainApp.getConfig().getEditorFont() + " - " + MainApp.getConfig().getEditorFontsize());

        optEditorFont = MainApp.getConfig().getEditorFont();
        optEditorFontSize = MainApp.getConfig().getEditorFontsize();
        optEditorToolbarView = MainApp.getConfig().isEditorToolbarView();
        optEditorLinenoView = MainApp.getConfig().isEditorLinenoView();
        optEditorRenderView = MainApp.getConfig().isEditorRenderView();
        optSmartEditor = MainApp.getConfig().isEditorSmart();

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
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.change_render.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.change_render.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.change_render.text"));
                alert.initOwner(optionsWindow);

                alert.showAndWait();
            }
        });
    }

    private void setDisplayOptions(){
        optDisplayTheme.getItems().addAll(Theme.getThemeAvailable());
        optDisplayTheme.setValue(Theme.getThemeFromFileName(MainApp.getConfig().getDisplayTheme()));
        optDisplayTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Alert alert = new CustomAlert(Alert.AlertType.WARNING);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.change_theme.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.change_theme.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.change_theme.text"));
            alert.initOwner(optionsWindow);

            alert.showAndWait();
        });

        optDisplayLang.getItems().addAll(Lang.getLangAvailable());
        optDisplayLang.setValue(Lang.getLangFromCode(MainApp.getConfig().getDisplayLang()));
        optDisplayLang.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Alert alert = new CustomAlert(Alert.AlertType.WARNING);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.change_lang.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.change_lang.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.change_lang.text"));
            alert.initOwner(optionsWindow);

            alert.showAndWait();
        });

        optWritingLicense.getItems().addAll(EditContentDialog.getLicOptions());
        optWritingLicense.setValue(License.getLicenseFromCode(MainApp.getConfig().getWritingLicense()));

        if(MainApp.getConfig().isDisplayWindowMaximize())
            optDisplayWindowMaximizeYes.setSelected(true);
        else
            optDisplayWindowMaximizeNo.setSelected(true);

        if(MainApp.getConfig().isDisplayWindowPersonnalDimension())
            optDisplayWindowDimensionYes.setSelected(true);
        else
            optDisplayWindowDimensionNo.setSelected(true);

        if(MainApp.getConfig().isDisplayWindowPersonnalPosition())
            optDisplayWindowPositionYes.setSelected(true);
        else
            optDisplayWindowPositionNo.setSelected(true);


        if(MainApp.getConfig().isDisplayWindowMaximize()){
            optDisplayWindowDimensionYes.setDisable(true);
            optDisplayWindowDimensionNo.setDisable(true);
            optDisplayWindowPositionYes.setDisable(true);
            optDisplayWindowPositionNo.setDisable(true);
        }
    }

    private void setAuthentificationOptions(){
        optAuthentificationUsername.setText(MainApp.getConfig().getAuthentificationUsername());
        optAuthentificationPassword.setText(MainApp.getConfig().getAuthentificationPassword());
    }

    private void setAdvancedOptions(){
        optAdvancedProtocol.getItems().addAll("https", "http");
        optAdvancedProtocol.setValue(MainApp.getConfig().getAdvancedServerProtocol());
        optAdvancedHost.setText(MainApp.getConfig().getAdvancedServerHost());
        optAdvancedPort.setText(MainApp.getConfig().getAdvancedServerPort());
        optAdvancedPandocUrl.setText(MainApp.getConfig().getAdvancedServerPandoc());
    }

    private void resetOptions(){
        MainApp.getConfig().resetAllOptions();
        initialize();
    }
}
