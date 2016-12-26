package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindReplaceDialog{
    private StyleClassedTextArea sourceText;

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
        REPLACE
    }

    public void setSourceText(StyleClassedTextArea sourceText){
        this.sourceText = sourceText;
    }

    @FXML private void initialize(){
        searchButton.disableProperty().bind(searchField.textProperty().isEmpty());
        replaceButton.disableProperty().bind(searchField.textProperty().isEmpty());
        replaceAllButton.disableProperty().bind(searchField.textProperty().isEmpty());
        replaceButton.disableProperty().bind(replaceField.textProperty().isEmpty());
        replaceAllButton.disableProperty().bind(replaceField.textProperty().isEmpty());

        caseSensitive.selectedProperty().addListener((observable, oldValue, newValue) -> refreshSearch());
        wholeWord.selectedProperty().addListener((observable, oldValue, newValue) -> refreshSearch());
        markLines.selectedProperty().addListener((observable, oldValue, newValue) -> refreshSearch());
        selectionOnly.selectedProperty().addListener((observable, oldValue, newValue) -> refreshSearch());
    }

    @FXML private void HandleSearchFieldChange(){
        replaceIndex = 0;
        findIndex = 0;
        resetTextFill();
        if(!searchField.getText().isEmpty()){
            refreshSearch();
        } else {
            resetIterationNumber();
        }

    }

    @FXML private void HandleReplaceFieldChange(){
        replaceIndex = 0;
    }

    @FXML private void HandleSearchButtonAction(){
        findReplace(FindReplaceAction.FIND_ACTION);
    }

    @FXML private void HandleReplaceButtonAction(){
        findReplace(FindReplaceAction.REPLACE);
    }

    @FXML private void HandleReplaceAllButtonAction(){
        findReplaceAll();
    }

    private static List<Pair<Integer, Integer>> getRegionIndex(Pattern pattern, String s, int startFrom) {
        List<Pair<Integer, Integer>> matches = new ArrayList<>();
        Matcher m = pattern.matcher(s);
        while(m.find()) {
            int left=0, right=0;
            if(m.groupCount() != 0) {
                left=m.group(1).length();
                right = m.group(2).length();
            }
            matches.add(new Pair<>(m.start() + startFrom + left, m.end() + startFrom - right));
        }
        return matches;
    }

    private List<Pair<Integer, Integer>>  refreshSearch() {
        resetTextFill();

        if(!searchField.getText().isEmpty()){
            String text = sourceText.getText();
            String searchText = searchField.getText();
            String regxp = searchText;
            int deltaText = 0;
            int flags = Pattern.DOTALL;

            if(!caseSensitive.isSelected()){
                flags = flags | Pattern.CASE_INSENSITIVE;
            }

            if(wholeWord.isSelected())
                regxp = "(\\s|^)" + searchText + "(\\s|$)";

            Pattern pattern = Pattern.compile(regxp, flags);

            if(selectionOnly.isSelected()){
                if(!sourceText.getSelectedText().isEmpty()){
                    text = sourceText.getSelectedText();
                    deltaText = sourceText.getSelection().getStart();
                }
            }

            List<Pair<Integer, Integer>> matchers=getRegionIndex(pattern, text, deltaText);
            numberIterationTotal = matchers.size();

            for(Pair<Integer, Integer> m:matchers) {
                textFill(m.getKey(), m.getValue(), FindReplaceAction.FIND);
            }

            if(numberIterationTotal > 0){
                iterations.setText(numberIterationTotal +" "+Configuration.getBundle().getString("ui.dialog.find.plural"));
            }else{
                iterations.setText(Configuration.getBundle().getString("ui.dialog.find.empty"));
            }
            return matchers;
        }
        return null;
    }

    private void findReplaceAll(){
        resetTextFill();
        for(int k=0; k<numberIterationTotal; k++) {
            findReplace(FindReplaceAction.REPLACE);
        }
    }
    private void findReplace(FindReplaceAction action){
        List<Pair<Integer, Integer>> matchers = refreshSearch();
        if(matchers != null)
        for(int i = 0; i< matchers.size(); i++) {
            Pair<Integer, Integer> matchCurrent = matchers.get(i);
            if(action == FindReplaceAction.FIND_ACTION){
                if(i == findIndex){
                    textFill(matchCurrent.getKey(), matchCurrent.getValue(), FindReplaceAction.FIND_ACTION);
                    sourceText.moveTo(matchCurrent.getKey());
                    refreshSearch();
                    findIndex = numberIterationTotal>0 ? (findIndex + 1) % numberIterationTotal : 0;
                    break;
                }
            } else if(action == FindReplaceAction.REPLACE){
                if(i == replaceIndex){
                    sourceText.replaceText(matchCurrent.getKey(), matchCurrent.getValue(), replaceField.getText());
                    textFill(matchCurrent.getKey(), matchCurrent.getKey() + replaceField.getText().length(), FindReplaceAction.REPLACE);
                    sourceText.moveTo(matchCurrent.getKey());
                    refreshSearch();
                    replaceIndex = numberIterationTotal>0 ? (replaceIndex + 1) % numberIterationTotal : 0;
                    break;
                }
            }
        }
    }

    private void resetIterationNumber(){
        iterations.setText(Configuration.getBundle().getString("ui.dialog.find.empty"));
    }

    private void textFill(int start, int end, FindReplaceAction action){
        if(action == FindReplaceAction.FIND){
            sourceText.setStyleClass(start, end, "findReplace-highlightsFind");
        }else if(action == FindReplaceAction.FIND_ACTION){
            sourceText.setStyleClass(start, end, "findReplace-highlightsFindAction");
        }else if(action == FindReplaceAction.REPLACE){
            sourceText.setStyleClass(start, end, "findReplace-highlightsReplace");
        }
    }

    private void resetTextFill(){
        sourceText.setStyleClass(0, sourceText.getText().length(), "");
    }
}
