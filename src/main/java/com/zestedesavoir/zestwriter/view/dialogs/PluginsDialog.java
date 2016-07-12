package com.zestedesavoir.zestwriter.view.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PluginsDialog{
    private Logger logger = LogManager.getLogger(PluginsDialog.class);

    @FXML private ListView listPlugins;
    @FXML private ListView listOfficialInstalledPlugins;
    @FXML private ListView listUnofficialInstalledPlugins;

    @FXML private void initialize(){
        logger.debug("Initialize");
        listPlugins.getItems().add("[VALIDER] Un super plugin");
        listPlugins.getItems().add("Un autre plugin");

        listPlugins.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            logger.debug("[EVENT] List plugins changed");
        });
    }

    @FXML private void handleListPluginInfos(){
        logger.debug("ListPluginsInfos");
    }

    @FXML private void handleListPluginsInstall(){
        logger.debug("ListPluginsInstall");
    }

    @FXML private void handleOfficialPluginState(){
        logger.debug("OfficialPluginState");
    }

    @FXML private void handleUnofficialPluginState(){
        logger.debug("UnofficialPluginState");
    }

    @FXML private void handleUnofficialPluginUninstall(){
        logger.debug("UnofficialPluginUninstall");
    }
}
