package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.utils.api.ApiContentResponse;
import com.zestedesavoir.zestwriter.utils.api.ApiContentsResponse;
import com.zestedesavoir.zestwriter.utils.api.ApiMapper;
import com.zestedesavoir.zestwriter.utils.api.ApiRequester;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ContentsDialog{
    private Logger logger = LogManager.getLogger(ContentsDialog.class);
    private Stage window;

    private Map<Integer, ApiContentResponse> plugins = new HashMap<>();
    private Map<Integer, ApiContentResponse> themes = new HashMap<>();
    private String currentPlugin;
    private String currentTheme;

    @FXML private ListView listThemes;
    @FXML private ListView listOfficialInstalledThemes;
    @FXML private ListView listUnofficialInstalledThemes;
    @FXML private ListView listPlugins;
    @FXML private ListView listOfficialInstalledPlugins;
    @FXML private ListView listUnofficialInstalledPlugins;

    public void setWindow(Stage window){
        this.window = window;
    }

    @FXML private void initialize(){
        listOfficialInstalledThemes.getItems().add("Aucun thème n'est installé");
        listUnofficialInstalledThemes.getItems().add("Aucun thème n'est installé");
        listOfficialInstalledPlugins.getItems().add("Aucun thème n'est installé");
        listUnofficialInstalledPlugins.getItems().add("Aucun thème n'est installé");

        ApiRequester requester = new ApiRequester();
        ApiMapper mapper;

        if(!requester.isApiOk()){
            alert(Alert.AlertType.ERROR, "API: Erreur", "Une errer est survenu lors du contact de l'API", "http://zw.winxaito.com/api/");
            return;
        }

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
                    plugins.put(i, theme);

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

        listPlugins.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentPlugin = (String)newValue;
        });

        listThemes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentTheme = (String)newValue;
        });
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

        alert(
                Alert.AlertType.CONFIRMATION,
                "Installation",
                "Voulez-vous vraiment installé ce plugin ?",
                "Le plugin sera télécharger depuis ces adresses:\n  " + plugin.getDownload_url() + ".content\n  " + plugin.getDownload_url() + ".data"
        );
    }

    @FXML private void handleOfficialPluginState(){
        
    }

    @FXML private void handleUnofficialPluginState(){
        
    }

    @FXML private void handleUnofficialPluginUninstall(){
        alert(Alert.AlertType.CONFIRMATION, "Désinstallation", null, "Voulez-vous désinstaller ce plugin ?");
    }

    @FXML private void handleListThemeInfos(){
        ApiContentResponse theme = getPlugin();

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
        ApiContentResponse theme = getPlugin();
        assert theme != null;

        alert(
                Alert.AlertType.CONFIRMATION,
                "Installation",
                "Voulez-vous vraiment installé ce thème ?",
                "Le thème sera télécharger depuis ces adresses:\n  " + theme.getDownload_url() + ".content\n  " + theme.getDownload_url() + ".data"
        );
    }

    @FXML private void handleOfficialThemeState(){

    }

    @FXML private void handleUnofficialThemeState(){

    }

    @FXML private void handleUnofficialThemeUninstall(){

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

    private void alert(Alert.AlertType type, String title, String header, String content){
        Alert a = new CustomAlert(type);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.initOwner(window);
        a.showAndWait();
    }
}
