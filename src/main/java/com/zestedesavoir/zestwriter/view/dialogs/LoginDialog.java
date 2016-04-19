package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.IconFactory;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;

public class LoginDialog extends BaseDialog<Pair<String, String>> {
    private Configuration config;
    private MainApp mainApp;

	public LoginDialog(Button googleButton, MainApp mainApp) {
		super("Connexion", "Connectez vous au site Zeste de Savoir");
        this.mainApp = mainApp;
        this.config = this.mainApp.getConfig();

        this.setGraphic(IconFactory.createLoginIcon());

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Se connecter", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        TextField username = new TextField();
        username.setPromptText("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");
        CheckBox keepConnection = new CheckBox("Rester connecté");

        getGridPane().add(googleButton, 0, 0, 1, 2);
        getGridPane().add(new Label("Nom d'utilisateur:"), 1, 0);
        getGridPane().add(username, 2, 0);
        getGridPane().add(new Label("Mot de passe:"), 1, 1);
        getGridPane().add(password, 2, 1);
        getGridPane().add(keepConnection, 2, 2);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = this.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        keepConnection.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(keepConnection.isSelected()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention !");
                alert.setContentText("Attention, vos informations d'identification ne sont pas crypté dans le fichier de configuration.");
                Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("static/icons/logo.png")));

                alert.showAndWait();
            }
        });

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        this.getDialogPane().setContent(getGridPane());

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                if(keepConnection.isSelected()){
                    config.setAuthentificationUsername(username.getText());
                    config.setAuthentificationPassword(password.getText());
                    config.saveConfFile();
                }

                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
	}
}
