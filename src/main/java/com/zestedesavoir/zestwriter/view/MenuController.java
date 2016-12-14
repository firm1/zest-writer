package com.zestedesavoir.zestwriter.view;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.MetaAttribute;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.com.*;
import com.zestedesavoir.zestwriter.view.dialogs.*;
import com.zestedesavoir.zestwriter.view.task.*;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang.StringEscapeUtils;
import org.controlsfx.control.Rating;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @FXML public GridPane hBottomBox;
    @FXML private Menu menuExport;
    @FXML private MenuItem menuQuit;
    @FXML private MenuItem menuFindReplace;
    public BooleanPropertyBase isOnReadingTab = new SimpleBooleanProperty(true);

    public MenuController(){
        super();
        logger = LoggerFactory.getLogger(getClass());
    }

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    @FXML private void initialize() {
        if(FunctionTreeFactory.isMacOs()) {
            menuQuit.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        }
        labelField.getStyleClass().addAll("label-bottom");
        menuFindReplace.disableProperty().bind(isOnReadingTab);
    }

    @FXML private void HandleQuitButtonAction(ActionEvent event){
        mainApp.quitApp();
    }

    public static String markdownToHtml(MdTextController index, String chaine){
        PythonInterpreter console = index.getPyconsole();
        console.set("text", chaine);
        console.exec("render = mk_instance.convert(text)");
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

        ObservableList<ModelLisibilty> rows = FXCollections.observableArrayList();
        for(Entry<Textual, Double> entry : fleshResult.entrySet()){
            String v1;
            if(entry.getKey() instanceof MetaAttribute) {
                MetaAttribute attribute = (MetaAttribute) entry.getKey();
                v1 = attribute.getTitle()+ " (" + attribute.getParent().getTitle() + ")";
            } else {
                v1 = entry.getKey().getTitle();
            }
            Double t = (entry.getValue()/100)*5;
            Rating rating = new Rating(5, t.intValue());
            rows.add(new ModelLisibilty(v1, rating));
        }

        // Create the custom dialog.
        CustomDialog<Pair<String, String>> dialog = new CustomDialog<>();
        dialog.setResizable(true);
        dialog.setTitle(Configuration.bundle.getString("ui.menu.edit.readable.flesch_index"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.menu.edit.readable.flesch_index.header"));

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TableView table = new TableView();
        table.setPrefSize(600, 400);
        TableColumn colText = new TableColumn();
        colText.setCellValueFactory(new PropertyValueFactory<ModelLisibilty, String>("text"));
        TableColumn colRate = new TableColumn();
        colRate.setCellValueFactory(new PropertyValueFactory<ModelLisibilty, Rating>("rating"));
        table.getColumns().addAll(colText, colRate);
        table.setItems(rows);


        dialog.getDialogPane().setContent(table);
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

        ObservableList<ModelLisibilty> rows = FXCollections.observableArrayList();
        for(Entry<Textual, Double> entry : gunningResult.entrySet()){
            String v1;
            if(entry.getKey() instanceof MetaAttribute) {
                MetaAttribute attribute = (MetaAttribute) entry.getKey();
                v1 = attribute.getTitle()+ " (" + attribute.getParent().getTitle() + ")";
            } else {
                v1 = entry.getKey().getTitle();
            }
            Double t = (entry.getValue()/100)*5;
            Rating rating = new Rating(5, t.intValue());
            rows.add(new ModelLisibilty(v1, rating));
        }

        // Create the custom dialog.
        CustomDialog<Pair<String, String>> dialog = new CustomDialog<>();
        dialog.setResizable(true);
        dialog.setTitle(Configuration.bundle.getString("ui.menu.edit.readable.gunning_index"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.menu.edit.readable.gunning_index.header"));

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TableView table = new TableView();
        table.setPrefSize(600, 400);
        TableColumn colText = new TableColumn();
        colText.setCellValueFactory(new PropertyValueFactory<ModelLisibilty, String>("text"));
        TableColumn colRate = new TableColumn();
        colRate.setCellValueFactory(new PropertyValueFactory<ModelLisibilty, Rating>("rating"));
        table.getColumns().addAll(colText, colRate);
        table.setItems(rows);


        dialog.getDialogPane().setContent(table);
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

        if(MainApp.getConfig().getWorkspaceFactory() == null){
            MainApp.getConfig().loadWorkspace();
        }

        defaultDirectory = new File(MainApp.getZdsutils().getOfflineContentPathDir());

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

        if(MainApp.getConfig().getWorkspaceFactory() == null){
            MainApp.getConfig().loadWorkspace();
        }
        defaultDirectory = new File(MainApp.getZdsutils().getOfflineContentPathDir());
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
        LoginDialog dialog = new LoginDialog(googleAuth);
        googleAuth.setOnAction(t -> {
            GoogleLoginDialog googleDialog = new GoogleLoginDialog(dialog);
            googleDialog.show();
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        hBottomBox.add(labelField, 0, 0);
        LoginService loginTask = new LoginService();
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
        hBottomBox.add(pb, 0, 0);
        hBottomBox.add(labelField, 1, 0);
        DownloadContentService downloadContentTask = new DownloadContentService();
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
        if(! MainApp.getZdsutils().isAuthenticated()){
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
        if(MainApp.getConfig().getWorkspaceFactory() == null){
            MainApp.getConfig().loadWorkspace();
        }
    }

    private void uploadContents(){
        prerequisitesForData();

        hBottomBox.getChildren().clear();
        hBottomBox.add(labelField, 0, 0);

        try {
            if(mainApp.getContents ().get (0).isArticle()) {
                MainApp.getZdsutils().initInfoOnlineContent("article");
            } else {
                MainApp.getZdsutils().initInfoOnlineContent("tutorial");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }


        List<MetadataContent> contents = new ArrayList<>();
        contents.add(new MetadataContent(null, "---"+Configuration.bundle.getString("ui.content.new.title")+"---", null));
        List<MetadataContent> possibleContent;
        if(mainApp.getContents ().get (0).isArticle()) {
            possibleContent = MainApp.getZdsutils().getContentListOnline().stream()
                    .filter(meta -> meta.isArticle())
                    .collect(Collectors.toList());
        } else {
            possibleContent = MainApp.getZdsutils().getContentListOnline().stream()
                    .filter(meta -> meta.isTutorial())
                    .collect(Collectors.toList());
        }
        contents.addAll(possibleContent);

        Dialog<Pair<String, MetadataContent>> dialog = new CustomDialog<>();
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
        UploadContentService uploadContentTask = new UploadContentService(result, mainApp.getContents ().get (0));
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
        if(! MainApp.getZdsutils().isAuthenticated()){
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
        fileChooser.setInitialDirectory(mainApp.getDefaultHome());
        fileChooser.setTitle(Configuration.bundle.getString("ui.dialog.switchworkspace"));
        File selectedDirectory = fileChooser.showDialog(mainApp.getPrimaryStage());
        if(selectedDirectory!=null) {
            MainApp.getConfig().setWorkspacePath(selectedDirectory.getAbsolutePath());
            MainApp.getConfig().loadWorkspace();

            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.bundle.getString("ui.options.workspace"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.workspace.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.workspace.text") + " " + MainApp.getConfig().getWorkspacePath());
            alert.setResizable(true);

            alert.showAndWait();
        }
    }

    @FXML private void HandleExportMarkdownButtonAction(ActionEvent event){
        Content content = mainApp.getContents().get(0);
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(mainApp.getDefaultHome());
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
        fileChooser.setInitialDirectory(mainApp.getDefaultHome());
        fileChooser.setTitle(Configuration.bundle.getString("ui.dialog.export.dir.title"));
        File selectedDirectory = fileChooser.showDialog(mainApp.getPrimaryStage());
        File selectedFile = new File(selectedDirectory, ZdsHttp.toSlug(content.getTitle()) + ".pdf");
        logger.debug("Tentative d'export vers le fichier " + selectedFile.getAbsolutePath());

        if(selectedDirectory != null){
            hBottomBox.getChildren().clear();
            hBottomBox.add(pb, 0, 0);
            hBottomBox.add(labelField, 1, 0);
            ExportPdfService exportPdfTask = new ExportPdfService(MainApp.getConfig().getPandocProvider(), content, selectedFile);
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

    @FXML private void HandleMdCheatSheetButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/MdCheatSheetDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.bundle.getString("ui.menu.help.md_cheat_sheet"));

        MdCheatSheetDialog mdCheatSheetController = loader.getController();

        dialogStage.show();
    }

    @FXML private void HandleFindReplaceAction(ActionEvent event){
        SplitPane sPane = (SplitPane) mainApp.getExtracts()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isSelected())
                .findFirst()
                .get()
                .getValue().getContent();
        BorderPane bPane = (BorderPane) sPane.getItems().get(0);
        StyleClassedTextArea source = (StyleClassedTextArea) bPane.getCenter();
        FunctionTreeFactory.OpenFindReplaceDialog(mainApp, source);
    }

    @FXML private void HandleAboutButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/AboutDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.bundle.getString("ui.menu.help.about"));
        dialogStage.setResizable(false);

        AboutDialog aboutController = loader.getController();
        aboutController.setMainApp(mainApp);

        dialogStage.show();
    }

    @FXML private void HandleOptionsButtonAction(ActionEvent evnet){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/OptionsDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.bundle.getString("ui.menu.options"));
        dialogStage.setResizable(false);


        OptionsDialog optionsController = loader.getController();
        optionsController.setMainApp(mainApp);
        optionsController.setWindow(dialogStage);

        dialogStage.show();
    }

    @FXML private void HandleImportGithubButtonAction() {
        TextInputDialog dialog = new TextInputDialog("https://github.com/");
        dialog.setTitle(Configuration.bundle.getString("ui.dialog.import.github.title"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.dialog.import.github.header"));
        dialog.setContentText(Configuration.bundle.getString("ui.dialog.import.github.text")+" :");
        dialog.getEditor().setPrefWidth(500);
        dialog.initOwner(mainApp.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(url -> {
            hBottomBox.getChildren().clear();
            hBottomBox.add(pb, 0, 0);
            hBottomBox.add(labelField, 1, 0);
            DownloadGithubService downloadGithubTask = new DownloadGithubService(url, MainApp.getZdsutils().getOfflineContentPathDir(), MainApp.getZdsutils().getOnlineContentPathDir());
            labelField.textProperty().bind(downloadGithubTask.messageProperty());
            pb.progressProperty().bind(downloadGithubTask.progressProperty());
            Alert alert = new CustomAlert(AlertType.NONE);
            downloadGithubTask.setOnFailed (t -> {
                alert.setAlertType(AlertType.ERROR);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.download.github.failed.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.download.github.failed.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.download.github.failed.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });
            downloadGithubTask.setOnSucceeded (t -> {
                FunctionTreeFactory.switchContent (downloadGithubTask.getValue (), mainApp.getContents ());
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.download.github.success.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.download.github.success.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.download.github.success.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });

            if(result.isPresent()){
                downloadGithubTask.start();
            }
        });
    }

    @FXML private void HandleImportZdsButtonAction() {
        TextInputDialog dialog = new TextInputDialog("https://zestedesavoir.com/");
        dialog.setTitle(Configuration.bundle.getString("ui.dialog.import.zds.title"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.dialog.import.zds.header"));
        dialog.setContentText(Configuration.bundle.getString("ui.dialog.import.zds.text")+" :");
        dialog.getEditor().setPrefWidth(500);
        dialog.initOwner(mainApp.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(url -> {
            hBottomBox.getChildren().clear();
            hBottomBox.add(pb, 0, 0);
            hBottomBox.add(labelField, 1, 0);
            DownloadZdsService downloadZdsTask = new DownloadZdsService(url, MainApp.getZdsutils().getOfflineContentPathDir(), MainApp.getZdsutils().getOnlineContentPathDir());
            labelField.textProperty().bind(downloadZdsTask.messageProperty());
            pb.progressProperty().bind(downloadZdsTask.progressProperty());
            Alert alert = new CustomAlert(AlertType.NONE);
            downloadZdsTask.setOnFailed (t -> {
                alert.setAlertType(AlertType.ERROR);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.download.zds.failed.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.download.zds.failed.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.download.zds.failed.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });
            downloadZdsTask.setOnSucceeded (t -> {
                FunctionTreeFactory.switchContent (downloadZdsTask.getValue (), mainApp.getContents ());
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.download.zds.success.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.download.zds.success.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.download.zds.success.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });

            if(result.isPresent()){
                downloadZdsTask.start();
            }
        });
    }

    @FXML private void HandleCheckUpdateButtonAction(ActionEvent event){
        Service<Boolean> checkService = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        String current = MainApp.getConfig().getProps().getProperty("version", Configuration.bundle.getString("ui.version.label.unknown"));
                        String versionOnline = Configuration.getLastRelease();
                        if(versionOnline == null) {
                            throw new IOException();
                        } else {
                            String[] locale_tab = current.split(".");
                            for(String s:locale_tab) {
                                try {
                                    Integer.valueOf(s);
                                } catch(Exception e) {
                                    return true;
                                }
                            }
                            return versionOnline.compareTo(current) <= 0;
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

    @FXML private void HandleContentsButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/ContentsDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.bundle.getString("ui.menu.tools.external_contents"));
        dialogStage.setResizable(false);
        dialogStage.initOwner(mainApp.getPrimaryStage());

        ContentsDialog contentsController = loader.getController();
        contentsController.setWindow(dialogStage);

        if(!MainApp.getContentsConfigPlugins().isCorrupted() && !MainApp.getContentsConfigThemes().isCorrupted())
            dialogStage.show();
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
