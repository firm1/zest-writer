package com.zestedesavoir.zestwriter.contents.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.api.ApiContentResponse;
import com.zestedesavoir.zestwriter.utils.api.ApiMapper;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import com.zestedesavoir.zestwriter.view.dialogs.ContentsDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ContentsConfig{
    private Logger logger = LoggerFactory.getLogger(ContentsConfig.class);
    private ObjectMapper mapper = new ObjectMapper();

    private File configFileOfficial;
    private File configFile;
    private ContentsConfigJson configJsonOfficial;
    private ContentsConfigJson configJson;
    private boolean isCorrupted = false;

    public enum ConfigType{
        OFFICIAL(MainApp.getConfig().getContentsPath() + "/config.official.data"),
        UNOFFICIAL(MainApp.getConfig().getContentsPath() + "/config.data");

        public String path;

        ConfigType(String path){
            this.path = path;
        }
    }

    public ContentsConfig(){
        configFileOfficial = new File(ConfigType.OFFICIAL.path);
        configFile = new File(ConfigType.UNOFFICIAL.path);

        if(!configFileOfficial.exists())
            setConfig(ConfigType.OFFICIAL);
        else
            loadConfig(ConfigType.OFFICIAL);

        if(!configFile.exists())
            setConfig(ConfigType.UNOFFICIAL);
        else
            loadConfig(ConfigType.UNOFFICIAL);
    }

    public void enableContent(){

    }

    public void disableContent(){

    }

    public void addContents(ConfigType configType, ContentsDialog.ContentType contentsType, ApiContentResponse contentResponse){
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

        if(configType == ConfigType.OFFICIAL){
            configJsonOfficial.addContent(detailJson);
            saveConfig(ConfigType.OFFICIAL);
        }else{
            configJson.addContent(detailJson);
            saveConfig(ConfigType.UNOFFICIAL);
        }
    }

    public File getConfigFileOfficial(){
        return configFileOfficial;
    }

    public File getConfigFile(){
        return configFile;
    }

    public ContentsConfigJson getConfigJsonOfficial(){
        return configJsonOfficial;
    }

    public ContentsConfigJson getConfigJson(){
        return configJson;
    }

    public boolean isCorrupted(){
        return isCorrupted;
    }

    public void generateIndex(ConfigType configType){
        setConfig(configType);

        String path;
        if(configType == ConfigType.OFFICIAL)
            path = MainApp.class.getResource("officialContents/plugins").toString();
        else
            path = MainApp.getConfig().getContentsPath() + "/plugins";

        File dataFiles[] = new File(path).listFiles();

        if(dataFiles == null){
            setConfig(configType);
            return;
        }

        logger.debug("Generate index for : " + configType);
        for(File dataFile : dataFiles){
            logger.debug("  Add -> " + dataFile.getPath());
            String line;
            StringBuilder json = new StringBuilder();
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
                while((line = reader.readLine()) != null){
                    json.append(line);
                }
            }catch(IOException e){
                logger.error(e.getMessage(), e);
            }

            InternalMapper mapper = new InternalMapper(json.toString());
            ApiContentResponse content = mapper.getContent();
            addContents(configType, ContentsDialog.ContentType.PLUGIN, content);
        }
    }

    private void loadConfig(ConfigType configType){
        logger.debug("Chargement du fichier de configuration des contenus externe");

        try{
            if(configType == ConfigType.OFFICIAL)
                configJsonOfficial = mapper.readValue(configFileOfficial, ContentsConfigJson.class);
            else
                configJson = mapper.readValue(configFile, ContentsConfigJson.class);
        }catch(IOException e){
            logger.error(configType + " -- " + e.getMessage(), e);

            Alert alert = new Alert(Alert.AlertType.WARNING);
            IconFactory.addAlertLogo(alert);
            FunctionTreeFactory.addTheming(alert.getDialogPane());
            alert.setHeaderText("Une erreur dans le fichier de configuration des contenus externes à été détecté");
            alert.setContentText("Il se peut que le fichier aie été corrompu ou modifier par un programme externe, " +
                    "afin de résoudre ce problème, il faut créer un réindexage des plugins. Après ceci, tous les plugins seront à nouveau activer par défaut." +
                    "\n\nSouhaitez-vous procéder au réindexage ?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if(alert.getResult() == ButtonType.YES)
                generateIndex(configType);
            else
                isCorrupted = true;
        }
    }

    private void saveConfig(ConfigType configType){
        logger.debug("Sauvegarde du fichier de configuration des contenus externes");
        try{
            if(configType == ConfigType.OFFICIAL)
                mapper.writeValue(configFileOfficial, configJsonOfficial);
            else
                mapper.writeValue(configFile, configJson);
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    private void setConfig(ConfigType configType){
        logger.debug("Création du fichier de configuration des contenus externes");

        if(configType == ConfigType.OFFICIAL)
            configJsonOfficial = new ContentsConfigJson();
        else
            configJson = new ContentsConfigJson();

        try{
            if(configType == ConfigType.OFFICIAL)
                mapper.writeValue(configFileOfficial, configJsonOfficial);
            else
                mapper.writeValue(configFile, configJson);
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }
}
