package com.zds.zw.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.zw.MainApp;
import com.zds.zw.model.*;
import com.zds.zw.utils.Configuration;
import com.zds.zw.utils.HtmlToPlainText;
import com.zds.zw.utils.ZMD;
import com.zds.zw.utils.ZdsHttp;
import com.zds.zw.utils.readability.Readability;
import com.zds.zw.view.com.*;
import com.zds.zw.view.dialogs.*;
import com.zds.zw.view.task.*;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
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

import static com.zds.zw.utils.Configuration.isNumeric;

public class MenuController{
    private final ProgressBar pb = new ProgressBar(0);
    private final Text labelField = new Text("");
    private MainApp mainApp;
    @FXML private MenuItem menuDownload;
    @FXML private MenuItem menuUpload;
    @FXML private MenuItem menuLisibility;
    @FXML private GridPane hBottomBox;
    @FXML private Menu menuExport;
    @FXML private MenuItem menuQuit;
    @FXML private MenuItem menuFindReplace;
    private final BooleanPropertyBase isOnReadingTab = new SimpleBooleanProperty(true);
    private final Logger log = LoggerFactory.getLogger(getClass());

    public MenuController() {
    }

    public Text getLabelField() {
        return labelField;
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public MenuItem getMenuDownload() {
        return menuDownload;
    }

    public void setMenuDownload(MenuItem menuDownload) {
        this.menuDownload = menuDownload;
    }

    public GridPane getHBottomBox() {
        return hBottomBox;
    }

    public void setHBottomBox(GridPane hBottomBox) {
        this.hBottomBox = hBottomBox;
    }

    public static String markdownToHtml(MdTextController index, String chaine) {
        ZMD zmd = index.getZmd();
        return zmd.toHtml(chaine);
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
            String htmlText = StringEscapeUtils.unescapeHtml4(markdownToHtml(mainApp.getIndex(), ch.readMarkdown()));
            String plainText = new HtmlToPlainText().getPlainText(Jsoup.parse(htmlText), "pre", "table");
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
            String htmlText = StringEscapeUtils.unescapeHtml4(markdownToHtml(mainApp.getIndex(), ch.readMarkdown()));
            String plainText = new HtmlToPlainText().getPlainText(Jsoup.parse(htmlText), "pre", "table");
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
            paramContent.put("version", "2.1");
            paramContent.put("ready_to_publish", true);
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
                log.error(e.getMessage(), e);
            }
        }
    }

    @FXML private void handleOpenButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/OpenContent.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.getBundle().getString("ui.menu.dialog.content.open.title"));
        dialogStage.setResizable(false);
        OpenContent openContentDialog = loader.getController();
        openContentDialog.setMainApp(mainApp);
        openContentDialog.setOpenContentWindow(dialogStage);

        dialogStage.show();
    }

    public void activateButtonForOpenContent() {
        menuExport.disableProperty().bind(mainApp.contentProperty().isNull());
        menuUpload.disableProperty().bind(mainApp.contentProperty().isNull());
        menuLisibility.disableProperty().bind(mainApp.contentProperty().isNull());
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

    private void downloadContents(String typeContent){
        prerequisitesForData();

        hBottomBox.getChildren().clear();
        hBottomBox.add(pb, 0, 0);
        hBottomBox.add(labelField, 1, 0);
        DownloadContentService downloadContentTask = new DownloadContentService(typeContent);
        labelField.textProperty().bind(downloadContentTask.messageProperty());
        pb.progressProperty().bind(downloadContentTask.progressProperty());
        downloadContentTask.setOnSucceeded(t -> {
            hBottomBox.getChildren().clear();

            Map<Content, Map<String, List<Map<File, String>>>> result = downloadContentTask.getValue();
            result.entrySet().stream()
                    .filter(r -> r.getValue() != null)
                    .forEach(c -> {
                        c.getValue().get("update").stream().forEach(elt -> {
                            elt.entrySet().stream().forEach(map -> {
                                DiffDisplayDialog confirm = new DiffDisplayDialog(map.getKey(), map.getValue(), c.getKey().getTitle(), map.getKey().getName());
                                confirm.setGraphic(c.getKey().buildIcon());
                                Optional<Boolean> choice = confirm.showAndWait();
                                if (choice.get()) {
                                    try {
                                        FileUtils.writeStringToFile(map.getKey(), map.getValue(), "UTF-8");
                                    } catch (IOException e) {
                                        log.error(e.getMessage(), e);
                                    }
                                }
                            });
                        });
                        c.getValue().get("add").stream().forEach(elt -> {
                            elt.entrySet().stream().forEach(map -> {
                                DiffDisplayDialog confirm = new DiffDisplayDialog(map.getKey(), map.getValue(), c.getKey().getTitle(), map.getKey().getName());
                                confirm.setGraphic(c.getKey().buildIcon());
                                Optional<Boolean> choice = confirm.showAndWait();
                                if (choice.get()) {
                                    try {
                                        FileUtils.writeStringToFile(map.getKey(), map.getValue(), "UTF-8");
                                    } catch (IOException e) {
                                        log.error(e.getMessage(), e);
                                    }
                                }
                            });
                        });
                    });

            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.alert.download.success.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.alert.download.success.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.alert.download.success.text"));
            alert.showAndWait();

            mainApp.getIndex().refreshRecentProject();
        });
        downloadContentTask.start();
    }

    @FXML private void handleDownloadButtonAction(ActionEvent event){
        if(! MainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = handleLoginButtonAction(event);
            loginTask.setOnSucceeded(t -> downloadContents(null));
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
            downloadContents(null);
        }
    }

    @FXML private void handleDownloadArticleButtonAction(ActionEvent event){
        if(! MainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = handleLoginButtonAction(event);
            loginTask.setOnSucceeded(t -> downloadContents("ARTICLE"));
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
            downloadContents("ARTICLE");
        }
    }

    @FXML private void handleDownloadTutorialButtonAction(ActionEvent event){
        if(! MainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = handleLoginButtonAction(event);
            loginTask.setOnSucceeded(t -> downloadContents("TUTORIAL"));
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
            downloadContents("TUTORIAL");
        }
    }

    @FXML private void handleDownloadOpinionButtonAction(ActionEvent event){
        if(! MainApp.getZdsutils().isAuthenticated()){
            Service<Void> loginTask = handleLoginButtonAction(event);
            loginTask.setOnSucceeded(t -> downloadContents("OPINION"));
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
            downloadContents("OPINION");
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
            } else if(mainApp.getContent().isOpinion()) {
                MainApp.getZdsutils().initInfoOnlineContent("opinion");
            } else {
                    MainApp.getZdsutils().initInfoOnlineContent("tutorial");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }


        List<MetadataContent> contents = new ArrayList<>();
        contents.add(new MetadataContent(null, "---"+Configuration.getBundle().getString("ui.content.new.title")+"---", null));
        List<MetadataContent> possibleContent;
        if(mainApp.getContent().isArticle()) {
            possibleContent = MainApp.getZdsutils().getContentListOnline().stream()
                    .filter(MetadataContent::isArticle)
                    .collect(Collectors.toList());
        } else if(mainApp.getContent().isOpinion()) {
            possibleContent = MainApp.getZdsutils().getContentListOnline().stream()
                    .filter(MetadataContent::isOpinion)
                    .collect(Collectors.toList());
        }
        else {
            possibleContent = MainApp.getZdsutils().getContentListOnline().stream()
                    .filter(MetadataContent::isTutorial)
                    .collect(Collectors.toList());
        }
        contents.addAll(possibleContent);

        Optional<MetadataContent> selectedContent = MainApp.getZdsutils().getContentListOnline().stream()
                .filter(t -> t.getSlug().equals(mainApp.getContent().getSlug()))
                .findFirst();


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
        selectedContent.ifPresent(metadataContent -> contenus.getSelectionModel().select(metadataContent));

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
            loginTask.setOnSucceeded(t -> uploadContents());
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
        log.debug("Tentative d'export vers le fichier " + selectedFile.getAbsolutePath());

        if(selectedDirectory != null){

            content.saveToMarkdown(selectedFile);
            log.debug("Export réussi vers " + selectedFile.getAbsolutePath());

            Alert alert = new CustomAlert(AlertType.INFORMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.export.success.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.export.success.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.export.success.text")+" \"" + selectedFile.getAbsolutePath() + "\"");
            alert.setResizable(true);

            alert.showAndWait();
        }
    }

    @FXML private void handleMdCheatSheetButtonAction(ActionEvent event){
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/MdCheatSheetDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.getBundle().getString("ui.menu.help.md_cheat_sheet"));

        loader.getController();

        dialogStage.show();
    }

    @FXML private void handleFindReplaceAction(ActionEvent event){
        SplitPane sPane = (SplitPane) mainApp.getIndex().getEditorList().getTabs().stream()
                .filter(t ->t.isSelected())
                .findFirst()
                .get()
                .getContent();
        BorderPane bPane = (BorderPane) sPane.getItems().get(0);
        TextArea source = (TextArea) bPane.getCenter();
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

        dialogStage.showAndWait();
        mainApp.getIndex().refreshRecentProject();
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

            result.ifPresent(s -> downloadGithubTask.start());
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

            result.ifPresent(s -> downloadZdsTask.start());
        });
    }

    @FXML private void handleCheckUpdateButtonAction(ActionEvent event){
        Service<Boolean> checkService = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        String current = MainApp.getVersion();
                        String versionOnline = Configuration.getLastRelease();
                        if(versionOnline == null) {
                            throw new IOException();
                        } else {
                            String[] localeTab = current.split(".");
                            for(String s:localeTab) {
                                if(!isNumeric(s)) {
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
}
