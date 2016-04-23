package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.view.MdConvertController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.Collection;

public class FindReplaceDialog{
    private MainApp mainApp;
    private MdConvertController mdConvertController;
    private StyleClassedTextArea sourceText;

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
        if(!replaceField.getText().isEmpty() && !searchField.getText().isEmpty()){
            replaceButton.setDisable(false);
            replaceAllButton.setDisable(false);
        }else{
            replaceButton.setDisable(true);
            replaceAllButton.setDisable(true);
        }
    }

    @FXML private void HandleSearchButtonAction(){
        findText(true);
    }

    @FXML private void HandleReplaceButtonAction(){
        replaceNextText();
    }

    @FXML private void HandleReplaceAllButtonAction(){
        replaceAllText();
    }

    private void findText(boolean findText){
        if(!searchField.getText().isEmpty()){
            String text = sourceText.getText();
            String searchText = searchField.getText();

            Collection<String> css = new ArrayList<>();
            css.add("-fx-background-color: red;");
            sourceText.setStyle(0, 20, css);

            if(!caseSensitive.isSelected()){
                text = text.toLowerCase();
                searchText = searchText.toLowerCase();
            }

            if(wholeWord.isSelected())
                searchText = " " + searchText + " ";

            int numberIteration = 0;
            for(int i = - 1; (i = text.indexOf(searchText, i + 1)) != - 1; ){
                if(findText){
                    //Action for find text
                }

                if(selectionOnly.isSelected() && !sourceText.getSelectedText().isEmpty()){
                    if(i > sourceText.getSelection().getStart() && i < sourceText.getSelection().getEnd()){
                        numberIteration++;
                    }
                }else{
                    numberIteration++;
                }
            }

            if(numberIteration > 0)
                iterations.setText(numberIteration + " itérations trouvés");
            else
                iterations.setText("0 itération trouvé");
        }
    }

    private void replaceNextText(){

    }

    private void replaceAllText(){
        if(!searchField.getText().isEmpty() && !replaceField.getText().isEmpty()){
            String text = sourceText.getText();
            String searchText = searchField.getText();

            if(!caseSensitive.isSelected()){
                text = text.toLowerCase();
                searchText = searchText.toLowerCase();
            }

            if(wholeWord.isSelected())
                searchText = " " + searchText + " ";

            int numberIteration = 0;
            for(int i = - 1; (i = text.indexOf(searchText, i + 1)) != - 1; ){
                if(selectionOnly.isSelected() && !sourceText.getSelectedText().isEmpty()){
                    if(i > sourceText.getSelection().getStart() && i < sourceText.getSelection().getEnd()){
                        sourceText.replaceText(i, i + searchField.getText().length(), replaceField.getText());
                        numberIteration++;
                    }
                }else{
                    sourceText.replaceText(i, i + searchField.getText().length(), replaceField.getText());
                    numberIteration++;
                }
            }

            if(numberIteration > 0)
                iterations.setText(numberIteration + " itérations modifiés");
            else
                iterations.setText("0 itération modifié");
        }
    }

    private void resetIterationNumber(){
        iterations.setText("0 itération trouvé");
    }
}
