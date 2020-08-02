package com.zds.zw.view.dialogs;


import com.zds.zw.MainApp;
import com.zds.zw.model.Content;
import com.zds.zw.utils.Configuration;
import com.zds.zw.utils.ZdsHttp;
import com.zds.zw.view.MenuController;
import com.zds.zw.view.com.CustomAlert;
import com.zds.zw.view.task.UploadImageService;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImageInputDialog{
    private TextArea sourceText;
    private ZdsHttp zdsUtils;
    private MenuController menuManager;
    private Content content;
    private Stage stage;
    @FXML private Button selectButton;
    @FXML private TextField link;
    @FXML private TextField title;

    public void setSourceText(TextArea sourceText, ZdsHttp zdsUtils, MenuController menuManager, Content content){
        this.sourceText = sourceText;
        this.zdsUtils = zdsUtils;
        this.menuManager = menuManager;
        this.content = content;
        if(content == null ) {
            selectButton.setDisable(true);
        }
        title.setText(sourceText.getSelectedText());
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML private void handleInsertAction(){
        sourceText.replaceText(sourceText.getSelection(),"![" + title.getText() + "](" + link.getText() + ")");
        stage.close();
    }

    @FXML private void handleSelectFileAction(){
        if(! zdsUtils.isAuthenticated()){
            Service<Void> loginTask = menuManager.handleLoginButtonAction(null);
            loginTask.setOnSucceeded(t -> selectAndUploadImage());
            loginTask.setOnCancelled(t -> {
                menuManager.getHBottomBox().getChildren().clear();
                Alert alert = new CustomAlert(AlertType.ERROR);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.auth.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.auth.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.auth.failed.text"));

                alert.showAndWait();
            });
            loginTask.start();
        }else{
            selectAndUploadImage();
        }
    }

    private void selectAndUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(MainApp.getDefaultHome());
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            UploadImageService uploadImageTask = new UploadImageService(content, selectedFile.getAbsoluteFile());
            uploadImageTask.setOnFailed( t -> {
                Alert alert = new CustomAlert(AlertType.ERROR);
                alert.setTitle(Configuration.getBundle().getString("ui.dialog.upload.img.failed.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.upload.img.failed.header"));
                alert.setContentText(Configuration.getBundle().getString("ui.dialog.upload.img.failed.text"));
                alert.showAndWait();
            });
            uploadImageTask.setOnSucceeded(t -> link.setText(uploadImageTask.getValue()));
            uploadImageTask.start();
        }
    }
}
