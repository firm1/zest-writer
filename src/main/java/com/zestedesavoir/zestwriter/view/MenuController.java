package com.zestedesavoir.zestwriter.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.CustomFXMLLoader;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import com.zestedesavoir.zestwriter.view.dialogs.AboutDialog;
import com.zestedesavoir.zestwriter.view.dialogs.GoogleLoginDialog;
import com.zestedesavoir.zestwriter.view.dialogs.LoginDialog;
import com.zestedesavoir.zestwriter.view.dialogs.OptionsDialog;
import com.zestedesavoir.zestwriter.view.task.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang.StringEscapeUtils;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

public class MenuController{
    private MainApp mainApp;
    private TextArea textArea;
    private final ProgressBar pb = new ProgressBar(0);
    private final Text labelField = new Text("");
    private final Logger logger;

    @FXML private MenuItem menuDownload;
    @FXML private MenuItem menuUpload;
    @FXML private MenuItem menuReport;
    @FXML private MenuItem menuLisibility;
    @FXML private MenuItem menuGoogle;
    @FXML public GridPane hBottomBox;
    @FXML private Menu menuExport;
    @FXML private MenuItem menuQuit;


    public MenuController(){
        super();
        logger = LoggerFactory.getLogger(getClass());
    }

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML private void initialize() {
        if(FunctionTreeFactory.isMacOs()) {
            menuQuit.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        }
    }

    @FXML private void HandleQuitButtonAction(ActionEvent event){
        mainApp.quitApp();
    }

    public static String markdownToHtml(MdTextController index, String chaine){
        PythonInterpreter console = index.getPyconsole();
        console.set("text", chaine);
        console.exec("render = Markdown(extensions=(ZdsExtension({'inline': False, 'emoticons': smileys}),),safe_mode = 'escape', enable_attributes = False, tab_length = 4, output_format = 'html5', smart_emphasis = True, lazy_ol = True).convert(text)");
        PyString render = console.get("render", PyString.class);
        return render.toString();
    }

    @FXML private void HandleFleshButtonAction(ActionEvent event){
        Function<Textual, Double> calFlesh = (Textual ch) -> {
            String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), ch.readMarkdown()));
            String plainText = Corrector.HtmlToTextWithoutCode(htmlText);
            if(plainText.trim().equals("")){
                return 100.0;
            }else{
                Readability rd = new Readability(plainText);
                return rd.getFleschReadingEase();
            }
        };
        Map<Textual, Double> fleshResult = ((Content)mainApp.getIndex().getSummary().getRoot().getValue()).doOnTextual(calFlesh);

        ObservableList<String> rows = FXCollections.observableArrayList();
        for(Entry<Textual, Double> entry : fleshResult.entrySet()){
            String easy;
            if(entry.getValue() < 30){
                easy = Configuration.bundle.getString("ui.level.very_difficult");
            }else if(entry.getValue() < 50){
                easy = Configuration.bundle.getString("ui.level.difficult");
            }else if(entry.getValue() < 60){
                easy = Configuration.bundle.getString("ui.level.quite_difficult");
            }else if(entry.getValue() < 70){
                easy = Configuration.bundle.getString("ui.level.normal");
            }else if(entry.getValue() < 80){
                easy = Configuration.bundle.getString("ui.level.easy");
            }else{
                easy = Configuration.bundle.getString("ui.level.very_easy");
            }

            String v1 = entry.getKey().getTitle();
            String v2 = entry.getValue().toString() + " (" + easy + ")";
            rows.add(v1 + " => " + v2);
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(Configuration.bundle.getString("ui.menu.edit.readable.flesch_index"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.menu.edit.readable.flesch_index.header"));

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ListView<String> list = new ListView<>();
        list.setPrefSize(800, 500);
        list.setItems(rows);


        dialog.getDialogPane().setContent(list);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK){
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML private void HandleGunningButtonAction(ActionEvent event){
        Function<Textual, Double> calFlesh = (Textual ch) -> {
            String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), ch.readMarkdown()));
            String plainText = Corrector.HtmlToTextWithoutCode(htmlText);
            if(plainText.trim().equals("")){
                return 100.0;
            }else{
                Readability rd = new Readability(plainText);
                return rd.getGunningFog();
            }
        };
        Map<Textual, Double> gunningResult = ((Content)mainApp.getIndex().getSummary().getRoot().getValue()).doOnTextual(calFlesh);

        ObservableList<String> rows = FXCollections.observableArrayList();
        for(Entry<Textual, Double> entry : gunningResult.entrySet()){
            String easy;
            if(entry.getValue() >= 15){
                easy = Configuration.bundle.getString("ui.level.very_difficult");
            }else if(entry.getValue() >= 12){
                easy = Configuration.bundle.getString("ui.level.difficult");
            }else if(entry.getValue() >= 10){
                easy = Configuration.bundle.getString("ui.level.quite_difficult");
            }else if(entry.getValue() >= 8){
                easy = Configuration.bundle.getString("ui.level.normal");
            }else if(entry.getValue() >= 6){
                easy = Configuration.bundle.getString("ui.level.easy");
            }else{
                easy = Configuration.bundle.getString("ui.level.very_easy");
            }

            String v1 = entry.getKey().getTitle();
            String v2 = entry.getValue().toString() + " (" + easy + ")";
            rows.add(v1 + " => " + v2);
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(Configuration.bundle.getString("ui.menu.edit.readable.gunning_index"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.menu.edit.readable.gunning_index.header"));

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ListView<String> list = new ListView<>();
        list.setPrefSize(800, 500);
        list.setItems(rows);


        dialog.getDialogPane().setContent(list);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK){
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML private void HandleReportWithoutTypoButtonAction(ActionEvent event){
        textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label(Configuration.bundle.getString("ui.menu.edit.correction")), 0, 0);
        expContent.add(textArea, 0, 1);

        hBottomBox.getChildren().addAll(labelField);
        CorrectionService correctTask = new CorrectionService(mainApp.getIndex());
        labelField.textProperty().bind(correctTask.messageProperty());
        textArea.textProperty().bind(correctTask.valueProperty());
        correctTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            Alert alert = new CustomAlert(AlertType.NONE);

            switch(newValue){
                case FAILED:
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle(Configuration.bundle.getString("ui.alert.correction.failed.title"));
                    alert.setHeaderText(Configuration.bundle.getString("ui.alert.correction.failed.header"));
                    alert.setContentText(Configuration.bundle.getString("ui.alert.correction.failed.text"));

                    alert.showAndWait();
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setTitle(Configuration.bundle.getString("ui.alert.correction.success.title"));
                    alert.setHeaderText(Configuration.bundle.getString("ui.alert.correction.success.header"));

                    // Set expandable Exception into the dialog pane.
                    alert.getDialogPane().setExpandableContent(expContent);
                    alert.showAndWait();
                    break;
            }
            hBottomBox.getChildren().clear();
        });
        correctTask.start();
    }

    @FXML private void HandleNewButtonAction(ActionEvent event){
        File defaultDirectory;

        if(mainApp.getConfig().getWorkspaceFactory() == null){
            mainApp.getConfig().loadWorkspace();
        }

        defaultDirectory = new File(mainApp.getZdsutils().getOfflineContentPathDir());

        Map<String, Object> paramContent = FunctionTreeFactory.initContentDialog(null);

        if(paramContent != null){
            // find inexistant directory
            String localPath = defaultDirectory.getAbsolutePath() + File.separator + ZdsHttp.toSlug((String)paramContent.get("title"));
            String realLocalPath = FunctionTreeFactory.getUniqueDirPath(localPath);
            File folder = new File(realLocalPath);
            folder.mkdir();

            // create manifest.json
            File manifest = new File(realLocalPath + File.separator + "manifest.json");
            ObjectMapper mapper = new ObjectMapper();
            paramContent.put("slug", ZdsHttp.toSlug((String)paramContent.get("title")));
            paramContent.put("version", 2);
            paramContent.put("object", "container");
            paramContent.put("introduction", "introduction.md");
            paramContent.put("conclusion", "conclusion.md");
            paramContent.put("children", new ArrayList<>());

            try{
                mapper.writeValue(manifest, paramContent);
                // create introduction and conclusion
                File intro = new File(realLocalPath + File.separator + "introduction.md");
                File conclu = new File(realLocalPath + File.separator + "conclusion.md");
                intro.createNewFile();
                conclu.createNewFile();
                Content content = mapper.readValue(manifest, Content.class);
                content.setRootContent(content, realLocalPath);
                FunctionTreeFactory.switchContent(content, mainApp.getContents());

            }catch(IOException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    @FXML private void HandleOpenButtonAction(ActionEvent event){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Configuration.bundle.getString("ui.menu.dialog.open.title"));
        File defaultDirectory;

        if(mainApp.getConfig().getWorkspaceFactory() == null){
            mainApp.getConfig().loadWorkspace();
        }
        defaultDirectory = new File(mainApp.getZdsutils().getOfflineContentPathDir());
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(mainApp.getPrimaryStage());

        if(selectedDirectory != null){
            File manifest = new File(selectedDirectory.getAbsolutePath() + File.separator + "manifest.json");
            ObjectMapper mapper = new ObjectMapper();
            Content content;
            try{
                content = mapper.readValue(manifest, Content.class);
                content.setRootContent(content, selectedDirectory.getAbsolutePath());
                FunctionTreeFactory.switchContent(content, mainApp.getContents());
            }catch(IOException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void activateButtonForOpenContent() {
        menuUpload.setDisable(false);
        menuLisibility.setDisable(false);
        menuReport.setDisable(false);
        menuExport.setDisable(false);
    }

    @FXML public Service<Void> HandleLoginButtonAction(ActionEvent event){
        // Button for google
        Button googleAuth = new Button(Configuration.bundle.getString("ui.dialog.auth.google.title"), IconFactory.createGoogleIcon());
        LoginDialog dialog = new LoginDialog(googleAuth, mainApp);
        googleAuth.setOnAction(t -> {
            GoogleLoginDialog googleDialog = new GoogleLoginDialog(dialog, mainApp.getZdsutils());
            googleDialog.show();
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        hBottomBox.getChildren().addAll(labelField);
        LoginService loginTask = new LoginService(mainApp.getZdsutils(), mainApp.getConfig());
        result.ifPresent(usernamePassword -> {
            loginTask.setUsername(usernamePassword.getKey());
            loginTask.setPassword(usernamePassword.getValue());
        });
        labelField.textProperty().bind(loginTask.messageProperty());
        return loginTask;
    }

    private void downloadContents(){
        prerequisitesForData();

        hBottomBox.getChildren().clear();
        hBottomBox.getChildren().addAll(pb, labelField);
        DownloadContentService downloadContentTask = new DownloadContentService(mainApp.getZdsutils());
        labelField.textProperty().bind(downloadContentTask.messageProperty());
        pb.progressProperty().bind(downloadContentTask.progressProperty());
        downloadContentTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch(newValue){
                case FAILED:
                case CANCELLED:
                case SUCCEEDED:
                    Alert alert = new CustomAlert(AlertType.INFORMATION);
                    alert.setTitle(Configuration.bundle.getString("ui.alert.download.success.title"));
                    alert.setHeaderText(Configuration.bundle.getString("ui.alert.download.success.header"));
                    alert.setContentText(Configuration.bundle.getString("ui.alert.download.success.text"));
                    alert.showAndWait();
                    hBottomBox.getChildren().clear();

                    mainApp.getIndex().refreshRecentProject();

                    break;
            }
        });

        downloadContentTask.start();
    }

    @FXML private void HandleDownloadButtonAction(ActionEvent event){
        if(! mainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = HandleLoginButtonAction(event);

            loginTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                Alert alert;
                switch(newValue){
                    case FAILED:
                    case CANCELLED:
                        hBottomBox.getChildren().clear();
                        alert = new CustomAlert(AlertType.ERROR);
                        alert.setTitle(Configuration.bundle.getString("ui.dialog.auth.failed.title"));
                        alert.setHeaderText(Configuration.bundle.getString("ui.dialog.auth.failed.header"));
                        alert.setContentText(Configuration.bundle.getString("ui.dialog.auth.failed.text"));

                        alert.showAndWait();
                        break;
                    case SUCCEEDED:
                        if(mainApp.getContents().size() > 0){
                            menuUpload.setDisable(false);
                        }
                        downloadContents();
                        break;
                }
            });
            loginTask.start();
        }else{
            downloadContents();
        }

    }

    private void prerequisitesForData(){
        if(mainApp.getConfig().getWorkspaceFactory() == null){
            mainApp.getConfig().loadWorkspace();
        }
    }

    private void uploadContents(){
        prerequisitesForData();

        hBottomBox.getChildren().clear();
        hBottomBox.getChildren().addAll(labelField);


        List<MetadataContent> contents = new ArrayList<>();
        contents.add(new MetadataContent(null, "---"+Configuration.bundle.getString("ui.content.new.title")+"---", null));
        contents.addAll(mainApp.getZdsutils().getContentListOnline());

        Dialog<Pair<String, MetadataContent>> dialog = new Dialog<>();
        dialog.setTitle(Configuration.bundle.getString("ui.content.select.title"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.content.select.header"));
        ButtonType loginButtonType = new ButtonType(Configuration.bundle.getString("ui.content.select.button.send"), ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea msg = new TextArea();
        msg.setText(Configuration.bundle.getString("ui.content.select.placeholder.commit_msg"));
        ChoiceBox<MetadataContent> contenus = new ChoiceBox<>();
        contenus.setItems(FXCollections.observableArrayList(contents));

        grid.add(new Label(Configuration.bundle.getString("ui.content.select.field.slug")+" : "), 0, 0);
        grid.add(contenus, 1, 0);
        grid.add(new Label(Configuration.bundle.getString("ui.content.select.field.commit_msg")+" : "), 0, 1);
        grid.add(msg, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(msg.getText(), contenus.getValue());
            }
            return null;
        });
        Optional<Pair<String, MetadataContent>> result = dialog.showAndWait();
        UploadContentService uploadContentTask = new UploadContentService(mainApp.getZdsutils(), result);
        labelField.textProperty().bind(uploadContentTask.messageProperty());
        uploadContentTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            Alert alert = new CustomAlert(AlertType.NONE);

            switch(newValue){
                case FAILED:
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle(Configuration.bundle.getString("ui.dialog.upload.content.failed.title"));
                    alert.setHeaderText(Configuration.bundle.getString("ui.dialog.upload.content.failed.header"));
                    alert.setContentText(Configuration.bundle.getString("ui.dialog.upload.content.failed.text"));
                    alert.showAndWait();
                    break;
                case CANCELLED:
                    break;
                case SUCCEEDED:
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setTitle(Configuration.bundle.getString("ui.dialog.upload.content.success.title"));
                    alert.setHeaderText(Configuration.bundle.getString("ui.dialog.upload.content.success.header"));
                    alert.setContentText(Configuration.bundle.getString("ui.dialog.upload.content.success.text"));
                    alert.showAndWait();
                    hBottomBox.getChildren().clear();
                    break;
            }
        });

        if(result.isPresent()){
            Function<Textual, Boolean> checkExtractAvailability = (Textual ch) -> {
                File f = new File(ch.getFilePath());
                return f.exists();
            };
            Map<Textual, Boolean> analyse = mainApp.getContents().get(0).doOnTextual(checkExtractAvailability);
            boolean avalaible = true;
            for(Map.Entry<Textual, Boolean> tx:analyse.entrySet()) {
                if(!tx.getValue()) {
                    avalaible = false;
                    break;
                }
            }
            if(avalaible) {
                uploadContentTask.start();
            }
            else {
                Alert alert = new CustomAlert(AlertType.ERROR);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.upload.content.failed.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.upload.content.failed.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.upload.content.failed.text.nofile"));
                alert.showAndWait();
            }
        }
    }

    @FXML private void HandleUploadButtonAction(ActionEvent event){
        if(! mainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = HandleLoginButtonAction(event);

            loginTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                Alert alert;
                switch(newValue){
                    case FAILED:
                        break;
                    case CANCELLED:
                        hBottomBox.getChildren().clear();
                        alert = new CustomAlert(AlertType.ERROR);
                        alert.setTitle(Configuration.bundle.getString("ui.dialog.upload.content.failed.title"));
                        alert.setHeaderText(Configuration.bundle.getString("ui.dialog.upload.content.failed.header"));
                        alert.setContentText(Configuration.bundle.getString("ui.dialog.upload.content.failed.text"));

                        alert.showAndWait();
                        break;
                    case SUCCEEDED:
                        if(mainApp.getContents().size() > 0){
                            menuUpload.setDisable(false);
                        }
                        uploadContents();
                        break;
                }
            });
            loginTask.start();
        }else{
            uploadContents();
        }
    }

    @FXML private void HandleSwitchWorkspaceAction(ActionEvent event) throws IOException{
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Sélectionnez un dossier");
        File selectedDirectory = fileChooser.showDialog(mainApp.getPrimaryStage());
        if(selectedDirectory!=null) {
            mainApp.getConfig().setWorkspacePath(selectedDirectory.getAbsolutePath());
            mainApp.getConfig().loadWorkspace();

            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.bundle.getString("ui.options.workspace"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.workspace.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.workspace.text") + " " + mainApp.getConfig().getWorkspacePath());
            alert.setResizable(true);

            alert.showAndWait();
        }
    }

    @FXML private void HandleExportMarkdownButtonAction(ActionEvent event){
        Content content = mainApp.getContents().get(0);
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle(Configuration.bundle.getString("ui.dialog.export.dir.title"));
        File selectedDirectory = fileChooser.showDialog(mainApp.getPrimaryStage());
        File selectedFile = new File(selectedDirectory, ZdsHttp.toSlug(content.getTitle()) + ".md");
        logger.debug("Tentative d'export vers le fichier " + selectedFile.getAbsolutePath());

        if(selectedDirectory != null){

            content.saveToMarkdown(selectedFile);
            logger.debug("Export réussi vers " + selectedFile.getAbsolutePath());

            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.bundle.getString("ui.dialog.export.success.title"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.export.success.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.export.success.text")+" \"" + selectedFile.getAbsolutePath() + "\"");
            alert.setResizable(true);

            alert.showAndWait();
        }
    }

    @FXML private void HandleExportPdfButtonAction(ActionEvent event){
        Content content = mainApp.getContents().get(0);
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle(Configuration.bundle.getString("ui.dialog.export.dir.title"));
        File selectedDirectory = fileChooser.showDialog(mainApp.getPrimaryStage());
        File selectedFile = new File(selectedDirectory, ZdsHttp.toSlug(content.getTitle()) + ".pdf");
        logger.debug("Tentative d'export vers le fichier " + selectedFile.getAbsolutePath());

        if(selectedDirectory != null){
            hBottomBox.getChildren().clear();
            hBottomBox.getChildren().addAll(pb, labelField);
            ExportPdfService exportPdfTask = new ExportPdfService(mainApp.getConfig().getPandocProvider(), content, selectedFile);
            labelField.textProperty().bind(exportPdfTask.messageProperty());
            pb.progressProperty().bind(exportPdfTask.progressProperty());
            Alert alert = new CustomAlert(AlertType.NONE);
            exportPdfTask.setOnFailed((WorkerStateEvent ev) -> {
                alert.setAlertType(AlertType.ERROR);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.export.failed.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.export.failed.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.export.failed.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });

            exportPdfTask.setOnSucceeded((WorkerStateEvent ev) -> {
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.export.success.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.export.success.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.export.success.text")+" \"" + selectedFile.getAbsolutePath() + "\"");
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });
            exportPdfTask.start();
        }
    }

    @FXML private void HandleAboutButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/AboutDialog.fxml"));

        try{
            AnchorPane aboutDialog = loader.load();
            AboutDialog aboutController = loader.getController();
            aboutController.setMainApp(mainApp);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(Configuration.bundle.getString("ui.menu.help.about"));

            Scene scene = new Scene(aboutDialog);
            dialogStage.setScene(scene);
            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("assets/static/icons/logo.png")));
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            dialogStage.show();
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    @FXML private void HandleOptionsButtonAction(ActionEvent evnet){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/OptionsDialog.fxml"));

        try{
            AnchorPane optionsDialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(Configuration.bundle.getString("ui.menu.options"));

            Scene scene = new Scene(optionsDialog);
            dialogStage.setScene(scene);
            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("assets/static/icons/logo.png")));
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            OptionsDialog optionsController = loader.getController();
            optionsController.setMainApp(mainApp);
            optionsController.setWindow(dialogStage);

            dialogStage.show();
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    @FXML private void HandleCheckUpdateButtonAction(ActionEvent event){
        Service<Boolean> checkService = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        String current = mainApp.getConfig().getProps().getProperty("version", Configuration.bundle.getString("ui.version.label.unknown"));
                        String versionOnline = Configuration.getLastRelease();
                        if(versionOnline == null) {
                            throw new IOException();
                        } else {
                            return versionOnline.equals(current);
                        }
                    }
                };
            }
        };

        checkService.setOnFailed(t -> {
            Alert alert = new CustomAlert(AlertType.ERROR);
            alert.setTitle(Configuration.bundle.getString("ui.dialog.check_update.failed.title"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.check_update.failed.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.check_update.failed.text"));
            alert.showAndWait();
        });

        checkService.setOnSucceeded(t -> {
            Alert alert = new CustomAlert(AlertType.NONE);
            alert.setTitle(Configuration.bundle.getString("ui.dialog.check_update.success.title"));

            if(!checkService.getValue()){
                alert.setAlertType(AlertType.WARNING);
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.check_update.warn.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.check_update.warn.text"));
            }else{
                alert.setAlertType(AlertType.INFORMATION);
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.check_update.success.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.check_update.success.text"));
            }

            alert.showAndWait();
        });

        checkService.start();
    }

    public Text getLabelField(){
        return labelField;
    }

    public GridPane gethBottomBox(){
        return hBottomBox;
    }

    public MenuItem getMenuDownload(){
        return menuDownload;
    }
}
