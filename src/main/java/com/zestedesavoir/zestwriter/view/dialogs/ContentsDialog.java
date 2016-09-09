package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.contents.internal.ContentsConfig;
import com.zestedesavoir.zestwriter.contents.internal.ContentsConfigDetailJson;
import com.zestedesavoir.zestwriter.contents.internal.ContentsConfigJson;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.api.*;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.CustomStage;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ContentsDialog implements ApiDownloaderListener, ApiInstallerListener{
    private Logger logger = LogManager.getLogger(ContentsDialog.class);
    private Configuration config = MainApp.getConfig();
    private Stage window;
    private Stage waitStage;
    private File tempDir;
    private File pluginsDir;
    private File themesDir;

    private ApiDownloader apiDownloader;
    private ApiInstaller apiInstaller;
    private Map<Integer, ApiContentResponse> plugins = new HashMap<>();
    private Map<Integer, ApiContentResponse> themes = new HashMap<>();
    private Map<Integer, ContentsConfigDetailJson> pluginsInstalled = new HashMap<>();
    private Map<Integer, ContentsConfigDetailJson> themesInstalled = new HashMap<>();
    private String currentPlugin;
    private String currentTheme;

    @FXML private ListView listThemes;
    @FXML private ListView listOfficialInstalledThemes;
    @FXML private ListView listUnofficialInstalledThemes;
    @FXML private ListView listPlugins;
    @FXML private ListView listOfficialInstalledPlugins;
    @FXML private ListView listUnofficialInstalledPlugins;


    public enum ContentType{
        PLUGIN,
        THEME
    }

    public void setWindow(Stage window){
        this.window = window;
    }

    @FXML private void initialize(){
        if(MainApp.getContentsConfigPlugins().isCorrupted() || MainApp.getContentsConfigThemes().isCorrupted()){
            Alert alert = new CustomAlert(Alert.AlertType.WARNING);
            alert.setHeaderText("Une erreur dans le fichier de configuration des contenus externes à été détecté");
            alert.setContentText("Il se peut que le fichier aie été corrompu ou modifier par un programme externe, " +
                    "afin de résoudre ce problème, veuillez soumettre le fichier suivant au développeur " +
                    "<" + MainApp.getContentsConfigPlugins().getConfigFile().getPath() + ">");
            alert.showAndWait();

            return;
        }

        logger.debug("Contents path: " + config.getContentsPath());
        tempDir = new File(config.getContentsPath() + "/temp/");
        pluginsDir = new File(config.getContentsPath() + "/plugins/");
        themesDir = new File(config.getContentsPath() + "/themes/");

        if(!tempDir.exists())
            if(!tempDir.mkdirs())
                logger.error("Error for create plugins directory");
        if(!pluginsDir.exists())
            if(!pluginsDir.mkdirs())
                logger.error("Error for create plugins directory");
        if(!themesDir.exists())
            if(!themesDir.mkdirs())
                logger.error("Error for create themes directory");

        loadInstalledContents();

        ApiRequester requester = new ApiRequester();
        ApiMapper mapper;

        if(!requester.isApiOk()){
            listPlugins.getItems().add("Erreur lors du contact de l'API");
            listThemes.getItems().add("Erreur lors du contact de l'API");
        }else{
            //Get plugins
            try{
                StringBuilder json = requester.request(new URL("http://zw.winxaito.com/api/plugins"), ApiRequester.RequestMethod.GET);
                mapper = new ApiMapper(json.toString());
                ApiContentsResponse pluginsContents = mapper.getContents();
                String validate = "";

                if(pluginsContents == null){
                    listPlugins.getItems().add("Aucun plugin n'a été trouvé");
                }else{
                    int i = 0;
                    for(ApiContentResponse plugin : pluginsContents.getContents()){
                        if(plugin.isOfficial())
                            continue;

                        plugins.put(i, plugin);

                        if(plugin.isValidate())
                            validate = "[VALIDE] ";
                        else
                            validate = "";

                        listPlugins.getItems().add("[" + plugin.getVersion() + "] " + validate + plugin.getName());
                        i++;
                    }
                }
            }catch(MalformedURLException e){
                logger.error(e.getMessage(), e);
            }

            //Get themes
            try{
                StringBuilder json = requester.request(new URL("http://zw.winxaito.com/api/themes"), ApiRequester.RequestMethod.GET);
                mapper = new ApiMapper(json.toString());
                ApiContentsResponse themesContents = mapper.getContents();
                String validate = "";

                if(themesContents == null){
                    listThemes.getItems().add("Aucun thème n'a été trouvé");
                }else{
                    int i = 0;
                    for(ApiContentResponse theme : themesContents.getContents()){
                        if(theme.isOfficial())
                            continue;

                        themes.put(i, theme);

                        if(theme.isValidate())
                            validate = "[VALIDE] ";
                        else
                            validate = "";

                        listThemes.getItems().add("[" + theme.getVersion() + "] " + validate + theme.getName());
                        i++;
                    }
                }
            }catch(MalformedURLException e){
                logger.error(e.getMessage(), e);
            }

            listPlugins.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> currentPlugin = (String)newValue);
            listOfficialInstalledPlugins.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> currentPlugin = (String)newValue);
            listUnofficialInstalledPlugins.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> currentPlugin = (String)newValue);

            listThemes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> currentTheme = (String)newValue);
            listOfficialInstalledThemes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> currentTheme = (String)newValue);
            listUnofficialInstalledThemes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> currentTheme = (String)newValue);
        }
    }

    @FXML private void handleListPluginInfos(){
        ApiContentResponse plugin = getPlugin();

        if(plugin == null){
            logger.error("Error for found plugin in list");
            return;
        }

        alert(Alert.AlertType.INFORMATION,
                "Informations",
                "Plugin proposé par: " + plugin.getUser().getName() + "\n" +
                        "Version: " + plugin.getVersion() + "\n" +
                        "Validé: " + (plugin.isValidate() ? "Oui" : "Non"),
                plugin.getDescription()
        );
    }

    @FXML private void handleListPluginInstall(){
        ApiContentResponse plugin = getPlugin();
        assert plugin != null;

        if(alert(
                Alert.AlertType.CONFIRMATION,
                "Installation",
                "Voulez-vous vraiment installé ce plugin ?",
                "Le plugin sera télécharger depuis ces adresses:\n  " + plugin.getDownload_url() + ".content\n  " + plugin.getDownload_url() + ".data",
                ButtonType.YES, ButtonType.NO
        ) == ButtonType.YES){
            waitStage("Téléchargement", "Téléchargement et installation en cours");

            apiDownloader = new ApiDownloader(ContentType.PLUGIN, tempDir.getPath() + "/", plugin.getDownload_url() + ".content", plugin.getDownload_url() + ".data");
            apiDownloader.addListener(this);
            apiDownloader.setContent(plugin);
        }
    }

    @FXML private void handleOfficialPluginState(){
        
    }

    @FXML private void handleUnofficialPluginState(){
        
    }

    @FXML private void handleUnofficialPluginUninstall(){
        ContentsConfigDetailJson plugin = getInstalledPlugin();
        assert plugin != null;

        if(alert(
                Alert.AlertType.CONFIRMATION,
                "Désinstallation",
                "Souhaitez-vous vraiment désinstaller ce plugin ?",
                null,
                ButtonType.YES, ButtonType.NO
        ) == ButtonType.YES){
            if(ApiInstaller.uninstall(ContentType.PLUGIN,
                    new File(MainApp.getConfig().getContentsPath() + "/plugins/" + plugin.getUrl_id() + ".jar"),
                    new File(MainApp.getConfig().getContentsPath() + "/plugins/" + plugin.getUrl_id() + ".data")
            )){
                alert(Alert.AlertType.INFORMATION, "Désinstallation", "Le plugin a bien été désinstaller", null);
                MainApp.getContentsConfigPlugins().generateIndex(ContentsConfig.ConfigType.UNOFFICIAL);
                listUnofficialInstalledPlugins.getItems().clear();
                loadInstalledContents();
                listUnofficialInstalledPlugins.refresh();
            }else{
                alert(Alert.AlertType.ERROR, "Désinstallation", "Une erreur est survenu durant la désinstallation du plugin", null);
            }
        }
    }

    @FXML private void handleListThemeInfos(){
        ApiContentResponse theme = getTheme();

        if(theme == null){
            logger.error("Error for found theme in list");
            return;
        }

        alert(Alert.AlertType.INFORMATION,
                "Informations",
                "Thème proposé par: " + theme.getUser().getName() + "\n" +
                        "Version: " + theme.getVersion() + "\n" +
                        "Validé: " + (theme.isValidate() ? "Oui" : "Non"),
                theme.getDescription()
        );
    }

    @FXML private void handleListThemeInstall(){
        ApiContentResponse theme = getTheme();
        assert theme != null;

        if(alert(
                Alert.AlertType.CONFIRMATION,
                "Installation",
                "Voulez-vous vraiment installé ce thème ?",
                "Le thème sera télécharger depuis ces adresses:\n  " + theme.getDownload_url() + ".content\n  " + theme.getDownload_url() + ".data",
                ButtonType.YES, ButtonType.NO
        ) == ButtonType.YES){
            waitStage("Installation", "Téléchargement et installation en cours");

            apiDownloader = new ApiDownloader(ContentType.THEME, tempDir.getPath() + "/", theme.getDownload_url() + ".content", theme.getDownload_url() + ".data");
            apiDownloader.addListener(this);
            apiDownloader.setContent(theme);
        }
    }

    @FXML private void handleOfficialThemeState(){

    }

    @FXML private void handleUnofficialThemeState(){

    }

    @FXML private void handleUnofficialThemeUninstall(){
        ContentsConfigDetailJson theme = getInstalledTheme();
        assert theme != null;

        if(alert(
                Alert.AlertType.CONFIRMATION,
                "Désinstallation",
                "Souhaitez-vous vraiment désinstaller ce thème ?",
                null,
                ButtonType.YES, ButtonType.NO
        ) == ButtonType.YES){
            File contentFile = new File(MainApp.getConfig().getContentsPath() + "/themes/" + theme.getUrl_id() + ".css");
            File dataFile = new File(MainApp.getConfig().getContentsPath() + "/themes/" + theme.getUrl_id() + ".data");


            if(ApiInstaller.uninstall(ContentType.THEME,
                    contentFile,
                    dataFile
            )){
                alert(Alert.AlertType.INFORMATION, "Désinstallation", "Le thème a bien été désinstaller", null);
                MainApp.getContentsConfigThemes().generateIndex(ContentsConfig.ConfigType.UNOFFICIAL);
                listUnofficialInstalledThemes.getItems().clear();
                loadInstalledContents();
                listUnofficialInstalledThemes.refresh();
            }else{
                alert(Alert.AlertType.ERROR, "Désinstallation", "Une erreur est survenu durant la désinstallation du thème", null);
            }
        }
    }

    private ApiContentResponse getPlugin(){
        String validate = "";
        for(Map.Entry<Integer, ApiContentResponse> map : plugins.entrySet()){
            validate = "";
            ApiContentResponse plugin = map.getValue();

            if(plugin.isValidate())
                validate = "[VALIDE] ";

            if(currentPlugin.equals("[" + plugin.getVersion() + "] " + validate + plugin.getName()))
                return plugin;
        }

        return null;
    }

    private ContentsConfigDetailJson getInstalledPlugin(){
        for(Map.Entry<Integer, ContentsConfigDetailJson> map : pluginsInstalled.entrySet()){
            ContentsConfigDetailJson plugin = map.getValue();

            if(currentPlugin.equals("[" + plugin.getVersion() + "] " + plugin.getName()))
                return plugin;
        }

        return null;
    }

    private ApiContentResponse getTheme(){
        String validate = "";
        for(Map.Entry<Integer, ApiContentResponse> map : themes.entrySet()){
            validate = "";
            ApiContentResponse theme = map.getValue();

            if(theme.isValidate())
                validate = "[VALIDE] ";

            if(currentTheme.equals("[" + theme.getVersion() + "] " + validate + theme.getName()))
                return theme;
        }

        return null;
    }

    private ContentsConfigDetailJson getInstalledTheme(){
        logger.debug(currentTheme + " -- " + themesInstalled.size());
        for(Map.Entry<Integer, ContentsConfigDetailJson> map : themesInstalled.entrySet()){
            ContentsConfigDetailJson theme = map.getValue();

            if(currentTheme.equals("[" + theme.getVersion() + "] " + theme.getName()))
                return theme;
        }

        return null;
    }

    private ButtonType alert(Alert.AlertType type, String title, String header, String content, ButtonType... buttonType){
        Alert a = new CustomAlert(type);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.getButtonTypes().setAll(buttonType);
        a.initOwner(window);
        a.showAndWait();

        return a.getResult();
    }

    private ButtonType alert(Alert.AlertType type, String title, String header, String content){
        return (alert(type, title, header, content, ButtonType.OK, ButtonType.CANCEL));
    }

    private void waitStage(String title, String label){
        waitStage = new CustomStage(title);
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 350, 150);
        FunctionTreeFactory.addTheming(root);
        waitStage.setScene(scene);
        waitStage.setResizable(false);
        waitStage.initOwner(window);
        waitStage.initModality(Modality.APPLICATION_MODAL);
        waitStage.setOnCloseRequest(Event::consume);

        Label lb = new Label(label);
        lb.setFont(new Font(lb.getFont().getName(), 18));

        ProgressIndicator pin = new ProgressIndicator();
        pin.setProgress(- 1);

        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(15);
        vb.getChildren().addAll(lb, pin);

        scene.setRoot(vb);
        waitStage.show();
    }

    private void errorAlert(){
        waitStage.hide();
        waitStage.close();

        alert(Alert.AlertType.ERROR,
                "Erreur",
                "Une erreur est survenu lors du téléchargement",
                "Détail supplémentaire"
        );
    }

    private void successAlert(){
        waitStage.close();

        alert(Alert.AlertType.INFORMATION,
                "Succès",
                "Le plugin a été télécharger et installer avec succès",
                "Le Plugin sera fonctionnel au prochain démarrage de l'application"
        );
    }

    private void loadInstalledContents(){
        int i = 0;
        pluginsInstalled.clear();
        themesInstalled.clear();

        //Load installed plugins
        ContentsConfigJson pluginsJson = MainApp.getContentsConfigPlugins().getConfigJson();

        for(ContentsConfigDetailJson detailJson : pluginsJson.getContents()){
            //listUnofficialInstalledPlugins.getItems().add("[" + detailJson.getVersion() + "] " + detailJson.getName());
            pluginsInstalled.put(i, detailJson);
            i++;
        }

        //Load installed themes
        ContentsConfigJson themesJson = MainApp.getContentsConfigThemes().getConfigJson();

        i = 0;
        for(ContentsConfigDetailJson detailJson : themesJson.getContents()){
            Platform.runLater(() -> listUnofficialInstalledThemes.getItems().add("[" + detailJson.getVersion() + "] " + detailJson.getName()));
            logger.debug("PUT In themesInstalled : " + i + " -- [" + detailJson.getVersion() + "] " + detailJson.getName());
            themesInstalled.put(i, detailJson);
            i++;
        }

        Platform.runLater(() -> {
            if(listOfficialInstalledPlugins.getItems().size() == 0)
                listOfficialInstalledPlugins.getItems().add("Aucun plugin officiel n'est installé");
            if(listOfficialInstalledThemes.getItems().size() == 0)
                listOfficialInstalledThemes.getItems().add("Aucun thème officiel n'est installé");
            if(listUnofficialInstalledPlugins.getItems().size() == 0)
                listUnofficialInstalledPlugins.getItems().add("Aucun plugin non officiel n'est installé");
            if(listUnofficialInstalledThemes.getItems().size() == 0)
                listUnofficialInstalledThemes.getItems().add("Aucun thème non officiel n'est installé");
        });
    }

    @Override
    public void onDownloadError(){
        Platform.runLater(this::errorAlert);
    }

    @Override
    public void onDownloadSuccess(){
        apiInstaller = new ApiInstaller(apiDownloader.getContentType(), apiDownloader.getContent(),
                apiDownloader.getOutputContentFile(), apiDownloader.getOutputDataFile());
        apiInstaller.addListener(this);
    }

    @Override
    public void onInstallError(){
        Platform.runLater(this::errorAlert);
    }

    @Override
    public void onInstallSuccess(){
        if(apiDownloader.getContentType() == ContentType.PLUGIN)
            MainApp.getContentsConfigPlugins().addContents(ContentsConfig.ConfigType.UNOFFICIAL, apiDownloader.getContent());
        else
            MainApp.getContentsConfigThemes().addContents(ContentsConfig.ConfigType.UNOFFICIAL, apiDownloader.getContent());

        //listUnofficialInstalledPlugins.getItems().clear();
        //listUnofficialInstalledThemes.getItems().clear();
        loadInstalledContents();

        listUnofficialInstalledPlugins.refresh();
        listUnofficialInstalledThemes.refresh();

        Platform.runLater(this::successAlert);
    }
}
