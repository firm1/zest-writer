package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.Pair;

public class LoginDialog extends BaseDialog<Pair<String, String>> {
    private Configuration config;

	public LoginDialog(Button googleButton) {
		super(Configuration.bundle.getString("ui.dialog.auth.title"), Configuration.bundle.getString("ui.dialog.auth.header"));
        this.config = MainApp.getConfig();

        this.setGraphic(IconFactory.createLoginIcon());

        // Set the button types.
        ButtonType loginButtonType = new ButtonType(Configuration.bundle.getString("ui.dialog.auth.button"), ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        TextField username = new TextField();
        username.setPromptText("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");
        CheckBox keepConnection = new CheckBox(Configuration.bundle.getString("ui.dialog.auth.stay"));

        getGridPane().add(googleButton, 0, 0, 1, 2);
        getGridPane().add(new Label(Configuration.bundle.getString("ui.dialog.auth.username")+":"), 1, 0);
        getGridPane().add(username, 2, 0);
        getGridPane().add(new Label(Configuration.bundle.getString("ui.dialog.auth.password")+":"), 1, 1);
        getGridPane().add(password, 2, 1);
        getGridPane().add(keepConnection, 2, 2);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = this.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        keepConnection.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(keepConnection.isSelected()){
                Alert alert = new CustomAlert(Alert.AlertType.WARNING);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.warning.title"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.auth.warning"));

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
