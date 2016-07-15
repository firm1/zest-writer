package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ContentsDialog{
    private Logger logger = LogManager.getLogger(ContentsDialog.class);
    private Stage window;

    @FXML private ListView listThemes;
    @FXML private ListView listOfficialInstalledThemes;
    @FXML private ListView listUnofficialInstalledThemes;
    @FXML private ListView listPlugins;
    @FXML private ListView listOfficialInstalledPlugins;
    @FXML private ListView listUnofficialInstalledPlugins;
    private String currentPlugin;
    private String currentTheme;

    public void setWindow(Stage window){
        this.window = window;
    }

    @FXML private void initialize(){
        logger.debug("Initialize");
        listPlugins.getItems().add("[0.0.0] [VALIDER] Un super plugin");
        listPlugins.getItems().add("[0.0.1] Un autre plugin");

        listPlugins.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentPlugin = (String)newValue;
        });

        listThemes.getItems().add("[0.0.0] [VALIDER] Un super thème");
        listThemes.getItems().add("[0.0.1] Un autre thème");

        listThemes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentTheme = (String)newValue;
        });
    }

    @FXML private void handleListPluginInfos(){
        alert(Alert.AlertType.INFORMATION,
                "Plugin informations",
                "Plugin proposé par: WinXaito\nVersion: 0.0.0\nValidé: Oui",
                "Description"
        );
    }

    @FXML private void handleListPluginInstall(){
        alert(Alert.AlertType.CONFIRMATION, "Installation", null, "Voulez-vous vraiment installé ce plugin ?");
    }

    @FXML private void handleOfficialPluginState(){
        
    }

    @FXML private void handleUnofficialPluginState(){
        
    }

    @FXML private void handleUnofficialPluginUninstall(){
        alert(Alert.AlertType.CONFIRMATION, "Désinstallation", null, "Voulez-vous désinstaller ce plugin ?");
    }

    @FXML private void handleListThemeInfos(){
        alert(Alert.AlertType.INFORMATION,
                "Thème informations",
                "Thèmme proposé par: WinXaito\nVersion: 0.0.0\nValidé: Oui",
                "Description"
        );
    }

    @FXML private void handleListThemeInstall(){

    }

    @FXML private void handleOfficialThemeState(){

    }

    @FXML private void handleUnofficialThemeState(){

    }

    @FXML private void handleUnofficialThemeUninstall(){

    }

    private void alert(Alert.AlertType type, String title, String header, String content){
        Alert a = new CustomAlert(type);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.initOwner(window);
        a.showAndWait();
    }
}
