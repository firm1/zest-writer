package com.zestedesavoir.zestwriter.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.*;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.com.*;
import com.zestedesavoir.zestwriter.view.dialogs.*;
import com.zestedesavoir.zestwriter.view.task.*;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuController{
    private MainApp mainApp;
    private final ProgressBar pb = new ProgressBar(0);
    private final Text labelField = new Text("");
    private final Logger logger;

    @FXML private MenuItem menuDownload;
    @FXML private MenuItem menuUpload;
    @FXML private MenuItem menuReport;
    @FXML private MenuItem menuLisibility;
    @FXML private GridPane hBottomBox;
    @FXML private Menu menuExport;
    @FXML private MenuItem menuQuit;
    @FXML private MenuItem menuFindReplace;
    private BooleanPropertyBase isOnReadingTab = new SimpleBooleanProperty(true);

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



    public BooleanPropertyBase isOnReadingTabProperty() {
        return isOnReadingTab;
    }

    public void setIsOnReadingTab(boolean isOnReadingTab) {
        this.isOnReadingTab.set(isOnReadingTab);
    }

    @FXML private void initialize() {
        if(FunctionTreeFactory.isMacOs()) {
            menuQuit.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        }
        labelField.getStyleClass().addAll("label-bottom");
        menuFindReplace.disableProperty().bind(isOnReadingTab);
    }

    @FXML private void handleQuitButtonAction(ActionEvent event){
        mainApp.quitApp();
    }

    public static String markdownToHtml(MdTextController index, String chaine){
        PythonInterpreter console = index.getPyconsole();
        console.set("text", chaine);
        console.exec("render = mk_instance.convert(text)");
        PyString render = console.get("render", PyString.class);
        return render.toString();
    }

    private void displayIndex(Map<String, Double> resultIndex, String title, String header) {
        BaseDialog dialog = new BaseDialog(title, header);
        dialog.getDialogPane().setPrefSize(800, 600);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType(Configuration.getBundle().getString("ui.actions.stats.close"), ButtonBar.ButtonData.CANCEL_CLOSE));

        // draw
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String,Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);
        lineChart.setLegendVisible(false);

        xAxis.setLabel(Configuration.getBundle().getString("ui.actions.stats.xaxis"));
        yAxis.setLabel(Configuration.getBundle().getString("ui.actions.readable.yaxis"));

        XYChart.Series<String, Number> series = new XYChart.Series();
        for(Map.Entry<String, Double> st:resultIndex.entrySet()) {
            series.getData().add(new XYChart.Data(st.getKey(), st.getValue()));
        }
        lineChart.getData().addAll(series);
        dialog.getDialogPane().setContent(lineChart);
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    @FXML private void handleFleshButtonAction(ActionEvent event){
        Function<Textual, Double> calFlesh = (Textual ch) -> {
            String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), ch.readMarkdown()));
            String plainText = Corrector.htmlToTextWithoutCode(htmlText);
            if("".equals(plainText.trim())){
                return 100.0;
            }else{
                Readability rd = new Readability(plainText);
                return rd.getFleschReadingEase();
            }
        };
        ComputeIndexService computeIndexService = new ComputeIndexService(calFlesh, (Container) mainApp.getIndex().getSummary().getRoot().getValue());
        hBottomBox.getChildren().clear();
        hBottomBox.getChildren().addAll(labelField);
        labelField.textProperty().bind(computeIndexService.messageProperty());
        computeIndexService.setOnSucceeded(t -> {
            displayIndex(((ComputeIndexService) t.getSource()).getValue(),
                    Configuration.getBundle().getString("ui.menu.edit.readable.flesch_index"),
                    Configuration.getBundle().getString("ui.menu.edit.readable.flesch_index.header"));
            hBottomBox.getChildren().clear();
        });
        computeIndexService.start();
    }

    @FXML private void handleGunningButtonAction(ActionEvent event){
        Function<Textual, Double> calGuning = (Textual ch) -> {
            String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), ch.readMarkdown()));
            String plainText = Corrector.htmlToTextWithoutCode(htmlText);
            if("".equals(plainText.trim())){
                return 100.0;
            }else{
                Readability rd = new Readability(plainText);
                return rd.getGunningFog();
            }
        };
        ComputeIndexService computeIndexService = new ComputeIndexService(calGuning, (Container) mainApp.getIndex().getSummary().getRoot().getValue());
        hBottomBox.getChildren().clear();
        hBottomBox.getChildren().addAll(labelField);
        labelField.textProperty().bind(computeIndexService.messageProperty());
        computeIndexService.setOnSucceeded(t -> {
            displayIndex(((ComputeIndexService) t.getSource()).getValue(),
                    Configuration.getBundle().getString("ui.menu.edit.readable.gunning_index"),
                    Configuration.getBundle().getString("ui.menu.edit.readable.gunning_index.header"));
            hBottomBox.getChildren().clear();
        });
        computeIndexService.start();
    }

    @FXML private void handleReportWithoutTypoButtonAction(ActionEvent event){
        TextArea textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label(Configuration.getBundle().getString("ui.menu.edit.correction")), 0, 0);
        expContent.add(textArea, 0, 1);

        hBottomBox.getChildren().addAll(labelField);
        CorrectionService correctTask = new CorrectionService(mainApp.getIndex());
        labelField.textProperty().bind(correctTask.messageProperty());
        textArea.textProperty().bind(correctTask.valueProperty());
        correctTask.setOnFailed(t -> {
            Alert alert = new CustomAlert(AlertType.ERROR);
            alert.setTitle(Configuration.getBundle().getString("ui.alert.correction.failed.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.alert.correction.failed.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.alert.correction.failed.text"));

            alert.showAndWait();
            hBottomBox.getChildren().clear();
        });
        correctTask.setOnSucceeded(t -> {
            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.alert.correction.success.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.alert.correction.success.header"));

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
            alert.showAndWait();
            hBottomBox.getChildren().clear();
        });
        correctTask.start();
    }

    @FXML private void handleNewButtonAction(ActionEvent event){
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
            paramContent.put("introduction", Constant.DEFAULT_INTRODUCTION_FILENAME);
            paramContent.put("conclusion", Constant.DEFAULT_CONCLUSION_FILENAME);
            paramContent.put("children", new ArrayList<>());

            try{
                mapper.writeValue(manifest, paramContent);
                // create introduction and conclusion
                FunctionTreeFactory.generateMetadataAttributes(realLocalPath + File.separator);
                Content content = mapper.readValue(manifest, Content.class);
                content.setRootContent(content, realLocalPath);
                mainApp.setContent(content);
            }catch(IOException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    @FXML private void handleOpenButtonAction(ActionEvent event){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Configuration.getBundle().getString("ui.menu.dialog.open.title"));
        File defaultDirectory;

        if(MainApp.getConfig().getWorkspaceFactory() == null){
            MainApp.getConfig().loadWorkspace();
        }
        defaultDirectory = new File(MainApp.getZdsutils().getOfflineContentPathDir());
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(MainApp.getPrimaryStage());

        if(selectedDirectory != null){
            File manifest = new File(selectedDirectory.getAbsolutePath() + File.separator + "manifest.json");
            ObjectMapper mapper = new ObjectMapper();
            Content content;
            try{
                content = mapper.readValue(manifest, Content.class);
                content.setRootContent(content, selectedDirectory.getAbsolutePath());
                mainApp.setContent(content);
            }catch(IOException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void activateButtonForOpenContent() {
        menuExport.disableProperty().bind(mainApp.contentProperty().isNull());
        menuUpload.disableProperty().bind(mainApp.contentProperty().isNull());
        menuLisibility.disableProperty().bind(mainApp.contentProperty().isNull());
        menuReport.disableProperty().bind(mainApp.contentProperty().isNull());
    }

    @FXML public Service<Void> handleLoginButtonAction(ActionEvent event){
        // Button for google
        Button googleAuth = new Button(Configuration.getBundle().getString("ui.dialog.auth.google.title"), IconFactory.createGoogleIcon());
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
        downloadContentTask.setOnSucceeded(t -> {
            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.alert.download.success.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.alert.download.success.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.alert.download.success.text"));
            alert.showAndWait();
            hBottomBox.getChildren().clear();

            mainApp.getIndex().refreshRecentProject();
        });
        downloadContentTask.start();
    }

    @FXML private void handleDownloadButtonAction(ActionEvent event){
        if(! MainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = handleLoginButtonAction(event);
            loginTask.setOnSucceeded(t -> {
                downloadContents();
            });
            loginTask.setOnCancelled(t -> {
                hBottomBox.getChildren().clear();
                Alert alert = new CustomAlert(AlertType.ERROR);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.auth.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.auth.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.auth.failed.text"));

                alert.showAndWait();
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
            if(mainApp.getContent().isArticle()) {
                MainApp.getZdsutils().initInfoOnlineContent("article");
            } else {
                MainApp.getZdsutils().initInfoOnlineContent("tutorial");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }


        List<MetadataContent> contents = new ArrayList<>();
        contents.add(new MetadataContent(null, "---"+Configuration.getBundle().getString("ui.content.new.title")+"---", null));
        List<MetadataContent> possibleContent;
        if(mainApp.getContent().isArticle()) {
            possibleContent = MainApp.getZdsutils().getContentListOnline().stream()
                    .filter(MetadataContent::isArticle)
                    .collect(Collectors.toList());
        } else {
            possibleContent = MainApp.getZdsutils().getContentListOnline().stream()
                    .filter(MetadataContent::isTutorial)
                    .collect(Collectors.toList());
        }
        contents.addAll(possibleContent);

        Dialog<Pair<String, MetadataContent>> dialog = new CustomDialog<>();
        dialog.setTitle(Configuration.getBundle().getString("ui.content.select.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.content.select.header"));
        ButtonType loginButtonType = new ButtonType(Configuration.getBundle().getString("ui.content.select.button.send"), ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea msg = new TextArea();
        msg.setText(Configuration.getBundle().getString("ui.content.select.placeholder.commit_msg"));
        ChoiceBox<MetadataContent> contenus = new ChoiceBox<>();
        contenus.setItems(FXCollections.observableArrayList(contents));

        grid.add(new Label(Configuration.getBundle().getString("ui.content.select.field.slug")+" : "), 0, 0);
        grid.add(contenus, 1, 0);
        grid.add(new Label(Configuration.getBundle().getString("ui.content.select.field.commit_msg")+" : "), 0, 1);
        grid.add(msg, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(msg.getText(), contenus.getValue());
            }
            return null;
        });
        Optional<Pair<String, MetadataContent>> result = dialog.showAndWait();
        UploadContentService uploadContentTask = new UploadContentService(result, mainApp.getContent());
        labelField.textProperty().bind(uploadContentTask.messageProperty());
        uploadContentTask.setOnSucceeded(t -> {
            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.upload.content.success.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.upload.content.success.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.upload.content.success.text"));
            alert.showAndWait();
            hBottomBox.getChildren().clear();
        });
        uploadContentTask.setOnFailed(t -> {
            Alert alert = new CustomAlert(AlertType.ERROR);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.upload.content.failed.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.upload.content.failed.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.upload.content.failed.text"));
            alert.showAndWait();
        });
        if(result.isPresent()){
            Function<Textual, Boolean> checkExtractAvailability = (Textual ch) -> {
                File f = new File(ch.getFilePath());
                return f.exists();
            };
            Map<Textual, Boolean> analyse = mainApp.getContent().doOnTextual(checkExtractAvailability);
            if(analyse.entrySet().stream().filter(t -> !t.getValue()).count() == 0) {
                uploadContentTask.start();
            }
            else {
                Alert alert = new CustomAlert(AlertType.ERROR);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.upload.content.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.upload.content.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.upload.content.failed.text.nofile"));
                alert.showAndWait();
            }
        }
    }

    @FXML private void handleUploadButtonAction(ActionEvent event){
        if(! MainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = handleLoginButtonAction(event);
            loginTask.setOnCancelled(t -> {
                hBottomBox.getChildren().clear();
                Alert alert = new CustomAlert(AlertType.ERROR);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.upload.content.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.upload.content.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.upload.content.failed.text"));

                alert.showAndWait();
            });
            loginTask.setOnSucceeded(t -> {
                uploadContents();
            });
            loginTask.start();
        }else{
            uploadContents();
        }
    }

    @FXML private void handleSwitchWorkspaceAction(ActionEvent event) throws IOException{
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(MainApp.getDefaultHome());
        fileChooser.setTitle(Configuration.getBundle().getString("ui.dialog.switchworkspace"));
        File selectedDirectory = fileChooser.showDialog(MainApp.getPrimaryStage());
        if(selectedDirectory!=null) {
            MainApp.getConfig().setWorkspacePath(selectedDirectory.getAbsolutePath());
            MainApp.getConfig().loadWorkspace();

            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.options.workspace"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.workspace.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.workspace.text") + " " + MainApp.getConfig().getWorkspacePath());
            alert.setResizable(true);

            alert.showAndWait();
        }
    }

    @FXML private void handleExportMarkdownButtonAction(ActionEvent event){
        Content content = mainApp.getContent();
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(MainApp.getDefaultHome());
        fileChooser.setTitle(Configuration.getBundle().getString("ui.dialog.export.dir.title"));
        File selectedDirectory = fileChooser.showDialog(MainApp.getPrimaryStage());
        File selectedFile = new File(selectedDirectory, ZdsHttp.toSlug(content.getTitle()) + ".md");
        logger.debug("Tentative d'export vers le fichier " + selectedFile.getAbsolutePath());

        if(selectedDirectory != null){

            content.saveToMarkdown(selectedFile);
            logger.debug("Export réussi vers " + selectedFile.getAbsolutePath());

            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.export.success.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.export.success.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.export.success.text")+" \"" + selectedFile.getAbsolutePath() + "\"");
            alert.setResizable(true);

            alert.showAndWait();
        }
    }

    @FXML private void handleExportPdfButtonAction(ActionEvent event){
        Content content = mainApp.getContent();
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(MainApp.getDefaultHome());
        fileChooser.setTitle(Configuration.getBundle().getString("ui.dialog.export.dir.title"));
        File selectedDirectory = fileChooser.showDialog(MainApp.getPrimaryStage());
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
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.export.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.export.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.export.failed.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });

            exportPdfTask.setOnSucceeded((WorkerStateEvent ev) -> {
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.export.success.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.export.success.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.export.success.text")+" \"" + selectedFile.getAbsolutePath() + "\"");
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });
            exportPdfTask.start();
        }
    }

    @FXML private void handleMdCheatSheetButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/MdCheatSheetDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.getBundle().getString("ui.menu.help.md_cheat_sheet"));

        loader.getController();

        dialogStage.show();
    }

    @FXML private void handleFindReplaceAction(ActionEvent event){
        SplitPane sPane = (SplitPane) mainApp.getExtracts()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isSelected())
                .findFirst()
                .get()
                .getValue().getContent();
        BorderPane bPane = (BorderPane) sPane.getItems().get(0);
        StyleClassedTextArea source = (StyleClassedTextArea) bPane.getCenter();
        FunctionTreeFactory.openFindReplaceDialog(source);
    }

    @FXML private void handleAboutButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/AboutDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.getBundle().getString("ui.menu.help.about"));
        dialogStage.setResizable(false);

        AboutDialog aboutController = loader.getController();
        aboutController.setMainApp(mainApp);

        dialogStage.show();
    }

    @FXML private void handleOptionsButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/OptionsDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.getBundle().getString("ui.menu.options"));
        dialogStage.setResizable(false);


        OptionsDialog optionsController = loader.getController();
        optionsController.setWindow(dialogStage);

        dialogStage.show();
    }

    @FXML private void handleImportGithubButtonAction() {
        TextInputDialog dialog = new TextInputDialog("https://github.com/");
        dialog.setTitle(Configuration.getBundle().getString("ui.dialog.import.github.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.dialog.import.github.header"));
        dialog.setContentText(Configuration.getBundle().getString("ui.dialog.import.github.text")+" :");
        dialog.getEditor().setPrefWidth(500);
        dialog.initOwner(MainApp.getPrimaryStage());

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
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.download.github.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.download.github.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.download.github.failed.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });
            downloadGithubTask.setOnSucceeded (t -> {
                mainApp.setContent(downloadGithubTask.getValue ());
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.download.github.success.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.download.github.success.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.download.github.success.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });

            if(result.isPresent()){
                downloadGithubTask.start();
            }
        });
    }

    @FXML private void handleImportZdsButtonAction() {
        TextInputDialog dialog = new TextInputDialog("https://zestedesavoir.com/");
        dialog.setTitle(Configuration.getBundle().getString("ui.dialog.import.zds.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.dialog.import.zds.header"));
        dialog.setContentText(Configuration.getBundle().getString("ui.dialog.import.zds.text")+" :");
        dialog.getEditor().setPrefWidth(500);
        dialog.initOwner(MainApp.getPrimaryStage());

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
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.download.zds.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.download.zds.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.download.zds.failed.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });
            downloadZdsTask.setOnSucceeded (t -> {
                mainApp.setContent(downloadZdsTask.getValue ());
                alert.setAlertType(AlertType.INFORMATION);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.download.zds.success.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.download.zds.success.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.download.zds.success.text"));
                alert.showAndWait();
                hBottomBox.getChildren().clear();
            });

            if(result.isPresent()){
                downloadZdsTask.start();
            }
        });
    }

    @FXML private void handleCheckUpdateButtonAction(ActionEvent event){
        Service<Boolean> checkService = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        String current = MainApp.getConfig().getProps().getProperty("version", Configuration.getBundle().getString("ui.version.label.unknown"));
                        String versionOnline = Configuration.getLastRelease();
                        if(versionOnline == null) {
                            throw new IOException();
                        } else {
                            String[] localeTab = current.split(".");
                            for(String s:localeTab) {
                                if(!StringUtils.isNumeric(s)) {
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
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.check_update.failed.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.check_update.failed.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.check_update.failed.text"));
            alert.showAndWait();
        });

        checkService.setOnSucceeded(t -> {
            Alert alert = new CustomAlert(AlertType.NONE);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.check_update.success.title"));

            if(!checkService.getValue()){
                alert.setAlertType(AlertType.WARNING);
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.check_update.warn.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.check_update.warn.text"));
            }else{
                alert.setAlertType(AlertType.INFORMATION);
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.check_update.success.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.check_update.success.text"));
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
