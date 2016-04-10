package com.zestedesavoir.zestwriter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final static String WORKSPACE_KEY = "data.workspace";
    private final static String SMART_EDITOR_KEY = "editor.smart";
    private final static String SERVER_PROTOCOL_KEY = "server.protocol";
    private final static String SERVER_HOST_KEY = "server.host";
    private final static String SERVER_PORT_KEY = "server.port";


    public enum Options{
        EditorFont("options.editor.font", "Arial"),
        EditorFontSize("options.editor.fontSize", "14"),
        DisplayTheme("options.display.theme", "Standard"),
        AuthentificationUsername("options.authentification.username", ""),
        AuthentificationPassword("options.authentification.password", ""),
        AdvancedServerProtocol("options.advanced.protocol", "https"),
        AdvancedServerHost("options.advanced.host", "zestedesavoir.com"),
        AdvancedServerPort("options.advanced.port", "80");

        private String key;
        private String defaultValue;

        Options(String key, String defaultValue){
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
        Properties props = new Properties();
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


    /*
     * Zest-Writer options
     */
    public String getEditorFont(){
        if(conf.containsKey(Options.EditorFont.getKey()))
            return conf.getProperty(Options.EditorFont.getKey());
        else
            return Options.EditorFont.getDefaultValue();
    }

    public void setEditorFont(String font){
        conf.setProperty(Options.EditorFont.getKey(), font);
    }

    public double getEditorFontsize(){
        if(conf.containsKey(Options.EditorFontSize.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(Options.EditorFontSize.getKey())))
                return Double.parseDouble(conf.getProperty(Options.EditorFontSize.getKey()));
            else
                return Double.parseDouble(Options.EditorFontSize.getDefaultValue());
        }else{
            return Double.parseDouble(Options.EditorFontSize.getDefaultValue());
        }
    }
    public void setEditorFontSize(String fontSize){
        conf.setProperty(Options.EditorFontSize.getKey(), fontSize);
    }

    public String getDisplayTheme(){
        if(conf.containsKey(Options.DisplayTheme.getKey()))
            return conf.getProperty(Options.DisplayTheme.getKey());
        else
            return Options.DisplayTheme.getDefaultValue();
    }
    public void setDisplayTheme(String displayTheme){
        conf.setProperty(Options.DisplayTheme.getKey(), displayTheme);
    }

    public String getAuthentificationUsername(){
        if(conf.containsKey(Options.AuthentificationUsername.getKey()))
            return conf.getProperty(Options.AuthentificationUsername.getKey());
        else
            return Options.AuthentificationUsername.getDefaultValue();
    }
    public void setAuthentificationUsername(String username){
        conf.setProperty(Options.AuthentificationUsername.getKey(), username);
    }

    public String getAuthentificationPassword(){
        if(conf.containsKey(Options.AuthentificationPassword.getKey()))
            return conf.getProperty(Options.AuthentificationPassword.getKey());
        else
            return Options.AuthentificationPassword.getDefaultValue();
    }

    public void setAuthentificationPassword(String password){
        conf.setProperty(Options.AuthentificationPassword.getKey(), password);
    }

    public String getAdvancedServerProtocol(){
        if(conf.containsKey(Options.AdvancedServerProtocol.getKey()))
            return conf.getProperty(Options.AdvancedServerProtocol.getKey());
        else
            return Options.AdvancedServerProtocol.getDefaultValue();
    }

    public void setAdvancedServerProtocol(String protocol){
        conf.setProperty(Options.AdvancedServerProtocol.getKey(), protocol);
    }

    public String getAdvancedServerHost(){
        if(conf.containsKey(Options.AdvancedServerHost.getKey()))
            return conf.getProperty(Options.AdvancedServerHost.getKey());
        else
            return Options.AdvancedServerHost.getDefaultValue();
    }

    public void setAdvancedServerHost(String host){
        conf.setProperty(Options.AdvancedServerHost.getKey(), host);
    }

    public String getAdvancedServerPort(){
        if(conf.containsKey(Options.AdvancedServerPort.getKey()))
            return conf.getProperty(Options.AdvancedServerPort.getKey());
        else
            return Options.AdvancedServerPort.getDefaultValue();
    }

    public void setAdvancedServerPort(String port){
        conf.setProperty(Options.EditorFont.getKey(), port);
    }
}
