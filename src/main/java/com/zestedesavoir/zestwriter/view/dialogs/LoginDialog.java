package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.view.com.IconFactory;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Pair;

public class LoginDialog extends BaseDialog<Pair<String, String>> {
	public LoginDialog(Button googleButton) {
		super("Connexion", "Connectez vous au site Zeste de Savoir");

        this.setGraphic(IconFactory.createLoginIcon());

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Se connecter", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);



        TextField username = new TextField();
        username.setPromptText("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");

        getGridPane().add(googleButton, 0, 0, 1, 2);
        getGridPane().add(new Label("Nom d'utilisateur:"), 1, 0);
        getGridPane().add(username, 2, 0);
        getGridPane().add(new Label("Mot de passe:"), 1, 1);
        getGridPane().add(password, 2, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = this.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        this.getDialogPane().setContent(getGridPane());

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
	}
}
