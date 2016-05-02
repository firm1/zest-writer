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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;

public class Configuration {
    private Properties conf;
    private String appName = "zestwriter";
    private String confFileName = "conf.properties";
    private String confDirPath;
    private File confFile;
    private StorageSaver offlineSaver;
    private StorageSaver onlineSaver;
    private LocalDirectoryFactory workspaceFactory;
    private final Logger logger;
    private Properties props;


    public enum Options{
        WorkspacePath("options.workspace.path", ""),
        EditorSmart("options.editor.smart", "false"),
        EditorFont("options.editor.font", "Fira Mono"),
        EditorFontSize("options.editor.fontSize", "14"),
        EditorToolbarView("options.editor.toolbar.view", "yes"),
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
        confDirPath = homeDir+File.separator+"."+this.appName;
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

    public String getLastRelease() throws ClientProtocolException, IOException {
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
     * Zest-Writer options
     */
    public String getWorkspacePath(){
        if(conf.containsKey(Options.WorkspacePath.getKey()))
            return conf.getProperty(Options.WorkspacePath.getKey());
        else
            return Configuration.getDefaultWorkspace();
    }

    public void setWorkspacePath(String font){
        conf.setProperty(Options.WorkspacePath.getKey(), font);
    }

    public Boolean getEditorSmart(){
        if(conf.containsKey(Options.EditorSmart.getKey())){
            if(NumberUtils.isNumber(conf.getProperty(Options.EditorSmart.getKey())))
                return Boolean.parseBoolean(conf.getProperty(Options.EditorSmart.getKey()));
            else
                return Boolean.parseBoolean(Options.EditorSmart.getDefaultValue());
        }else{
            return Boolean.parseBoolean(Options.EditorSmart.getDefaultValue());
        }
    }

    public void isEditorSmart(String editorSmart){
        conf.setProperty(Options.EditorSmart.getKey(), editorSmart);
    }

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

    public String getEditorToolbarView(){
        if(conf.containsKey(Options.EditorToolbarView.getKey()))
            return conf.getProperty(Options.EditorToolbarView.getKey());
        else
            return Options.DisplayTheme.getDefaultValue();
    }
    public void setEditorToolbarView(String view){
        if(!view.toLowerCase().equals("yes") && !view.toLowerCase().equals("no"))
            view = Options.EditorToolbarView.getDefaultValue();

        conf.setProperty(Options.EditorToolbarView.getKey(), view);
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
        conf.setProperty(Options.AdvancedServerPort.getKey(), port);
    }

    public void resetAuthentification(){
        setAuthentificationUsername("");
        setAuthentificationPassword("");
        saveConfFile();
    }

    public void resetAllOptions(){
        for(Options opt : Options.values()){
            conf.setProperty(opt.getKey(), opt.getDefaultValue());
        }

        saveConfFile();
    }

    public Properties getProps() {
        return props;
    }
}
