package com.zestedesavoir.zestwriter.view;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.ZipUtil;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

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

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void HandleQuitButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void correctChildren(TreeItem<ExtractFile> root, boolean typo) throws IOException {
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

                Corrector cr = new Corrector();
                if (!typo) {
                    cr.ignoreRule("FRENCH_WHITESPACE");
                }
                String htmlText = StringEscapeUtils.unescapeHtml(markdownToHtml(mainApp.getIndex(), markdown));
                textArea.appendText(cr.checkHtmlContentToText(htmlText, child.getValue().getTitle().getValue()));
            } else {
                correctChildren(child, typo);
            }
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
    private void HandleReportWithTypoButtonAction(ActionEvent event) throws IOException {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Validation du contenu");
        alert.setHeaderText("Rapport de validation du contenu prêt à être copié sur ZdS");

        Label label = new Label("Liste des erreurs dans votre contenu");

        textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setPrefSize(760, 700);

        correctChildren(mainApp.getIndex().getSummary().getRoot(), true);

        alert.showAndWait();
    }

    @FXML
    private void HandleReportWithoutTypoButtonAction(ActionEvent event) throws IOException {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Validation du contenu");
        alert.setHeaderText("Rapport de validation du contenu prêt à être copié sur ZdS");

        Label label = new Label("The exception stacktrace was:");

        textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        correctChildren(mainApp.getIndex().getSummary().getRoot(), false);

        alert.showAndWait();
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

        if (mainApp.getZdsutils().isAuthenticated()) {
            menuUpload.setDisable(false);
        }

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
    private void HandleLoginButtonAction(ActionEvent event) {
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

        result.ifPresent(usernamePassword -> {
            try {
                mainApp.getZdsutils().login(usernamePassword.getKey(), usernamePassword.getValue());

                menuDownload.setDisable(false);
                menuLogin.setDisable(true);
                menuLogout.setDisable(false);
                if (mainApp.getContents().containsKey("dir")) {
                    menuUpload.setDisable(false);
                }
                mainApp.getZdsutils().initInfoOnlineContent("tutorial");
                mainApp.getZdsutils().initInfoOnlineContent("article");

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Connexion");
                alert.setHeaderText("Confirmation de connexion");
                alert.setContentText("Félicitations, vous êtes a présent connecté.");

                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Connexion");
                alert.setHeaderText("Erreur de connexion");
                alert.setContentText("Désolé mais vous n'avez pas été authentifié sur le serveur de Zeste de Savoir.");

                alert.showAndWait();
            }
        });
    }

    @FXML
    private void HandleDownloadButtonAction(ActionEvent event) {
        if (mainApp.getZdsutils().isAuthenticated()) {
            try {

                for (MetadataContent meta : mainApp.getZdsutils().getContentListOnline()) {
                    mainApp.getZdsutils().downloaDraft(meta.getId(), meta.getType());
                    mainApp.getZdsutils().unzipOnlineContent(mainApp.getZdsutils().getOnlineContentPathDir() + File.separator + meta.getSlug() + ".zip");
                }

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Connexion");
                alert.setHeaderText("Confirmation de connexion");
                alert.setContentText("Vos contenus (tutoriels et articles) de ZdS ont été téléchargés en local. \nVous pouvez maintenant travailler en mode Hors ligne !");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Connexion");
                alert.setHeaderText("Erreur de connexion");
                alert.setContentText("Désolé mais un problème vous empêche de télécharger le contenu de ZdS en local.");

                alert.showAndWait();
            }
        }
    }

    @FXML
    private void HandleUploadButtonAction(ActionEvent event) {
        List<MetadataContent> contents = mainApp.getZdsutils().getContentListOnline();

        ChoiceDialog<MetadataContent> dialog = new ChoiceDialog<>(null, contents);
        dialog.setTitle("Choix du tutoriel");
        dialog.setHeaderText("Choisissez le tutoriel vers lequel importer");
        dialog.setContentText("Tutoriel: ");

        // Traditional way to get the response value.
        Optional<MetadataContent> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (mainApp.getZdsutils().isAuthenticated()) {
                String targetId = result.get().getId();
                String localSlug = mainApp.getZdsutils().getLocalSlug();
                String targetSlug = result.get().getSlug();
                try {
                    String pathDir = mainApp.getZdsutils().getOfflineContentPathDir() + File.separator + localSlug;
                    ZipUtil.zipContent(pathDir, pathDir + ".zip");
                    mainApp.getZdsutils().importContent(pathDir + ".zip", targetId, targetSlug);

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Import de contenu");
                    alert.setHeaderText("Confirmation de l'import");
                    alert.setContentText("Votre contenu à été importé sur ZdS avec succès !");
                    alert.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Import de contenu");
                    alert.setHeaderText("Erreur d'import");
                    alert.setContentText("Désolé mais un problème vous empêche d'importer votre contenu sur ZdS");

                    alert.showAndWait();
                }
            }
        }
    }

}
