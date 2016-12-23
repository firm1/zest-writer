package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MenuController;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.CustomStyledClassedTextArea;
import com.zestedesavoir.zestwriter.view.task.UploadImageService;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImageInputDialog{
    private CustomStyledClassedTextArea SourceText;
    private ZdsHttp zdsUtils;
    private MenuController menuManager;
    private Content content;
    private Stage stage;
    @FXML private Button selectButton;
    @FXML private TextField link;
    @FXML private TextField title;

    public void setSourceText(CustomStyledClassedTextArea SourceText, ZdsHttp zdsUtils, MenuController menuManager, Content content){
        this.SourceText = SourceText;
        this.zdsUtils = zdsUtils;
        this.menuManager = menuManager;
        this.content = content;
        if(content == null ) {
            selectButton.setDisable(true);
        }
        title.setText(SourceText.getSelectedText());
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML private void HandleInsertAction(){
        SourceText.replaceText(SourceText.getSelection(),"![" + title.getText() + "](" + link.getText() + ")");
        stage.close();
    }

    @FXML private void HandleSelectFileAction(){
        if(! zdsUtils.isAuthenticated()){
            Service<Void> loginTask = menuManager.HandleLoginButtonAction(null);

            loginTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                Alert alert;
                switch(newValue){
                    case FAILED:
                        break;
                    case CANCELLED:
                        menuManager.gethBottomBox().getChildren().clear();
                        alert = new CustomAlert(AlertType.ERROR);
                        alert.setTitle(Configuration.bundle.getString("ui.dialog.auth.failed.title"));
                        alert.setHeaderText(Configuration.bundle.getString("ui.dialog.auth.failed.header"));
                        alert.setContentText(Configuration.bundle.getString("ui.dialog.auth.failed.text"));

                        alert.showAndWait();
                        break;
                    case SUCCEEDED:
                        selectAndUploadImage();
                        break;
                }
            });
            loginTask.start();
        }else{
            selectAndUploadImage();
        }
    }

    private void selectAndUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(menuManager.getMainApp().getDefaultHome());
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            UploadImageService uploadImageTask = new UploadImageService(content, selectedFile.getAbsoluteFile());
            uploadImageTask.setOnFailed( t -> {
                Alert alert = new CustomAlert(AlertType.ERROR);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.upload.img.failed.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.upload.img.failed.header"));
                alert.setContentText(Configuration.bundle.getString("ui.dialog.upload.img.failed.text"));
                alert.showAndWait();
            });
            uploadImageTask.setOnSucceeded(t -> link.setText(uploadImageTask.getValue()));
            uploadImageTask.start();
        }
    }
}
