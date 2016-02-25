package com.zestedesavoir.zestwriter.view;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.lang.StringEscapeUtils;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.ZipUtil;
import com.zestedesavoir.zestwriter.utils.readability.Readability;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;

public class MenuController {
    private MainApp mainApp;
    TextArea textArea;

    @FXML
    MenuItem menuDownload;
    @FXML
    MenuItem menuUpload;
    @FXML
    MenuItem menuLogin;
    @FXML
    MenuItem menuLogout;
    @FXML
    MenuItem menuReport;
    @FXML
    MenuItem menuLisibility;
    @FXML
    HBox hBottomBox;
    final ProgressBar pb = new ProgressBar(0);
    final Text labelField = new Text("");

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void HandleQuitButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private List<ExtractFile> getExtractFilesFromTree(TreeItem<ExtractFile> node) {
        List<ExtractFile> extractFiles = new ArrayList<>();
        for (TreeItem<ExtractFile> child : node.getChildren()) {
            if (child.getChildren().isEmpty()) {
                extractFiles.add(child.getValue());
            }
            else {
                extractFiles.addAll(getExtractFilesFromTree(child));
            }
        }
        return extractFiles;
    }

    private void correctChildren(TreeItem<ExtractFile> root, boolean typo) throws IOException {
        List<ExtractFile> myExtracts = getExtractFilesFromTree(root);
        for(ExtractFile extract:myExtracts) {
            String markdown = "";
            // load mdText
            Path path = Paths.get(extract.getFilePath());
            Scanner scanner;
            StringBuilder bfString = new StringBuilder();
            try {
                scanner = new Scanner(path, StandardCharsets.UTF_8.name());
                while (scanner.hasNextLine()) {
                    bfString.append(scanner.nextLine());
                    bfString.append("\n");
                }
                markdown = bfString.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Corrector cr = new Corrector();
            if (!typo) {
                cr.ignoreRule("FRENCH_WHITESPACE");
            }
            String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), markdown));
            textArea.appendText(cr.checkHtmlContentToText(htmlText, extract.getTitle().getValue()));
        }
    }

    public Map<ExtractFile, Double> getGunning(TreeItem<ExtractFile> root) {

        Map<ExtractFile, Double> map = new HashMap<>();
        for (TreeItem<ExtractFile> child : root.getChildren()) {
            String markdown = "";
            if (child.getChildren().isEmpty()) {
                // load mdText
                Path path = Paths.get(child.getValue().getFilePath());
                Scanner scanner;
                StringBuilder bfString = new StringBuilder();
                try {
                    scanner = new Scanner(path, StandardCharsets.UTF_8.name());
                    while (scanner.hasNextLine()) {
                        bfString.append(scanner.nextLine());
                        bfString.append("\n");
                    }
                    markdown = bfString.toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), markdown));
                String plainText = Corrector.HtmlToTextWithoutCode(htmlText);
                Readability rd = new Readability(plainText);
                map.put(child.getValue(), rd.getGunningFog());
            } else {
                map.putAll(getGunning(child));
            }
        }
        return map;
    }

    public Map<ExtractFile, Double> getFlesch(TreeItem<ExtractFile> root) {

        Map<ExtractFile, Double> map = new HashMap<>();
        for (TreeItem<ExtractFile> child : root.getChildren()) {
            String markdown = "";
            if (child.getChildren().isEmpty()) {
                // load mdText
                Path path = Paths.get(child.getValue().getFilePath());
                Scanner scanner;
                StringBuilder bfString = new StringBuilder();
                try {
                    scanner = new Scanner(path, StandardCharsets.UTF_8.name());
                    while (scanner.hasNextLine()) {
                        bfString.append(scanner.nextLine());
                        bfString.append("\n");
                    }
                    markdown = bfString.toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), markdown));
                String plainText = Corrector.HtmlToTextWithoutCode(htmlText);
                Readability rd = new Readability(plainText);
                map.put(child.getValue(), rd.getFleschReadingEase());
            } else {
                map.putAll(getGunning(child));
            }
        }
        return map;
    }

    public String markdownToHtml(MdTextController index, String chaine) {
        PythonInterpreter console = index.getPyconsole();
        console.set("text", chaine);
        console.exec("render = Markdown(extensions=(ZdsExtension({'inline': False, 'emoticons': smileys}),),safe_mode = 'escape', enable_attributes = False, tab_length = 4, output_format = 'html5', smart_emphasis = True, lazy_ol = True).convert(text)");
        PyString render = console.get("render", PyString.class);
        return render.toString();
    }

    @FXML
    private void HandleFleshButtonAction(ActionEvent event) {
        Map<ExtractFile, Double> results = getFlesch(mainApp.getIndex().getSummary().getRoot());

        ObservableList<String> rows = FXCollections.observableArrayList();
        for (Entry<ExtractFile, Double> entry : results.entrySet()) {
            String easy;
            if (entry.getValue() < 30) {
                easy = "très difficile";
            } else if (entry.getValue() < 50) {
                easy = "difficile";
            } else if (entry.getValue() < 60) {
                easy = "assez difficile";
            } else if (entry.getValue() < 70) {
                easy = "standard";
            } else if (entry.getValue() < 80) {
                easy = "assez facile";
            } else {
                easy = "très facile";
            }

            String v1 = entry.getKey().getTitle().getValue();
            String v2 = entry.getValue().toString() + " (" + easy + ")";
            rows.add(v1 + " => " + v2);
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Indice de Flesch");
        dialog.setHeaderText("Indice de Rudolph Flesch");

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ListView<String> list = new ListView<>();
        list.setPrefSize(800, 500);
        list.setItems(rows);


        dialog.getDialogPane().setContent(list);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return null;
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
    }

    @FXML
    private void HandleGunningButtonAction(ActionEvent event) {
        Map<ExtractFile, Double> results = getGunning(mainApp.getIndex().getSummary().getRoot());

        ObservableList<String> rows = FXCollections.observableArrayList();
        for (Entry<ExtractFile, Double> entry : results.entrySet()) {
            String easy;
            if (entry.getValue() >= 15) {
                easy = "très difficile";
            } else if (entry.getValue() >= 12) {
                easy = "difficile";
            } else if (entry.getValue() >= 10) {
                easy = "assez difficile";
            } else if (entry.getValue() >= 8) {
                easy = "standard";
            } else if (entry.getValue() >= 6) {
                easy = "assez facile";
            } else {
                easy = "très facile";
            }

            String v1 = entry.getKey().getTitle().getValue();
            String v2 = entry.getValue().toString() + " (" + easy + ")";
            rows.add(v1 + " => " + v2);
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Indice de Gunning");
        dialog.setHeaderText("Indice de Gunning");

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ListView<String> list = new ListView<>();
        list.setPrefSize(800, 500);
        list.setItems(rows);


        dialog.getDialogPane().setContent(list);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return null;
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
    }

    @FXML
    private void HandleReportWithoutTypoButtonAction(ActionEvent event) {
        textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label("Rapport de correction"), 0, 0);
        expContent.add(textArea, 0, 1);

        hBottomBox.getChildren().addAll(labelField);
        List<ExtractFile> myExtracts = getExtractFilesFromTree(mainApp.getIndex().getSummary().getRoot());
        MdTextController mdText = mainApp.getIndex();
        Corrector corrector = new Corrector();
        corrector.ignoreRule("FRENCH_WHITESPACE");

        Service<String> correctTask = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call(){
                        updateMessage("Préparation du rapport de validation ...");
                        StringBuilder resultCorrect = new StringBuilder();
                        for(ExtractFile extract:myExtracts) {
                            updateMessage("Préparation du rapport de validation de "+extract.getTitle().getValue());
                            String markdown = "";
                            // load mdText
                            Path path = Paths.get(extract.getFilePath());
                            Scanner scanner;
                            StringBuilder bfString = new StringBuilder();
                            try {
                                scanner = new Scanner(path, StandardCharsets.UTF_8.name());
                                while (scanner.hasNextLine()) {
                                    bfString.append(scanner.nextLine());
                                    bfString.append("\n");
                                }
                                markdown = bfString.toString();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mdText, markdown));
                            String note = corrector.checkHtmlContentToText(htmlText, extract.getTitle().getValue());
                            resultCorrect.append(note);
                        }
                        return resultCorrect.toString();
                    }
                };
            }
        };
        labelField.textProperty().bind(correctTask.messageProperty());
        textArea.textProperty().bind(correctTask.valueProperty());
        correctTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
        	Alert alert;
            switch (newValue) {
                case FAILED:
                	alert = new Alert(AlertType.ERROR);
                	alert.setTitle("Validation du contenu");
                    alert.setHeaderText("Erreur de validation");
                    alert.setContentText("Désolé une erreur nous empêche de trouver les erreurs dans votre contenu");

                    alert.showAndWait();
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Validation du contenu");
                    alert.setHeaderText("Rapport de validation du contenu prêt à être copié sur ZdS");

                    // Set expandable Exception into the dialog pane.
                    alert.getDialogPane().setExpandableContent(expContent);
                    alert.showAndWait();
                    break;
            }
            hBottomBox.getChildren().clear();
        });
        correctTask.start();
    }

    @FXML
    private void HandleOpenButtonAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Contenus Zestueux");
        File defaultDirectory = new File(mainApp.getZdsutils().getOfflineContentPathDir());
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(mainApp.getPrimaryStage());

        if (selectedDirectory != null) {
            mainApp.getContents().put("dir", selectedDirectory.getAbsolutePath());
        }

        menuUpload.setDisable(false);

        menuLisibility.setDisable(false);
        menuReport.setDisable(false);
    }

    @FXML
    private void HandleLogoutButtonAction(ActionEvent event) {
        if (mainApp.getZdsutils().isAuthenticated()) {
            mainApp.getZdsutils().logout();
            menuDownload.setDisable(true);
            menuLogin.setDisable(false);
            menuLogout.setDisable(true);
        }
    }

    @FXML
    private Service<Void> HandleLoginButtonAction(ActionEvent event) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connexion");
        dialog.setHeaderText("Connectez vous au site Zeste de Savoir pour synchroniser vos contenus en ligne et hors ligne");

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(this.getClass().getResource("static/icons/login.png").toString()));

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Se connecter", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");

        grid.add(new Label("Nom d'utilisateur:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        hBottomBox.getChildren().addAll(labelField);
        Service<Void> loginTask = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        result.ifPresent(usernamePassword -> {
                            try {
                                if(mainApp.getZdsutils().login(usernamePassword.getKey(), usernamePassword.getValue())) {
                                    updateMessage("Connexion au site en cours ...");
                                    updateMessage("Recherche des contenus ...");
                                    mainApp.getZdsutils().initInfoOnlineContent("tutorial");
                                    mainApp.getZdsutils().initInfoOnlineContent("article");
                                }
                                else {
                                    cancel();
                                }
                            } catch (Exception e) {
                                cancel();
                            }
                        });
                        if(!result.isPresent()) {
                            cancel();
                        }
                        return null;
                    }
                };
            }
        };
        labelField.textProperty().bind(loginTask.messageProperty());

        return loginTask;
    }

    private void downloadContents() {
        hBottomBox.getChildren().clear();
        hBottomBox.getChildren().addAll(pb, labelField);

        Service<Void> downloadContentTask = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        int max = mainApp.getZdsutils().getContentListOnline().size();
                        int iterations = 0;
                        if (mainApp.getZdsutils().isAuthenticated()) {
                            for (MetadataContent meta : mainApp.getZdsutils().getContentListOnline()) {
                                updateMessage("Téléchargement : " + meta.getSlug());
                                updateProgress(iterations, max);
                                mainApp.getZdsutils().downloaDraft(meta.getId(), meta.getType());
                                iterations++;
                            }

                            iterations = 0;
                            for (MetadataContent meta : mainApp.getZdsutils().getContentListOnline()) {
                                updateMessage("Décompression : " + meta.getSlug());
                                updateProgress(iterations, max);
                                mainApp.getZdsutils().unzipOnlineContent(mainApp.getZdsutils().getOnlineContentPathDir() + File.separator + meta.getSlug() + ".zip");
                                iterations++;
                            }
                            updateMessage("Terminé");
                            updateProgress(iterations, max);
                        }
                        return null;
                    }
                };
            }
        };
        labelField.textProperty().bind(downloadContentTask.messageProperty());
        pb.progressProperty().bind(downloadContentTask.progressProperty());
        downloadContentTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                case CANCELLED:
                case SUCCEEDED:
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Téléchargement des contenus");
                    alert.setHeaderText("Confirmation du téléchargement");
                    alert.setContentText("Vos contenus (tutoriels et articles) de ZdS ont été téléchargés en local. \n" +
                            "Vous pouvez maintenant travailler en mode Hors ligne !");
                    alert.showAndWait();
                    hBottomBox.getChildren().clear();
                    break;
            }
        });

        downloadContentTask.start();
    }

    @FXML
    private void HandleDownloadButtonAction(ActionEvent event) {
        if (!mainApp.getZdsutils().isAuthenticated()) {
            Service<Void> loginTask = HandleLoginButtonAction(event);

            loginTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                Alert alert;
                switch (newValue) {
                    case FAILED:
                    case CANCELLED:
                        hBottomBox.getChildren().clear();
                        alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Connexion");
                        alert.setHeaderText("Erreur de connexion");
                        alert.setContentText("Désolé mais vous n'avez pas été authentifié sur le serveur de Zeste de Savoir.");

                        alert.showAndWait();
                        break;
                    case SUCCEEDED:
                        if (mainApp.getContents().containsKey("dir")) {
                            menuUpload.setDisable(false);
                        }
                        downloadContents();
                        break;
                }
            });
            loginTask.start();
        } else {
            downloadContents();
        }

    }

    private void uploadContents() {
        hBottomBox.getChildren().clear();
        hBottomBox.getChildren().addAll(labelField);

        List<MetadataContent> contents = mainApp.getZdsutils().getContentListOnline();

        ChoiceDialog<MetadataContent> dialog = new ChoiceDialog<>(null, contents);
        dialog.setTitle("Choix du tutoriel");
        dialog.setHeaderText("Choisissez le tutoriel vers lequel importer");
        dialog.setContentText("Tutoriel: ");

        Optional<MetadataContent> result = dialog.showAndWait();

        Service<Void> uploadContentTask = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (mainApp.getZdsutils().isAuthenticated()) {
                            String targetId = result.get().getId();
                            String localSlug = mainApp.getZdsutils().getLocalSlug();
                            String targetSlug = result.get().getSlug();
                            try {
                                String pathDir = mainApp.getZdsutils().getOfflineContentPathDir() + File.separator + localSlug;
                                updateMessage("Compression : "+targetSlug+" en cours ...");
                                ZipUtil.zipContent(pathDir, pathDir + ".zip");
                                updateMessage("Import : "+targetSlug+" en cours ...");
                                mainApp.getZdsutils().importContent(pathDir + ".zip", targetId, targetSlug);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                };
            }
        };
        labelField.textProperty().bind(uploadContentTask.messageProperty());
        uploadContentTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            Alert alert;
            switch (newValue) {
                case FAILED:
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Import de contenu");
                    alert.setHeaderText("Erreur d'import");
                    alert.setContentText("Désolé mais un problème vous empêche d'importer votre contenu sur ZdS");
                    alert.showAndWait();
                    break;
                case CANCELLED:
                    break;
                case SUCCEEDED:
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Import de contenu");
                    alert.setHeaderText("Confirmation de l'import");
                    alert.setContentText("Votre contenu à été importé sur ZdS avec succès !");
                    alert.showAndWait();
                    hBottomBox.getChildren().clear();
                    break;
            }
        });

        if (result.isPresent()) {
            uploadContentTask.start();
        }
    }
    @FXML
    private void HandleUploadButtonAction(ActionEvent event) {
        if (!mainApp.getZdsutils().isAuthenticated()) {
            Service<Void> loginTask = HandleLoginButtonAction(event);

            loginTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                Alert alert;
                switch (newValue) {
                    case FAILED:
                        break;
                    case CANCELLED:
                        hBottomBox.getChildren().clear();
                        alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Connexion");
                        alert.setHeaderText("Erreur de connexion");
                        alert.setContentText("Désolé mais vous n'avez pas été authentifié sur le serveur de Zeste de Savoir.");

                        alert.showAndWait();
                        break;
                    case SUCCEEDED:
                        if (mainApp.getContents().containsKey("dir")) {
                            menuUpload.setDisable(false);
                        }
                        uploadContents();
                        break;
                }
            });
            loginTask.start();
        } else {
            uploadContents();
        }

    }

    @FXML
    private void HandleSwitchWorkspaceAction(ActionEvent event) throws IOException {
        String oldStr = mainApp.getZdsutils().getWorkspace();
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Sélectionnez un dossier");
        File selectedDirectory = fileChooser.showDialog(mainApp.getPrimaryStage());
        mainApp.getZdsutils().switchWorkspace(selectedDirectory.getAbsolutePath(), mainApp.getProps());
        String newStr = mainApp.getZdsutils().getWorkspace();

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Dossier de travail");
        alert.setHeaderText("Changement de dossier de travail");
        alert.setContentText("Votre dossier de travail est maintenant dans "+mainApp.getZdsutils().getWorkspace());
        alert.setResizable(true);

        alert.showAndWait();
    }

}
