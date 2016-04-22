package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FindReplaceDialog{
    private MainApp mainApp;

    @FXML private TextField searchField;
    @FXML private TextField replaceField;
    @FXML private CheckBox caseSensitive;
    @FXML private CheckBox wholeWord;
    @FXML private CheckBox markLines;
    @FXML private CheckBox selectionOnly;
    @FXML private Button searchButton;
    @FXML private Button replaceButton;
    @FXML private Button replaceAllButton;
    @FXML private Label iterations;


    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML private void initialize(){
        searchButton.setDisable(true);
        replaceButton.setDisable(true);
        replaceAllButton.setDisable(true);
    }

    @FXML private void HandleSearchFieldChange(){
        if(!searchField.getText().isEmpty()){
            searchButton.setDisable(false);

            if(!replaceField.getText().isEmpty()){
                replaceButton.setDisable(false);
                replaceAllButton.setDisable(false);
            }else{
                replaceButton.setDisable(true);
                replaceAllButton.setDisable(true);
            }
        }else{
            searchButton.setDisable(true);
            replaceButton.setDisable(true);
            replaceAllButton.setDisable(true);
        }
    }

    @FXML private void HandleReplaceFieldChange(){
        if(!replaceField.getText().isEmpty() && !searchField.getText().isEmpty()){
            replaceButton.setDisable(false);
            replaceAllButton.setDisable(false);
        }else{
            replaceButton.setDisable(true);
            replaceAllButton.setDisable(true);
        }
    }

    @FXML private void HandleSearchButtonAction(){

    }

    @FXML private void HandleReplaceButtonAction(){

    }

    @FXML private void HandleReplaceAllButtonAction(){

    }
}
