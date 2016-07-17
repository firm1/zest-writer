package com.zestedesavoir.zestwriter.contents.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.api.ApiContentResponse;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import com.zestedesavoir.zestwriter.view.dialogs.ContentsDialog;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ContentsConfig{
    private Logger logger = LoggerFactory.getLogger(ContentsConfig.class);

    private File configFile;
    private ObjectMapper mapper = new ObjectMapper();
    private ContentsConfigJson configJson;
    private boolean isCorrupted = false;

    public ContentsConfig(){
        configFile = new File(MainApp.getConfig().getContentsPath() + "/config.data");

        if(!configFile.exists()){
            setConfig();
        }else{
            loadConfig();
        }
    }

    public void addContents(ContentsDialog.ContentType contentsType, ApiContentResponse contentResponse){
        ContentsConfigDetailJson detailJson = new ContentsConfigDetailJson();
        detailJson.setContentsType(contentsType);
        detailJson.setId(contentResponse.getId());
        detailJson.setName(contentResponse.getName());
        detailJson.setUser_name(contentResponse.getUser().getName());
        detailJson.setDescription(contentResponse.getDescription());
        detailJson.setVersion(contentResponse.getVersion());
        detailJson.setUrl_id(contentResponse.getUrl_id());
        detailJson.setPlugin_url(contentResponse.getPlugin_url());
        detailJson.setDownload_url(contentResponse.getDownload_url());

        configJson.addContent(detailJson);
        saveConfig();
    }

    public File getConfigFile(){
        return configFile;
    }

    public boolean isCorrupted(){
        return isCorrupted;
    }

    private void loadConfig(){
        logger.debug("Chargement du fichier de configuration des contenus externe");

        try{
            configJson = mapper.readValue(configFile, ContentsConfigJson.class);
        }catch(IOException e){
            logger.error(e.getMessage(), e);

            Alert alert = new Alert(Alert.AlertType.WARNING);
            IconFactory.addAlertLogo(alert);
            FunctionTreeFactory.addTheming(alert.getDialogPane());
            alert.setHeaderText("Une erreur dans le fichier de configuration des contenus externes à été détecté");
            alert.setContentText("Il se peut que le fichier aie été corrompu ou modifier par un programme externe, " +
                    "afin de résoudre ce problème, veuillez soumettre le fichier suivant au développeur <" + configFile.getPath() + ">");
            alert.showAndWait();

            isCorrupted = true;
        }
    }

    private void saveConfig(){
        logger.debug("Sauvegarde du fichier de configuration des contenus externes");
        try{
            mapper.writeValue(new File(MainApp.getConfig().getContentsPath() + "/config.data"), configJson);
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    private void setConfig(){
        logger.debug("Création du fichier de configuration des contenus externes");
        configJson = new ContentsConfigJson();

        try{
            mapper.writeValue(new File(MainApp.getConfig().getContentsPath() + "/config.data"), configJson);
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }
}
