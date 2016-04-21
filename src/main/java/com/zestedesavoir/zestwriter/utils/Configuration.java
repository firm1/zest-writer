package com.zestedesavoir.zestwriter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;

public class Configuration {
    private Properties conf;
    private String appName = "zestwriter";
    private String confFileName = "conf.properties";
    private File confFile;
    private StorageSaver offlineSaver;
    private StorageSaver onlineSaver;
    private LocalDirectoryFactory workspaceFactory;
    private final Logger logger;
    private Properties props;

    private final static String WORKSPACE_KEY = "data.workspace";
    private final static String SMART_EDITOR_KEY = "editor.smart";
    private final static String SERVER_PROTOCOL_KEY = "server.protocol";
    private final static String SERVER_HOST_KEY = "server.host";
    private final static String SERVER_PORT_KEY = "server.port";


    public enum ConfigData{
        DisplayWindowWidth("data.display.window.width", "800"),
        DisplayWindowHeight("data.display.window.height", "800"),
        DisplayWindowPositionX("data.display.window.position.x", "0"),
        DisplayWindowPositionY("data.display.window.position.y", "0"),

        EditorFont("options.editor.font", "Arial"),
        EditorFontSize("options.editor.fontSize", "14"),
        DisplayTheme("options.display.theme", "Standard"),
        DisplayWindowStandardDimension("options.display.window.standardDimension", "false"),
        DisplayWindowStandardPosition("options.display.window.standardPosition", "true"),
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
        String confDirPath = homeDir+File.separator+"."+this.appName;
        String confFilePath = confDirPath+File.separator+this.confFileName;
        File confDir = new File(confDirPath);
        confFile = new File(confFilePath);
        if(!confDir.exists()) {
            confDir.mkdir();
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
            JFileChooser fr = new JFileChooser();
            FileSystemView fw = fr.getFileSystemView();
            conf.setProperty(WORKSPACE_KEY, fw.getDefaultDirectory().getAbsolutePath() + File.separator + "zwriter-workspace");
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

        for(Entry<?, ?> entry:props.entrySet()) {
            if(!conf.containsKey(entry.getKey())) {
                conf.putIfAbsent(entry.getKey(), entry.getValue());
                saveConfFile();
            }
        }
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

    public String getWorkspacePath() {
        return conf.getProperty(WORKSPACE_KEY);
    }

    public void setWorkspacePath(String workspacePath) {
        conf.setProperty(WORKSPACE_KEY, workspacePath);
        saveConfFile();
        this.workspaceFactory = new LocalDirectoryFactory(workspacePath);
    }

    public String getProtocol() {
        if(conf.containsKey(SERVER_PROTOCOL_KEY)) {
            return conf.getProperty(SERVER_PROTOCOL_KEY);
        } else {
            return "http";
        }
    }

    public String getPandocProvider() {
        return "http://vps146092.ovh.net/2pdf/";
    }

    public String getPort() {
        if(conf.containsKey(SERVER_PORT_KEY)) {
            return conf.getProperty(SERVER_PORT_KEY);
        } else {
            return "80";
        }
    }

    public String getHost() {
        if(conf.containsKey(SERVER_HOST_KEY)) {
            return conf.getProperty(SERVER_HOST_KEY);
        } else {
            return "localhost";
        }
    }


    public boolean isSmartEditor() {
        return conf.getProperty(SMART_EDITOR_KEY).equalsIgnoreCase("true");
    }

    public void setSmartEditor(boolean smart) {
        conf.setProperty(SMART_EDITOR_KEY, ""+smart);
        saveConfFile();
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

    public void loadWorkspace() throws IOException{

        this.workspaceFactory = new LocalDirectoryFactory(getWorkspacePath());

        try{
            offlineSaver = workspaceFactory.getOfflineSaver();
            onlineSaver = workspaceFactory.getOnlineSaver();
            logger.info("Espace de travail chargé en mémoire");
        }
        catch(IOException e){
            logger.error("", e);
        }

    }

    public String getLastRelease() {
        String projecUrlRelease = "https://api.github.com/repos/firm1/zest-writer/releases/latest";

        try {
            String json = Request.Get(projecUrlRelease).execute().returnContent().asString();
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
            Map map = mapper.readValue(json, Map.class);
            if(map.containsKey("tag_name")) {
                return (String) map.get("tag_name");
            }

        } catch (IOException e) {
            logger.error("Impossible de joindre l'api de github", e);
            e.printStackTrace();
        }
        return null;
    }


    /*
     * Zest-Writer data
     */
    public double getDisplayWindowWidth(){
        if(conf.containsKey(ConfigData.DisplayWindowWidth.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(ConfigData.DisplayWindowWidth.getKey())))
                return Double.parseDouble(conf.getProperty(ConfigData.DisplayWindowWidth.getKey()));
            else
                return Double.parseDouble(ConfigData.DisplayWindowWidth.getDefaultValue());
        }else{
            return Double.parseDouble(ConfigData.DisplayWindowWidth.getDefaultValue());
        }
    }
    public void setDisplayWindowWidth(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowWidth.getKey(), windowWidth);
    }

    public double getDisplayWindowHeight(){
        if(conf.containsKey(ConfigData.DisplayWindowHeight.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(ConfigData.DisplayWindowHeight.getKey())))
                return Double.parseDouble(conf.getProperty(ConfigData.DisplayWindowHeight.getKey()));
            else
                return Double.parseDouble(ConfigData.DisplayWindowHeight.getDefaultValue());
        }else{
            return Double.parseDouble(ConfigData.DisplayWindowHeight.getDefaultValue());
        }
    }
    public void setDisplayWindowHeight(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowHeight.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionX(){
        if(conf.containsKey(ConfigData.DisplayWindowPositionX.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(ConfigData.DisplayWindowPositionX.getKey())))
                return Double.parseDouble(conf.getProperty(ConfigData.DisplayWindowPositionX.getKey()));
            else
                return Double.parseDouble(ConfigData.DisplayWindowPositionX.getDefaultValue());
        }else{
            return Double.parseDouble(ConfigData.DisplayWindowPositionX.getDefaultValue());
        }
    }
    public void setDisplayWindowPositionX(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowPositionX.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionY(){
        if(conf.containsKey(ConfigData.DisplayWindowPositionY.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(ConfigData.DisplayWindowPositionY.getKey())))
                return Double.parseDouble(conf.getProperty(ConfigData.DisplayWindowPositionY.getKey()));
            else
                return Double.parseDouble(ConfigData.DisplayWindowPositionY.getDefaultValue());
        }else{
            return Double.parseDouble(ConfigData.DisplayWindowPositionY.getDefaultValue());
        }
    }
    public void setDisplayWindowPositionY(String windowWidth){
        conf.setProperty(ConfigData.DisplayWindowPositionY.getKey(), windowWidth);
    }

    /*
     * Zest-Writer options
     */
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
        if(conf.containsKey(ConfigData.EditorFontSize.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(ConfigData.EditorFontSize.getKey())))
                return Double.parseDouble(conf.getProperty(ConfigData.EditorFontSize.getKey()));
            else
                return Double.parseDouble(ConfigData.EditorFontSize.getDefaultValue());
        }else{
            return Double.parseDouble(ConfigData.EditorFontSize.getDefaultValue());
        }
    }
    public void setEditorFontSize(String fontSize){
        conf.setProperty(ConfigData.EditorFontSize.getKey(), fontSize);
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

    public Properties getProps() {
        return props;
    }
}
