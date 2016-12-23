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
import java.util.*;
import java.util.stream.Collectors;

public class Configuration {
    private final Logger logger;
    private Properties conf;
    private Properties actions;
    private File confFile;
    private File actionFile;
    private StorageSaver offlineSaver;
    private StorageSaver onlineSaver;
    private LocalDirectoryFactory workspaceFactory;
    private Properties props;
    public static ResourceBundle bundle;

    public Configuration(String homeDir) {
        logger = LoggerFactory.getLogger(Configuration.class);
        String appName = "zestwriter";
        String confDirPath = homeDir + File.separator + "." + appName;
        File confDir = new File(confDirPath);
        if(!confDir.exists()){
            if(!confDir.mkdir())
                logger.error("Le fichier de configuration n'a pas pu être créé");
        }

        initConf(confDirPath);
        initActions(confDirPath);
        try {
            bundle = ResourceBundle.getBundle("locales/ui", Lang.getLangFromCode(getDisplayLang()).getLocale());
        }catch(Exception e) {
            bundle = ResourceBundle.getBundle("locales/ui", Locale.FRANCE);
            logger.error("Impossible de charger la langue "+getDisplayLang());
        }
    }

    public static String getDefaultWorkspace() {
        JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        return fw.getDefaultDirectory().getAbsolutePath() + File.separator + "zwriter-workspace";
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

    private void initActions(String confDirPath) {
        actions = new Properties();

        String actionFileName = "action.properties";
        String actionFilePath = confDirPath+File.separator+ actionFileName;
        actionFile = new File(actionFilePath);

        if(!actionFile.exists()) {
            logger.debug("le fichier des actions "+actionFile.getAbsolutePath()+" n'existe pas");
            saveActionFile();
        }
        else {
            try {
                actions.load(new FileInputStream(actionFile));
            } catch (IOException e) {
                logger.error("Impossible de charger le fichier d'actions", e);
            }
        }
    }

    private void initConf(String confDirPath) {
        String confFileName = "conf.properties";
        String confFilePath = confDirPath+File.separator+ confFileName;
        confFile = new File(confFilePath);
        // defaults config
        props = new Properties();
        try {
            props.load(MainApp.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            logger.error("", e);
        }

        conf = new Properties(props);

        if(!confFile.exists()) {
            logger.debug("le fichier de configuration "+confFile.getAbsolutePath()+" n'existe pas");
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
            logger.info("Fichier de configuration enregistré dans "+confFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Impossible de sauvegarder le fichier de configuration", e);
        }
    }

    public void saveActionFile() {
        try {
            actions.store(new FileOutputStream(actionFile), "");
            logger.info("Fichier d'actions enregistré");
        } catch (IOException e) {
            logger.error("Impossible de sauvegarder le fichier d'actions", e);
        }
    }

    public String getPandocProvider() {
        return "http://firm1.eu/2pdf/";
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
        return getGenericDoubleDisplay(ConfigData.displayWindowWidth);
    }

    public void setDisplayWindowWidth(String windowWidth){
        conf.setProperty(ConfigData.displayWindowWidth.getKey(), windowWidth);
    }

    public double getDisplayWindowHeight(){
        return getGenericDoubleDisplay(ConfigData.displayWindowHeight);
    }

    public void setDisplayWindowHeight(String windowWidth){
        conf.setProperty(ConfigData.displayWindowHeight.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionX(){
        return getGenericDoubleDisplay(ConfigData.displayWindowPositionX);
    }

    public void setDisplayWindowPositionX(String windowWidth){
        conf.setProperty(ConfigData.displayWindowPositionX.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionY(){
        return getGenericDoubleDisplay(ConfigData.displayWindowPositionY);
    }

    public void setDisplayWindowPositionY(String windowWidth){
        conf.setProperty(ConfigData.displayWindowPositionY.getKey(), windowWidth);
    }

    /*
     * Zest-Writer options
     */
    public String getWorkspacePath(){
        String workspacePath = conf.getProperty(ConfigData.workspacePath.getKey());

        if(workspacePath != null && !workspacePath.isEmpty())
            return workspacePath;
        else
            return Configuration.getDefaultWorkspace();
    }

    public void setWorkspacePath(String path){
        conf.setProperty(ConfigData.workspacePath.getKey(), path);
    }

    public String getContentsPath(){
        if(conf.containsKey(ConfigData.contentsPath.getKey()))
            return conf.getProperty(ConfigData.contentsPath.getKey());
        else
            return getWorkspacePath() + "/contents";
    }

    public void setContentsPath(String path){
        conf.setProperty(ConfigData.contentsPath.getKey(), path);
    }

    public boolean isEditorSmart(){
        return getGenericBooleanDisplay(ConfigData.editorSmart);
    }

    public void setEditorSmart(String smart){
        conf.setProperty(ConfigData.editorSmart.getKey(), smart);
    }

    public String getEditorFont(){
        if(conf.containsKey(ConfigData.editorFont.getKey()))
            return conf.getProperty(ConfigData.editorFont.getKey());
        else
            return ConfigData.editorFont.getDefaultValue();
    }

    public void setEditorFont(String font){
        conf.setProperty(ConfigData.editorFont.getKey(), font);
    }

    public int getEditorFontsize(){
        return (int) Math.round(getGenericDoubleDisplay(ConfigData.editorFontSize));
    }

    public void setEditorFontSize(String fontSize){
        conf.setProperty(ConfigData.editorFontSize.getKey(), fontSize);
    }

    public boolean isEditorToolbarView(){
        return getGenericBooleanDisplay(ConfigData.editorToolbarView);
    }

    public void setEditorToolbarView(boolean view){
        conf.setProperty(ConfigData.editorToolbarView.getKey(), String.valueOf(view));
    }

    public boolean isEditorLinenoView(){
        return getGenericBooleanDisplay(ConfigData.editorLineNoView);
    }

    public void setEditorLinenoView(boolean view){
        conf.setProperty(ConfigData.editorLineNoView.getKey(), String.valueOf(view));
    }

    public boolean isEditorRenderView(){
        return getGenericBooleanDisplay(ConfigData.editorRenderView);
    }

    public void setEditorRenderView(boolean view){
        conf.setProperty(ConfigData.editorRenderView.getKey(), String.valueOf(view));
    }

    public String getDisplayTheme(){
        if(conf.containsKey(ConfigData.displayTheme.getKey())) {
            if (Theme.getThemeFromFileName(conf.getProperty(ConfigData.displayTheme.getKey())) != null) {
                return conf.getProperty(ConfigData.displayTheme.getKey());
            }
        }
        return ConfigData.displayTheme.getDefaultValue();
    }

    public String getDisplayLang(){
        if(conf.containsKey(ConfigData.displayLang.getKey()))
            return conf.getProperty(ConfigData.displayLang.getKey());
        else
            return ConfigData.displayLang.getDefaultValue();
    }

    public void setDisplayTheme(String displayTheme){
        conf.setProperty(ConfigData.displayTheme.getKey(), displayTheme);
    }

    public void setDisplayLang(String displayLang){
        conf.setProperty(ConfigData.displayLang.getKey(), displayLang);
    }

    public boolean isDisplayWindowPersonnalDimension(){
        return getGenericBooleanDisplay(ConfigData.displayWindowPersonnalDimension);
    }

    public void setDisplayWindowStandardDimension(String standardDimension){
        conf.setProperty(ConfigData.displayWindowPersonnalDimension.getKey(), standardDimension);
    }

    public boolean isDisplayWindowPersonnalPosition(){
        return getGenericBooleanDisplay(ConfigData.displayWindowPersonnalPosition);
    }

    public void setDisplayWindowPersonnalPosition(String standardPosition){
        conf.setProperty(ConfigData.displayWindowPersonnalPosition.getKey(), standardPosition);
    }

    public boolean isDisplayWindowMaximize(){
        return getGenericBooleanDisplay(ConfigData.displayWindowMaximize);
    }

    public void setDisplayWindowMaximize(String maximize){
        conf.setProperty(ConfigData.displayWindowMaximize.getKey(), maximize);
    }

    public String getAuthentificationUsername(){
        if(conf.containsKey(ConfigData.authentificationUsername.getKey()))
            return conf.getProperty(ConfigData.authentificationUsername.getKey());
        else
            return ConfigData.authentificationUsername.getDefaultValue();
    }

    public void setAuthentificationUsername(String username){
        conf.setProperty(ConfigData.authentificationUsername.getKey(), username);
    }

    public String getAuthentificationPassword(){
        if(conf.containsKey(ConfigData.authentificationPassword.getKey()))
            return conf.getProperty(ConfigData.authentificationPassword.getKey());
        else
            return ConfigData.authentificationPassword.getDefaultValue();
    }

    public void setAuthentificationPassword(String password){
        conf.setProperty(ConfigData.authentificationPassword.getKey(), password);
    }

    public String getAdvancedApiServerProtocol(){
        if(conf.containsKey(ConfigData.advancedApiServerProtocol.getKey()))
            return conf.getProperty(ConfigData.advancedApiServerProtocol.getKey());
        else
            return ConfigData.advancedApiServerProtocol.getDefaultValue();
    }

    public void setAdvancedApiServerProtocol(String protocol){
        conf.setProperty(ConfigData.advancedApiServerProtocol.getKey(), protocol);
    }

    public String getAdvancedApiServerHost(){
        if(conf.containsKey(ConfigData.advancedApiServerHost.getKey()))
            return conf.getProperty(ConfigData.advancedApiServerHost.getKey());
        else
            return ConfigData.advancedApiServerHost.getDefaultValue();
    }

    public void setAdvancedApiServerHost(String host){
        conf.setProperty(ConfigData.advancedApiServerHost.getKey(), host);
    }

    public String getAdvancedApiServerPort(){
        if(conf.containsKey(ConfigData.advancedApiServerPort.getKey()))
            return conf.getProperty(ConfigData.advancedApiServerPort.getKey());
        else
            return ConfigData.advancedApiServerPort.getDefaultValue();
    }

    public void setAdvancedApiServerPort(String port){
        conf.setProperty(ConfigData.advancedApiServerPort.getKey(), port);
    }

    public String getAdvancedServerProtocol(){
        if(conf.containsKey(ConfigData.advancedServerProtocol.getKey()))
            return conf.getProperty(ConfigData.advancedServerProtocol.getKey());
        else
            return ConfigData.advancedServerProtocol.getDefaultValue();
    }

    public void setAdvancedServerProtocol(String protocol){
        conf.setProperty(ConfigData.advancedServerProtocol.getKey(), protocol);
    }

    public String getAdvancedServerHost(){
        if(conf.containsKey(ConfigData.advancedServerHost.getKey()))
            return conf.getProperty(ConfigData.advancedServerHost.getKey());
        else
            return ConfigData.advancedServerHost.getDefaultValue();
    }

    public void setAdvancedServerHost(String host){
        conf.setProperty(ConfigData.advancedServerHost.getKey(), host);
    }

    public String getAdvancedServerPort(){
        if(conf.containsKey(ConfigData.advancedServerPort.getKey())) {
            return conf.getProperty(ConfigData.advancedServerPort.getKey());
        }
        else {
            return ConfigData.advancedServerPort.getDefaultValue();
        }
    }

    public void setAdvancedServerPort(String port){
        conf.setProperty(ConfigData.advancedServerPort.getKey(), port);
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

    public List<String> getActions() {
        String value = actions.getProperty(ActionData.lastProjects.getKey());
        if(value != null && ! "".equals(value.trim())) {
            return Arrays.asList(value.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    public void addActionProject(String projectFileName) {
        List<String> existant = getActions();
        List<String> recents = new ArrayList<>(existant);
        if(recents.contains(projectFileName)) {
            recents.remove(projectFileName);
        }
        recents.add(0, projectFileName);

        actions.put(ActionData.lastProjects.getKey(), recents.stream().limit(5).map(Object::toString).collect(Collectors.joining(",")));
        saveActionFile();
    }

    public void delActionProject(String projectFileName) {
        List<String> existant = getActions();
        List<String> recents = new ArrayList<>(existant);
        if(recents.contains(projectFileName)) {
            recents.remove(projectFileName);
        }
        actions.put(ActionData.lastProjects.getKey(), recents.stream().limit(5).map(Object::toString).collect(Collectors.joining(",")));
        saveActionFile();
    }

    public enum ActionData{
        lastProjects("content.open", "");
        private String key;
        private String defaultValue;

        ActionData(String key, String defaultValue){
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
        displayWindowWidth("data.display.window.width", "1000"),
        displayWindowHeight("data.display.window.height", "600"),
        displayWindowPositionX("data.display.window.position.x", "0"),
        displayWindowPositionY("data.display.window.position.y", "0"),

        workspacePath("options.workspace.path", ""),
        contentsPath("options.workspace.contents.path", ""),
        editorSmart("options.editor.smart", "true"),
        editorFont("options.editor.font", "Fira Mono"),
        editorFontSize("options.editor.fontSize", "14"),
        editorToolbarView("options.editor.toolbar.view", "true"),
        editorLineNoView("options.editor.lineno.view", "true"),
        editorRenderView("options.editor.render.view", "true"),
        displayTheme("options.display.theme", "light.css"),
        displayLang("options.display.lang", Locale.FRANCE.toString()),
        displayWindowPersonnalDimension("options.display.window.standardDimension", "true"),
        displayWindowPersonnalPosition("options.display.window.standardPosition", "true"),
        displayWindowMaximize("options.display.window.maximize", "false"),
        authentificationUsername("options.authentification.username", ""),
        authentificationPassword("options.authentification.password", ""),
        advancedApiServerProtocol("options.advanced.protocol", "http"),
        advancedApiServerHost("options.advanced.host", "winxaito.com"),
        advancedApiServerUri("options.advanced.host", "api"),
        advancedApiServerPort("options.advanced.port", "80"),
        advancedServerProtocol("server.protocol", "https"),
        advancedServerHost("server.host", "zestedesavoir.com"),
        advancedServerPort("server.port", "443");

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
}
