package com.zds.zw.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.zw.MainApp;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration class for Zest Writer App
 */
public class Configuration {
    private Properties conf;
    private Properties actions;
    private File confFile;
    private File actionFile;
    private StorageSaver offlineSaver;
    private StorageSaver onlineSaver;
    private LocalDirectoryFactory workspaceFactory;
    private Properties props;
    private static ResourceBundle bundle;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Class constructor
     * @param homeDir Absolute path of home directory
     */
    public Configuration(String homeDir) {
        String appName = "zestwriter";
        String confDirPath = homeDir + File.separator + "." + appName;
        File confDir = new File(confDirPath);
        if(!confDir.exists()){
            if(!confDir.mkdir()) {
                log.error("Le répertoire de configuration n'a pas pu être crée");
            } else {
                log.info("Le répertoire de configuration a été crée avec succès");
            }
        }

        initConf(confDirPath);
        initActions();
        try {
            bundle = ResourceBundle.getBundle("com/zds/zw/locales/ui", Lang.getLangFromCode(getDisplayLang()).getLocale());
        }catch(Exception e) {
            bundle = ResourceBundle.getBundle("com/zds/zw/locales/ui", Locale.FRANCE);
            log.error("Impossible de charger la langue "+getDisplayLang(), e);
        }
    }

    private static String getDefaultWorkspace() {
        JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        return fw.getDefaultDirectory().getAbsolutePath() + File.separator + "zwriter-workspace";
    }

    public static String getLastRelease() throws IOException {
        String projectUrlRelease = "https://api.github.com/repos/firm1/zest-writer/releases/latest";

        String json = Request.Get(projectUrlRelease).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        Map map = mapper.readValue(json, Map.class);
        if(map.containsKey("tag_name")) {
            return (String) map.get("tag_name");
        }
        return null;
    }

    private void initActions() {
        actions = new Properties();

        String actionFileName = "action.properties";
        actionFile = new File(getWorkspacePath(), actionFileName);

        if(!actionFile.exists()) {
            log.debug("le fichier des actions "+actionFile.getAbsolutePath()+" n'existe pas");
            saveActionFile();
        }
        else {
            try {
                actions.load(new FileInputStream(actionFile));
            } catch (IOException e) {
                log.error("Impossible de charger le fichier d'actions", e);
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
            InputStream configReader = MainApp.class.getResourceAsStream("config.properties");
            if (configReader != null) {
                props.load(configReader);
            } else {
                log.error("Impossible de charger le fichier config.properties");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        conf = new Properties(props);

        if(!confFile.exists()) {
            log.debug("le fichier de configuration "+confFile.getAbsolutePath()+" n'existe pas");
            setWorkspacePath(Configuration.getDefaultWorkspace());
            saveConfFile();
        }
        else {
            try {
                conf.load(new FileInputStream(confFile));
                loadWorkspace();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
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
            log.info("Fichier de configuration enregistré dans "+confFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Impossible de sauvegarder le fichier de configuration", e);
        }
    }

    private void saveActionFile() {
        try {
            actions.store(new FileOutputStream(actionFile), "");
            log.info("Fichier d'actions enregistré");
        } catch (IOException e) {
            log.error("Impossible de sauvegarder le fichier d'actions", e);
        }
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

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public void loadWorkspace() {

        this.workspaceFactory = new LocalDirectoryFactory(getWorkspacePath());

        offlineSaver = workspaceFactory.getOfflineSaver();
        onlineSaver = workspaceFactory.getOnlineSaver();
        log.info("Espace de travail chargé en mémoire");
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
        return getGenericDoubleDisplay(ConfigData.DISPLAY_WINDOW_WIDTH);
    }

    public void setDisplayWindowWidth(String windowWidth){
        conf.setProperty(ConfigData.DISPLAY_WINDOW_WIDTH.getKey(), windowWidth);
    }

    public double getDisplayWindowHeight(){
        return getGenericDoubleDisplay(ConfigData.DISPLAY_WINDOW_HEIGHT);
    }

    public void setDisplayWindowHeight(String windowWidth){
        conf.setProperty(ConfigData.DISPLAY_WINDOW_HEIGHT.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionX(){
        return getGenericDoubleDisplay(ConfigData.DISPLAY_WINDOW_POSITION_X);
    }

    public void setDisplayWindowPositionX(String windowWidth){
        conf.setProperty(ConfigData.DISPLAY_WINDOW_POSITION_X.getKey(), windowWidth);
    }

    public double getDisplayWindowPositionY(){
        return getGenericDoubleDisplay(ConfigData.DISPLAY_WINDOW_POSITION_Y);
    }

    public void setDisplayWindowPositionY(String windowWidth){
        conf.setProperty(ConfigData.DISPLAY_WINDOW_POSITION_Y.getKey(), windowWidth);
    }

    /*
     * Zest-Writer options
     */
    public String getWorkspacePath(){
        String workspacePath = conf.getProperty(ConfigData.WORKSPACE_PATH.getKey());

        if(workspacePath != null && !workspacePath.isEmpty())
            return workspacePath;
        else
            return Configuration.getDefaultWorkspace();
    }

    public void setWorkspacePath(String path){
        conf.setProperty(ConfigData.WORKSPACE_PATH.getKey(), path);
        initActions();
    }

    public String getContentsPath(){
        if(conf.containsKey(ConfigData.CONTENTS_PATH.getKey()))
            return conf.getProperty(ConfigData.CONTENTS_PATH.getKey());
        else
            return getWorkspacePath() + "/contents";
    }

    public void setContentsPath(String path){
        conf.setProperty(ConfigData.CONTENTS_PATH.getKey(), path);
    }

    public boolean isEditorSmart(){
        return getGenericBooleanDisplay(ConfigData.EDITOR_SMART);
    }

    public void setEditorSmart(String smart){
        conf.setProperty(ConfigData.EDITOR_SMART.getKey(), smart);
    }

    public String getEditorFont(){
        if(conf.containsKey(ConfigData.EDITOR_FONT.getKey()))
            return conf.getProperty(ConfigData.EDITOR_FONT.getKey());
        else
            return ConfigData.EDITOR_FONT.getDefaultValue();
    }

    public void setEditorFont(String font){
        conf.setProperty(ConfigData.EDITOR_FONT.getKey(), font);
    }

    public int getEditorFontsize(){
        return (int) Math.round(getGenericDoubleDisplay(ConfigData.EDITOR_FONT_SIZE));
    }

    public void setEditorFontSize(String fontSize){
        conf.setProperty(ConfigData.EDITOR_FONT_SIZE.getKey(), fontSize);
    }

    public boolean isEditorToolbarView(){
        return getGenericBooleanDisplay(ConfigData.EDITOR_TOOLBAR_VIEW);
    }

    public void setEditorToolbarView(boolean view){
        conf.setProperty(ConfigData.EDITOR_TOOLBAR_VIEW.getKey(), String.valueOf(view));
    }

    public boolean isEditorLinenoView(){
        return getGenericBooleanDisplay(ConfigData.EDITOR_LINE_NO_VIEW);
    }

    public void setEditorLinenoView(boolean view){
        conf.setProperty(ConfigData.EDITOR_LINE_NO_VIEW.getKey(), String.valueOf(view));
    }

    public boolean isEditorRenderView(){
        return getGenericBooleanDisplay(ConfigData.EDITOR_RENDER_VIEW);
    }

    public void setEditorRenderView(boolean view){
        conf.setProperty(ConfigData.EDITOR_RENDER_VIEW.getKey(), String.valueOf(view));
    }

    public String getDisplayTheme(){
        if(conf.containsKey(ConfigData.DISPLAY_THEME.getKey())) {
            if (Theme.getThemeFromFileName(conf.getProperty(ConfigData.DISPLAY_THEME.getKey())) != null) {
                return conf.getProperty(ConfigData.DISPLAY_THEME.getKey());
            }
        }
        return ConfigData.DISPLAY_THEME.getDefaultValue();
    }

    public String getDisplayLang(){
        if(conf.containsKey(ConfigData.DISPLAY_LANG.getKey()))
            return conf.getProperty(ConfigData.DISPLAY_LANG.getKey());
        else
            return ConfigData.DISPLAY_LANG.getDefaultValue();
    }

    public String getWritingLicense(){
        if(conf.containsKey(ConfigData.WRITING_LICENSE.getKey()))
            return conf.getProperty(ConfigData.WRITING_LICENSE.getKey());
        else
            return ConfigData.WRITING_LICENSE.getDefaultValue();
    }

    public void setDisplayTheme(String displayTheme){
        conf.setProperty(ConfigData.DISPLAY_THEME.getKey(), displayTheme);
    }

    public void setDisplayLang(String displayLang){
        conf.setProperty(ConfigData.DISPLAY_LANG.getKey(), displayLang);
    }

    public void setWritingLicense(String license){
        conf.setProperty(ConfigData.WRITING_LICENSE.getKey(), license);
    }

    public boolean isDisplayWindowPersonnalDimension(){
        return getGenericBooleanDisplay(ConfigData.DISPLAY_WINDOW_PERSONAL_DIMENSION);
    }

    public void setDisplayWindowStandardDimension(String standardDimension){
        conf.setProperty(ConfigData.DISPLAY_WINDOW_PERSONAL_DIMENSION.getKey(), standardDimension);
    }

    public boolean isDisplayWindowPersonnalPosition(){
        return getGenericBooleanDisplay(ConfigData.DISPLAY_WINDOW_PERSONAL_POSITION);
    }

    public void setDisplayWindowPersonnalPosition(String standardPosition){
        conf.setProperty(ConfigData.DISPLAY_WINDOW_PERSONAL_POSITION.getKey(), standardPosition);
    }

    public boolean isDisplayWindowMaximize(){
        return getGenericBooleanDisplay(ConfigData.DISPLAY_WINDOW_MAXIMIZE);
    }

    public void setDisplayWindowMaximize(String maximize){
        conf.setProperty(ConfigData.DISPLAY_WINDOW_MAXIMIZE.getKey(), maximize);
    }

    public String getAuthentificationUsername(){
        if(conf.containsKey(ConfigData.AUTHENTICATION_USERNAME.getKey()))
            return conf.getProperty(ConfigData.AUTHENTICATION_USERNAME.getKey());
        else
            return ConfigData.AUTHENTICATION_USERNAME.getDefaultValue();
    }

    public void setAuthentificationUsername(String username){
        conf.setProperty(ConfigData.AUTHENTICATION_USERNAME.getKey(), username);
    }

    public String getAuthentificationPassword(){
        if(conf.containsKey(ConfigData.AUTHENTICATION_PASSWORD.getKey()))
            return conf.getProperty(ConfigData.AUTHENTICATION_PASSWORD.getKey());
        else
            return ConfigData.AUTHENTICATION_PASSWORD.getDefaultValue();
    }

    public void setAuthentificationPassword(String password){
        conf.setProperty(ConfigData.AUTHENTICATION_PASSWORD.getKey(), password);
    }

    public String getAdvancedApiServerProtocol(){
        if(conf.containsKey(ConfigData.ADVANCED_API_SERVER_PROTOCOL.getKey()))
            return conf.getProperty(ConfigData.ADVANCED_API_SERVER_PROTOCOL.getKey());
        else
            return ConfigData.ADVANCED_API_SERVER_PROTOCOL.getDefaultValue();
    }

    public void setAdvancedApiServerProtocol(String protocol){
        conf.setProperty(ConfigData.ADVANCED_API_SERVER_PROTOCOL.getKey(), protocol);
    }

    public String getAdvancedApiServerHost(){
        if(conf.containsKey(ConfigData.ADVANCED_API_SERVER_HOST.getKey()))
            return conf.getProperty(ConfigData.ADVANCED_API_SERVER_HOST.getKey());
        else
            return ConfigData.ADVANCED_API_SERVER_HOST.getDefaultValue();
    }

    public void setAdvancedApiServerHost(String host){
        conf.setProperty(ConfigData.ADVANCED_API_SERVER_HOST.getKey(), host);
    }

    public String getAdvancedApiServerPort(){
        if(conf.containsKey(ConfigData.ADVANCED_API_SERVER_PORT.getKey()))
            return conf.getProperty(ConfigData.ADVANCED_API_SERVER_PORT.getKey());
        else
            return ConfigData.ADVANCED_API_SERVER_PORT.getDefaultValue();
    }

    public void setAdvancedApiServerPort(String port){
        conf.setProperty(ConfigData.ADVANCED_API_SERVER_PORT.getKey(), port);
    }

    public String getAdvancedServerProtocol(){
        if(conf.containsKey(ConfigData.ADVANCED_SERVER_PROTOCOL.getKey()))
            return conf.getProperty(ConfigData.ADVANCED_SERVER_PROTOCOL.getKey());
        else
            return ConfigData.ADVANCED_SERVER_PROTOCOL.getDefaultValue();
    }

    public void setAdvancedServerProtocol(String protocol){
        conf.setProperty(ConfigData.ADVANCED_SERVER_PROTOCOL.getKey(), protocol);
    }

    public String getAdvancedServerHost(){
        if(conf.containsKey(ConfigData.ADVANCED_SERVER_HOST.getKey()))
            return conf.getProperty(ConfigData.ADVANCED_SERVER_HOST.getKey());
        else
            return ConfigData.ADVANCED_SERVER_HOST.getDefaultValue();
    }

    public void setAdvancedServerHost(String host){
        conf.setProperty(ConfigData.ADVANCED_SERVER_HOST.getKey(), host);
    }

    public String getAdvancedServerPort(){
        if(conf.containsKey(ConfigData.ADVANCED_SERVER_PORT.getKey())) {
            return conf.getProperty(ConfigData.ADVANCED_SERVER_PORT.getKey());
        }
        else {
            return ConfigData.ADVANCED_SERVER_PORT.getDefaultValue();
        }
    }

    public void setAdvancedServerPandoc(String host){
        conf.setProperty(ConfigData.ADVANCED_SERVER_PANDOC.getKey(), host);
    }

    public String getAdvancedServerPandoc(){
        if(conf.containsKey(ConfigData.ADVANCED_SERVER_PANDOC.getKey())) {
            return conf.getProperty(ConfigData.ADVANCED_SERVER_PANDOC.getKey());
        }
        else {
            return ConfigData.ADVANCED_SERVER_PANDOC.getDefaultValue();
        }
    }

    public void setAdvancedServerPort(String port){
        conf.setProperty(ConfigData.ADVANCED_SERVER_PORT.getKey(), port);
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
        String value = actions.getProperty(ActionData.LAST_PROJECTS.getKey());
        if(value != null && ! "".equals(value.trim())) {
            return Arrays.asList(value.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Add new content config files
     * @param projectFileName file path of content you want to add
     */
    public void addActionProject(String projectFileName) {
        List<String> existing = getActions();
        List<String> recent = new ArrayList<>(existing);
        if(recent.contains(projectFileName)) {
            recent.remove(projectFileName);
        }
        recent.add(0, projectFileName);

        actions.put(ActionData.LAST_PROJECTS.getKey(), recent.stream().limit(5).map(Object::toString).collect(Collectors.joining(",")));
        saveActionFile();
    }

    /**
     * Remove content on config files
     * @param projectFileName file path of content you want to remove
     */
    public void delActionProject(String projectFileName) {
        List<String> existing = getActions();
        List<String> recent = new ArrayList<>(existing);
        if(recent.contains(projectFileName)) {
            recent.remove(projectFileName);
        }
        actions.put(ActionData.LAST_PROJECTS.getKey(), recent.stream().limit(5).map(Object::toString).collect(Collectors.joining(",")));
        saveActionFile();
    }

    /**
     * Enum for manage last contents open
     */
    public enum ActionData{
        LAST_PROJECTS("content.open", "");
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
        DISPLAY_WINDOW_WIDTH("data.display.window.width", "1000"),
        DISPLAY_WINDOW_HEIGHT("data.display.window.height", "600"),
        DISPLAY_WINDOW_POSITION_X("data.display.window.position.x", "0"),
        DISPLAY_WINDOW_POSITION_Y("data.display.window.position.y", "0"),

        WORKSPACE_PATH("options.workspace.path", ""),
        CONTENTS_PATH("options.workspace.contents.path", ""),
        EDITOR_SMART("options.editor.smart", "true"),
        EDITOR_FONT("options.editor.font", "Fira Mono"),
        EDITOR_FONT_SIZE("options.editor.fontSize", "14"),
        EDITOR_TOOLBAR_VIEW("options.editor.toolbar.view", "true"),
        EDITOR_LINE_NO_VIEW("options.editor.lineno.view", "true"),
        EDITOR_RENDER_VIEW("options.editor.render.view", "true"),
        DISPLAY_THEME("options.display.theme", "light.css"),
        DISPLAY_LANG("options.display.lang", Locale.FRANCE.toString()),
        WRITING_LICENSE("options.writing.license", "Tous droits réservés"),
        DISPLAY_WINDOW_PERSONAL_DIMENSION("options.display.window.standardDimension", "true"),
        DISPLAY_WINDOW_PERSONAL_POSITION("options.display.window.standardPosition", "true"),
        DISPLAY_WINDOW_MAXIMIZE("options.display.window.maximize", "false"),
        AUTHENTICATION_USERNAME("options.authentification.username", ""),
        AUTHENTICATION_PASSWORD("options.authentification.password", ""),
        ADVANCED_API_SERVER_PROTOCOL("options.advanced.protocol", "http"),
        ADVANCED_API_SERVER_HOST("options.advanced.host", "winxaito.com"),
        ADVANCED_API_SERVER_URI("options.advanced.host", "api"),
        ADVANCED_API_SERVER_PORT("options.advanced.port", "80"),
        ADVANCED_SERVER_PROTOCOL("server.protocol", "https"),
        ADVANCED_SERVER_HOST("server.host", "zestedesavoir.com"),
        ADVANCED_SERVER_PORT("server.port", "443"),
        ADVANCED_SERVER_PANDOC("server.pandoc.url", "http:/"+"/firm1.eu/2pdf/");

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
