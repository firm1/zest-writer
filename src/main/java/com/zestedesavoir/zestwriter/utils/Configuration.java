package com.zestedesavoir.zestwriter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Configuration {
    private Properties conf;
    private String appName = "zestwriter";
    private String confHomepageName = "homepage.properties";
    private String confFileName = "conf.properties";
    private String confDirPath;
    private File confFile;
    private File homepageConfFile;
    private StorageSaver offlineSaver;
    private StorageSaver onlineSaver;
    private LocalDirectoryFactory workspaceFactory;
    private final Logger logger;
    private Properties props;

    public enum HomepageData{
        HomepageRecentFile1("data.homepage.recentFile.1", ""),
        HomepageRecentFile2("data.homepage.recentFile.2", ""),
        HomepageRecentFile3("data.homepage.recentFile.3", ""),
        HomepageRecentFile4("data.homepage.recentFile.4", ""),
        HomepageRecentFile5("data.homepage.recentFile.5", "");

        private String key;
        private String defaultValue;

        HomepageData(String key, String defaultValue){
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey(){
            return key;
        }

        public String getDefaultValue(){
            return defaultValue;
        }
    }

    public enum ConfigData{
        DisplayWindowWidth("data.display.window.width", "1000"),
        DisplayWindowHeight("data.display.window.height", "600"),
        DisplayWindowPositionX("data.display.window.position.x", "0"),
        DisplayWindowPositionY("data.display.window.position.y", "0"),

        WorkspacePath("options.workspace.path", ""),
        EditorSmart("options.editor.smart", "false"),
        EditorFont("options.editor.font", "Fira Mono"),
        EditorFontSize("options.editor.fontSize", "14"),
        EditorToolbarView("options.editor.toolbar.view", "yes"),
        DisplayTheme("options.display.theme", "Standard"),
        DisplayWindowPersonnalDimension("options.display.window.standardDimension", "true"),
        DisplayWindowPersonnalPosition("options.display.window.standardPosition", "true"),
        DisplayWindowMaximize("options.display.window.maximize", "false"),
        AuthentificationUsername("options.authentification.username", ""),
        AuthentificationPassword("options.authentification.password", ""),
        AdvancedServerProtocol("options.advanced.protocol", "https"),
        AdvancedServerHost("options.advanced.host", "zestedesavoir.com"),
        AdvancedServerPort("options.advanced.port", "80");

        private String key;
        private String defaultValue;

        ConfigData(String key, String defaultValue){
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey(){
            return key;
        }

        public String getDefaultValue(){
            return defaultValue;
        }
    }

    public Configuration(String homeDir) {
        logger = LoggerFactory.getLogger(Configuration.class);
        confDirPath = homeDir+File.separator+"."+this.appName;
        String confFilePath = confDirPath+File.separator+this.confFileName;
        String homepageConfFilePath = confDirPath+File.separator+this.confHomepageName;
        File confDir = new File(confDirPath);
        confFile = new File(confFilePath);
        homepageConfFile = new File(homepageConfFilePath);

        if(!confDir.exists()){
            if(!confDir.mkdir())
                logger.error("Le fichier de configuration n'a pas pu être créé");
        }

        // defaults config
        props = new Properties();
        try {
            props.load(MainApp.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            logger.error("", e);
        }

        conf = new Properties(props);

        if(!confFile.exists()) {
            logger.debug("le fichier de configuartion "+confFile.getAbsolutePath()+" n'existe pas");
            setWorkspacePath(Configuration.getDefaultWorkspace());
            saveConfFile();
        }
        else {
            try {
                conf.load(new FileInputStream(confFile));
                loadWorkspace();
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        props.entrySet().stream()
                .filter(entry -> !conf.containsKey(entry.getKey()))
                .forEach(entry -> {
                        conf.putIfAbsent(entry.getKey(), entry.getValue());
                        saveConfFile();
                    }
                );
    }

    public void saveConfFile() {
        try {
            conf.store(new FileOutputStream(confFile), "");
            logger.info("Fichier de configuration enregistré");
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for(Entry<?, ?> entry:conf.entrySet()) {
            result.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }

        return result.toString();
    }

    public static String getDefaultWorkspace() {
        JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        return fw.getDefaultDirectory().getAbsolutePath() + File.separator + "zwriter-workspace";
    }

    public String getPandocProvider() {
        return "http://vps146092.ovh.net/2pdf/";
    }

    public StorageSaver getOfflineSaver() {
        return offlineSaver;
    }

    public StorageSaver getOnlineSaver() {
        return onlineSaver;
    }

    public LocalDirectoryFactory getWorkspaceFactory() {
        return workspaceFactory;
    }

    public void loadWorkspace() {

        this.workspaceFactory = new LocalDirectoryFactory(getWorkspacePath());

        offlineSaver = workspaceFactory.getOfflineSaver();
        onlineSaver = workspaceFactory.getOnlineSaver();
        logger.info("Espace de travail chargé en mémoire");
    }

    public static String getLastRelease() throws IOException {
        String projecUrlRelease = "https://api.github.com/repos/firm1/zest-writer/releases/latest";

        String json = Request.Get(projecUrlRelease).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        Map map = mapper.readValue(json, Map.class);
        if(map.containsKey("tag_name")) {
            return (String) map.get("tag_name");
        }
        return null;
    }


    /*
     * Zest-Writer data
     */
    private double getGenericDoubleDisplay(ConfigData configData) {
        if(conf.containsKey(configData.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(configData.getKey())))
                return Double.parseDouble(conf.getProperty(configData.getKey()));
            else
                return Double.parseDouble(configData.getDefaultValue());
        }else{
            return Double.parseDouble(configData.getDefaultValue());
        }
    }

    private boolean getGenericBooleanDisplay(ConfigData configData) {
        if(conf.containsKey(configData.getKey())){
            return Boolean.parseBoolean(conf.getProperty(configData.getKey()));
        }else{
            return Boolean.parseBoolean(configData.getDefaultValue());
        }
    }

    public double getDisplayWindowWidth(){
        return getGenericDoubleDisplay(ConfigData.DisplayWindowWidth);
    }
    public void setDisplayWindowWidth(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowWidth.getKey(), windowWidth);
    }

    public double getDisplayWindowHeight(){
        return getGenericDoubleDisplay(ConfigData.DisplayWindowHeight);
    }
    public void setDisplayWindowHeight(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowHeight.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionX(){
        return getGenericDoubleDisplay(ConfigData.DisplayWindowPositionX);
    }
    public void setDisplayWindowPositionX(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowPositionX.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionY(){
        return getGenericDoubleDisplay(ConfigData.DisplayWindowPositionY);
    }
    public void setDisplayWindowPositionY(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowPositionY.getKey(), windowWidth);
    }

    /*
     * Zest-Writer options
     */
    public String getWorkspacePath(){
        if(conf.containsKey(ConfigData.WorkspacePath.getKey()))
            return conf.getProperty(ConfigData.WorkspacePath.getKey());
        else
            return Configuration.getDefaultWorkspace();
    }

    public void setWorkspacePath(String font){
        conf.setProperty(ConfigData.WorkspacePath.getKey(), font);
    }

    public Boolean getEditorSmart(){
        return getGenericBooleanDisplay(ConfigData.EditorSmart);
    }

    public void isEditorSmart(String editorSmart){
        conf.setProperty(ConfigData.EditorSmart.getKey(), editorSmart);
    }

    public String getEditorFont(){
        if(conf.containsKey(ConfigData.EditorFont.getKey()))
            return conf.getProperty(ConfigData.EditorFont.getKey());
        else
            return ConfigData.EditorFont.getDefaultValue();
    }

    public void setEditorFont(String font){
        conf.setProperty(ConfigData.EditorFont.getKey(), font);
    }

    public double getEditorFontsize(){
        return getGenericDoubleDisplay(ConfigData.EditorFontSize);
    }

    public void setEditorFontSize(String fontSize){
        conf.setProperty(ConfigData.EditorFontSize.getKey(), fontSize);
    }

    public String getEditorToolbarView(){
        if(conf.containsKey(ConfigData.EditorToolbarView.getKey()))
            return conf.getProperty(ConfigData.EditorToolbarView.getKey());
        else
            return ConfigData.DisplayTheme.getDefaultValue();
    }
    public void setEditorToolbarView(String view){
        if(!view.toLowerCase().equals("yes") && !view.toLowerCase().equals("no"))
            view = ConfigData.EditorToolbarView.getDefaultValue();

        conf.setProperty(ConfigData.EditorToolbarView.getKey(), view);
    }

    public String getDisplayTheme(){
        if(conf.containsKey(ConfigData.DisplayTheme.getKey()))
            return conf.getProperty(ConfigData.DisplayTheme.getKey());
        else
            return ConfigData.DisplayTheme.getDefaultValue();
    }
    public void setDisplayTheme(String displayTheme){
        conf.setProperty(ConfigData.DisplayTheme.getKey(), displayTheme);
    }

    public boolean isDisplayWindowPersonnalDimension(){
        return getGenericBooleanDisplay(ConfigData.DisplayWindowPersonnalDimension);
    }
    public void setDisplayWindowStandardDimension(String standardDimension){
        conf.setProperty(ConfigData.DisplayWindowPersonnalDimension.getKey(), standardDimension);
    }

    public boolean isDisplayWindowPersonnalPosition(){
        return getGenericBooleanDisplay(ConfigData.DisplayWindowPersonnalPosition);
    }
    public void setDisplayWindowPersonnalPosition(String standardPosition){
        conf.setProperty(ConfigData.DisplayWindowPersonnalPosition.getKey(), standardPosition);
    }

    public boolean isDisplayWindowMaximize(){
        return getGenericBooleanDisplay(ConfigData.DisplayWindowMaximize);
    }
    public void setDisplayWindowMaximize(String maximize){
        conf.setProperty(ConfigData.DisplayWindowMaximize.getKey(), maximize);
    }

    public String getAuthentificationUsername(){
        if(conf.containsKey(ConfigData.AuthentificationUsername.getKey()))
            return conf.getProperty(ConfigData.AuthentificationUsername.getKey());
        else
            return ConfigData.AuthentificationUsername.getDefaultValue();
    }
    public void setAuthentificationUsername(String username){
        conf.setProperty(ConfigData.AuthentificationUsername.getKey(), username);
    }

    public String getAuthentificationPassword(){
        if(conf.containsKey(ConfigData.AuthentificationPassword.getKey()))
            return conf.getProperty(ConfigData.AuthentificationPassword.getKey());
        else
            return ConfigData.AuthentificationPassword.getDefaultValue();
    }

    public void setAuthentificationPassword(String password){
        conf.setProperty(ConfigData.AuthentificationPassword.getKey(), password);
    }

    public String getAdvancedServerProtocol(){
        if(conf.containsKey(ConfigData.AdvancedServerProtocol.getKey()))
            return conf.getProperty(ConfigData.AdvancedServerProtocol.getKey());
        else
            return ConfigData.AdvancedServerProtocol.getDefaultValue();
    }

    public void setAdvancedServerProtocol(String protocol){
        conf.setProperty(ConfigData.AdvancedServerProtocol.getKey(), protocol);
    }

    public String getAdvancedServerHost(){
        if(conf.containsKey(ConfigData.AdvancedServerHost.getKey()))
            return conf.getProperty(ConfigData.AdvancedServerHost.getKey());
        else
            return ConfigData.AdvancedServerHost.getDefaultValue();
    }

    public void setAdvancedServerHost(String host){
        conf.setProperty(ConfigData.AdvancedServerHost.getKey(), host);
    }

    public String getAdvancedServerPort(){
        if(conf.containsKey(ConfigData.AdvancedServerPort.getKey()))
            return conf.getProperty(ConfigData.AdvancedServerPort.getKey());
        else
            return ConfigData.AdvancedServerPort.getDefaultValue();
    }

    public void setAdvancedServerPort(String port){
        conf.setProperty(ConfigData.AdvancedServerPort.getKey(), port);
    }

    public void resetAuthentification(){
        setAuthentificationUsername("");
        setAuthentificationPassword("");
        saveConfFile();
    }

    public void resetAllOptions(){
        for(ConfigData opt : ConfigData.values()){
            conf.setProperty(opt.getKey(), opt.getDefaultValue());
        }

        saveConfFile();
    }

    public Properties getProps() {
        return props;
    }
}
