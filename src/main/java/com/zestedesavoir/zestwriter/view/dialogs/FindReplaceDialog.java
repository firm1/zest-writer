package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.view.MdConvertController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.Collection;

public class FindReplaceDialog{
    private MainApp mainApp;
    private MdConvertController mdConvertController;
    private StyleClassedTextArea sourceText;
    private Stage window;

    private int replaceIndex;
    private int findIndex;
    private int numberIterationTotal;

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


    private enum FindReplaceAction{
        FIND,
        FIND_ACTION,
        REPLACE,
        REPLACE_ALL
    }

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    public void setWindow(Stage window){
        this.window = window;
        window.setOnCloseRequest(event -> resetTextFill());
    }

    public void setMdConvertController(MdConvertController mdConvertController){
        this.mdConvertController = mdConvertController;
        this.sourceText = this.mdConvertController.getSourceText();
    }

    @FXML private void initialize(){
        searchButton.setDisable(true);
        replaceButton.setDisable(true);
        replaceAllButton.setDisable(true);

        caseSensitive.selectedProperty().addListener((observable, oldValue, newValue) -> {
            findText(false);
        });
        wholeWord.selectedProperty().addListener((observable, oldValue, newValue) -> {
            findText(false);
        });
        markLines.selectedProperty().addListener((observable, oldValue, newValue) -> {
            findText(false);
        });
        selectionOnly.selectedProperty().addListener((observable, oldValue, newValue) -> {
            findText(false);
        });
    }

    @FXML private void HandleSearchFieldChange(){
        replaceIndex = 0;
        findIndex = 0;
        resetTextFill();

        if(!searchField.getText().isEmpty()){
            searchButton.setDisable(false);

            if(!replaceField.getText().isEmpty()){
                replaceButton.setDisable(false);
                replaceAllButton.setDisable(false);
            }else{
                replaceButton.setDisable(true);
                replaceAllButton.setDisable(true);
            }

            findText(false);
        }else{
            searchButton.setDisable(true);
            replaceButton.setDisable(true);
            replaceAllButton.setDisable(true);

            resetIterationNumber();
        }
    }

    @FXML private void HandleReplaceFieldChange(){
        replaceIndex = 0;

        if(!replaceField.getText().isEmpty() && !searchField.getText().isEmpty()){
            replaceButton.setDisable(false);
            replaceAllButton.setDisable(false);
        }else{
            replaceButton.setDisable(true);
            replaceAllButton.setDisable(true);
        }
    }

    @FXML private void HandleSearchButtonAction(){
        findReplace(FindReplaceAction.FIND_ACTION);
    }

    @FXML private void HandleReplaceButtonAction(){
        replaceNextText();
    }

    @FXML private void HandleReplaceAllButtonAction(){
        replaceAllText();
    }

    private void findText(boolean findText){
        findReplace(FindReplaceAction.FIND);
    }

    private void replaceNextText(){
        findReplace(FindReplaceAction.REPLACE);
    }

    private void replaceAllText(){
        findReplace(FindReplaceAction.REPLACE_ALL);
    }

    private void findReplace(FindReplaceAction action){
        resetTextFill();

        if(!searchField.getText().isEmpty()){
            String text = sourceText.getText();
            String searchText = searchField.getText();

            if(!caseSensitive.isSelected()){
                text = text.toLowerCase();
                searchText = searchText.toLowerCase();
            }

            if(wholeWord.isSelected())
                searchText = " " + searchText + " ";

            int numberIteration = 0;
            numberIterationTotal = 0;
            for(int i = - 1; (i = text.indexOf(searchText, i + 1)) != - 1; ){
                if(action == FindReplaceAction.FIND){
                    if(selectionOnly.isSelected()){
                        if(!sourceText.getSelectedText().isEmpty()){
                            textFill(i, i + searchText.length(), FindReplaceAction.FIND);
                        }
                    }else{
                        textFill(i, i + searchText.length(), FindReplaceAction.FIND);
                    }
                }else if(action == FindReplaceAction.FIND_ACTION){
                    if(selectionOnly.isSelected()){
                        if(!sourceText.getSelectedText().isEmpty()){
                            textFill(i, i + searchText.length(), FindReplaceAction.FIND);
                        }
                    }else{
                        textFill(i, i + searchText.length(), FindReplaceAction.FIND);
                    }

                    if(numberIterationTotal == findIndex){
                        findReplace(FindReplaceAction.FIND);
                        textFill(i, i + searchText.length(), FindReplaceAction.FIND_ACTION);
                        sourceText.moveTo(i);
                        findIndex++;
                        break;
                    }
                }else if(action == FindReplaceAction.REPLACE){
                    if(!replaceField.getText().isEmpty()){
                        if(selectionOnly.isSelected()){
                            if(!sourceText.getSelectedText().isEmpty()){
                                textFill(i, i + searchText.length(), FindReplaceAction.FIND);
                            }
                        }else{
                            textFill(i, i + searchText.length(), FindReplaceAction.FIND);
                        }

                        if(replaceIndex == numberIterationTotal){
                            sourceText.replaceText(i, i + searchField.getText().length(), replaceField.getText());
                            findReplace(FindReplaceAction.FIND);
                            textFill(i, i + replaceField.getText().length(), FindReplaceAction.REPLACE);

                            sourceText.moveTo(i);
                            break;
                        }
                    }
                }else if(action == FindReplaceAction.REPLACE_ALL){
                    for(int j = - 1; (j = text.indexOf(searchText, j + 1)) != - 1; ){
                        findReplace(FindReplaceAction.REPLACE);
                    }
                }

                if(selectionOnly.isSelected()){
                    if(!sourceText.getSelectedText().isEmpty()){
                        if(i > sourceText.getSelection().getStart() && i < sourceText.getSelection().getEnd()){
                            if(numberIteration == 0)
                                sourceText.positionCaret(i);

                            numberIteration++;
                        }
                    }
                }else{
                    if(numberIteration == 0)
                        sourceText.positionCaret(i);

                    numberIteration++;
                }

                numberIterationTotal++;
            }

            if(action == FindReplaceAction.FIND_ACTION && findIndex == numberIteration)
                findIndex = 0;

            if(action == FindReplaceAction.REPLACE && replaceIndex == numberIterationTotal)
                HandleSearchFieldChange();

            if(action == FindReplaceAction.FIND){
                if(numberIteration > 0){
                    iterations.setText(numberIteration + " itérations trouvés");

                    searchButton.setDisable(false);
                    replaceButton.setDisable(false);
                    replaceAllButton.setDisable(false);
                }else{
                    iterations.setText("0 itération trouvé");

                    searchButton.setDisable(true);
                    replaceButton.setDisable(true);
                    replaceAllButton.setDisable(true);
                }
            }
        }
    }

    private void resetIterationNumber(){
        iterations.setText("0 itération trouvé");
    }

    private void textFill(int start, int end, FindReplaceAction action){
        if(action == FindReplaceAction.FIND){
            sourceText.setStyleClass(start, end, "findReplace-highlightsFind");
        }else if(action == FindReplaceAction.FIND_ACTION){
            sourceText.setStyleClass(start, end, "findReplace-highlightsFindAction");
        }else if(action == FindReplaceAction.REPLACE || action == FindReplaceAction.REPLACE_ALL){
            sourceText.setStyleClass(start, end, "findReplace-highlightsReplace");
        }
    }

    private void resetTextFill(){
        sourceText.setStyleClass(0, sourceText.getText().length(), "");
    }
}
