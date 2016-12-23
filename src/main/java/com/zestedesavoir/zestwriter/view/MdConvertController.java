package com.zestedesavoir.zestwriter.view;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.FlipTable;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.com.*;
import com.zestedesavoir.zestwriter.view.dialogs.ImageInputDialog;
import javafx.application.Platform;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import netscape.javascript.JSException;
import org.apache.commons.lang.StringEscapeUtils;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.EventPattern.keyReleased;

public class MdConvertController {
    private MainApp mainApp;
    private MdTextController mdBox;
    private Tab tab;
    private Textual extract;
    private Corrector corrector;
    private Service<String> renderTask;
    private final Logger logger;
    private int xRenderPosition = 0;
    private int yRenderPosition = 0;
    private StringProperty countChars = new SimpleStringProperty();
    private StringProperty countWords = new SimpleStringProperty();
    private StringProperty countTimes = new SimpleStringProperty();
    private BooleanPropertyBase isSaved = new SimpleBooleanProperty(true);

    @FXML private WebView renderView;
    @FXML private Button SaveButton;
    @FXML private SplitPane splitPane;
    @FXML private BorderPane BoxEditor;
    @FXML private BorderPane BoxRender;
    private CustomStyledClassedTextArea SourceText;
    public static final Pattern recognizeNumber = Pattern.compile("^(\\s*)([\\d][\\.]) (\\s*)(.*)");
    public static final Pattern recognizeBullet = Pattern.compile("^(\\s*)([*|-]) (\\s*)(.*)");

    public MdConvertController() {
        super();
        logger = LoggerFactory.getLogger(MdConvertController.class);
        SourceText = new CustomStyledClassedTextArea();
    }

    public MdTextController getMdBox() {
        return mdBox;
    }

    public StyleClassedTextArea getSourceText(){
        return SourceText;
    }

    public void setMdBox(MdTextController mdBox, Textual extract, Tab tab) throws IOException {
        this.mainApp = mdBox.getMainApp();
        this.mdBox = mdBox;
        this.tab = tab;
        this.extract = extract;

        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/Editor.fxml"));
        loader.load();

        if(!MainApp.getConfig().isEditorToolbarView()){
            BoxEditor.setTop(null);
            BoxRender.setTop(null);
        }

        BoxEditor.setCenter(SourceText);
        SourceText.setStyle("-fx-font-family: \"" + MainApp.getConfig().getEditorFont() + "\";-fx-font-size: " + MainApp.getConfig().getEditorFontsize() + ";");
        if(MainApp.config.isEditorRenderView()) {
            initRenderTask();
        } else {
            splitPane.getItems().remove(1);
        }
        Platform.runLater(() -> {
            SourceText.replaceText(extract.getMarkdown());
            initStats();
            SourceText.getUndoManager().forgetHistory();
            SourceText.textProperty().addListener((observableValue, s, s2) -> {
                tab.setText("! " + extract.getTitle());
                this.isSaved.setValue(false);
                SourceText.getUndoManager().mark();
                updateRender();
            });
            updateRender();
        });

        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.S, SHORTCUT_DOWN)).act( ev -> handleSaveButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.G, SHORTCUT_DOWN)).act( ev -> handleBoldButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.I, SHORTCUT_DOWN)).act( ev -> handleItalicButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.B, SHORTCUT_DOWN)).act( ev -> handleBarredButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.K, SHORTCUT_DOWN)).act( ev -> handleTouchButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.PLUS, SHORTCUT_DOWN)).act( ev -> handleExpButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.EQUALS, SHORTCUT_DOWN)).act( ev -> handleIndButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.E, SHORTCUT_DOWN)).act( ev -> handleCenterButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.D, SHIFT_DOWN, SHORTCUT_DOWN)).act( ev -> handleRightButtonAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.SPACE, SHORTCUT_DOWN)).act( ev -> handleUnbreakableAction(null)).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.L, SHORTCUT_DOWN)).act( ev -> handleGoToLineAction()).create());
        EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.F, SHORTCUT_DOWN)).act( ev -> handleFindReplaceDialog()).create());
        if(FunctionTreeFactory.isMacOs()) {
            EventHandlerHelper.install(SourceText.onKeyPressedProperty(),
                    EventHandlerHelper.on(keyPressed(KeyCode.Q, SHORTCUT_DOWN)).act( ev -> SourceText.selectAll()).create());
        }
        if(MainApp.getConfig().isEditorSmart()) {
            EventHandlerHelper.install(SourceText.onKeyReleasedProperty(),
                    EventHandlerHelper.on(keyReleased(KeyCode.TAB)).act(ev -> handleSmartTab()).create());
            EventHandlerHelper.install(SourceText.onKeyReleasedProperty(),
                    EventHandlerHelper.on(keyReleased(KeyCode.ENTER)).act(ev -> handleSmartEnter()).create());
        }

        tab.setOnSelectionChanged(t -> {
            if(tab.isSelected()) {
                Platform.runLater(() -> {
                    SourceText.requestFocus();
                    initStats();
                });
            }
        });

        Platform.runLater(SourceText::requestFocus);
    }


    private void handleSmartEnter() {
        int precLine = SourceText.getCurrentParagraph() - 1;
        if(precLine >= 0) {
            String line = SourceText.getParagraph(precLine).toString();
            Matcher matcher = recognizeBullet.matcher(line);
            //TODO : find how combine recognize bullet and number together for breaking following if
            if(!matcher.matches()) {
                matcher = recognizeNumber.matcher(line);
            }
            if(matcher.matches()) {
                if("".equals(matcher.group(4).trim())) {
                    int positionCaret = SourceText.getCaretPosition();
                    SourceText.deleteText(positionCaret-line.length() - 1, positionCaret);
                } else {
                    SourceText.replaceSelection(matcher.group(1)+matcher.group(2)+" ");
                }
            }
        }
    }

    private void handleSmartTab() {
        int caseLine = SourceText.getCurrentParagraph();
        if(caseLine >= 0) {
            String line = SourceText.getParagraph(caseLine).toString();
            Matcher matcher = recognizeBullet.matcher(line);
            //TODO : find how combine recognize bullet and number together for breaking following if
            if(!matcher.matches()) {
                matcher = recognizeNumber.matcher(line);
            }
            if(matcher.matches()) {
                int positionCaret = SourceText.getCaretPosition();
                int delta = matcher.group(1).length() + matcher.group(2).length() + matcher.group(3).length() + 1;
                SourceText.replaceText(positionCaret-delta, positionCaret, "    "+matcher.group(2)+" "+matcher.group(4));
            }
        }
    }


    @FXML private void initialize() {
        SourceText.getStyleClass().add("markdown-editor");
        SourceText.getStylesheets().add(MainApp.class.getResource("css/editor.css").toExternalForm());

        if(MainApp.config.isEditorLinenoView())
            SourceText.setParagraphGraphicFactory(LineNumberFactory.get(SourceText));

        SaveButton.disableProperty().bind(isSaved);
    }

    /*
     * Editor Toolbar Action
     */

    @FXML public void handleSaveButtonAction(ActionEvent event) {
        extract.setMarkdown(SourceText.getText());
        extract.save();
        tab.setText(extract.getTitle());
        this.isSaved.setValue(true);

        SourceText.requestFocus();
    }

    @FXML private void handleBoldButtonAction(ActionEvent event) {
        replaceAction("****", 2, "**", "**");
    }

    @FXML private void handleItalicButtonAction(ActionEvent event) {
        replaceAction("**", 1, "*", "*");
    }

    @FXML private void handleBarredButtonAction(ActionEvent event) {
        replaceAction("~~~~", 2, "~~", "~~");
    }

    @FXML private void handleTouchButtonAction(ActionEvent event) {
        replaceAction("||||", 2, "||", "||");
    }

    @FXML private void handleExpButtonAction(ActionEvent event) {
        replaceAction("^^", 1, "^", "^");
    }

    @FXML private void handleIndButtonAction(ActionEvent event) {
        replaceAction("~~", 1, "~", "~");
    }

    @FXML private void handleCenterButtonAction(ActionEvent event) {
        replaceAction("\n->  <-", 3, "\n-> ", " <-");
    }

    @FXML private void handleRightButtonAction(ActionEvent event) {
        replaceAction("\n->  ->", 3, "\n-> ", " ->\n");
    }

    @FXML private void handleImgButtonAction(ActionEvent event) {
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/ImageInput.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.bundle.getString("ui.dialog.upload.img.title"));

        ImageInputDialog imageController = loader.getController();
        if(mainApp.getContents().size() > 0) {
            imageController.setSourceText(SourceText, MainApp.getZdsutils(), mainApp.getMenuController(), mainApp.getContents().get(0));
        } else {
            imageController.setSourceText(SourceText, MainApp.getZdsutils(), mainApp.getMenuController(), null);
        }
        imageController.setStage(dialogStage);

        dialogStage.show();
    }
    @FXML private void handleBulletButtonAction(ActionEvent event) {
        if(SourceText.getSelectedText().isEmpty()){
            SourceText.replaceText(SourceText.getSelection(), "- ");
        }else{
            StringBuilder sb = new StringBuilder();
            String[] lines = SourceText.getSelectedText().split("\n");
            for(String line : lines){
                sb.append("- ").append(line).append("\n");
            }

            SourceText.replaceText(SourceText.getSelection(), sb.toString());
        }

        SourceText.requestFocus();
    }

    @FXML private void handleNumberedButtonAction(ActionEvent event) {
        if(SourceText.getSelectedText().isEmpty()){
            SourceText.replaceText(SourceText.getSelection(), "1. ");
        }else{
            StringBuilder sb = new StringBuilder();
            String[] lines = SourceText.getSelectedText().split("\n");
            int i = 1;
            for(String line : lines){
                sb.append(i).append(". ").append(line).append("\n");
                i++;
            }

            SourceText.replaceText(SourceText.getSelection(), sb.toString());
        }

        SourceText.requestFocus();
    }

    @FXML private void handleHeaderButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "# " + SourceText.getSelectedText());
        SourceText.requestFocus();
    }

    @FXML private void handleQuoteButtonAction(ActionEvent event) {
        replaceAction("> ", 0, "> ", "\n\n");
    }

    @FXML private void handleBlocButtonAction(ActionEvent event) {
        String text = "";
        String[] lines = SourceText.getSelectedText().split("\n");
        for (String line : lines) {
            text += "| " + line + "\n";
        }

        List<String> choices = new ArrayList<>();
        choices.add("information");
        choices.add("question");
        choices.add("attention");
        choices.add("erreur");
        choices.add("secret");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("information", choices);
        FunctionTreeFactory.addTheming(dialog.getDialogPane());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(mainApp.getPrimaryStage());
        dialog.setTitle(Configuration.bundle.getString("ui.editor.dialog.bloc.title"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.editor.dialog.bloc.header"));
        dialog.setContentText(Configuration.bundle.getString("ui.editor.dialog.bloc.text"));

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            SourceText.replaceText(SourceText.getSelection(), "\n[[" + result.get() + "]]\n" + text);
        }

        SourceText.requestFocus();
    }

    @FXML private void handleTableButtonAction(ActionEvent event) throws IOException {
        // Create the custom dialog.
        Dialog<Pair<ObservableList, ObservableList<ZRow>>> dialog = new CustomDialog<>();
        dialog.setTitle(Configuration.bundle.getString("ui.editor.button.table"));
        dialog.setHeaderText("");

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/TableEditor.fxml"));
        BorderPane tableEditor = loader.load();
        TableView<ZRow> tbView = (TableView) tableEditor.getCenter();

        TableController controller = loader.getController();
        controller.setEditor(this);

        dialog.getDialogPane().setContent(tableEditor);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(tbView.getColumns(), tbView.getItems());
            }
            return null;
        });

        Optional<Pair<ObservableList, ObservableList<ZRow>>> result = dialog.showAndWait();

        result.ifPresent(datas -> {
            String[][] data = new String[datas.getValue().size()][datas.getValue().get(0).getRow().size()];
            String[] headers = new String[datas.getKey().size()];
            int cpt = 0;
            for (Object key : datas.getKey()) {
                headers[cpt] = ((TextField) ((TableColumn) key).getGraphic()).getText();
                cpt++;
            }

            for (int i = 0; i < datas.getValue().size(); i++) {
                for (int j = 0; j < datas.getValue().get(i).getRow().size(); j++) {
                    data[i][j] = datas.getValue().get(i).getRow().get(j);
                }
            }
            String tablestring = FlipTable.of(headers, data);
            SourceText.replaceText(SourceText.getSelection(), "\n\n" + tablestring + "\n\n");
            SourceText.requestFocus();
        });
    }

    @FXML private void handleLinkButtonAction(ActionEvent event) {
        String link = SourceText.getSelectedText();

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new CustomDialog<>();
        dialog.setTitle(Configuration.bundle.getString("ui.editor.dialog.link.title"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.editor.dialog.link.header"));

        // Set the icon (must be included in the project).
        dialog.setGraphic(IconFactory.createLinkIcon());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 15, 10, 10));

        TextField tLink = new TextField();
        tLink.setText(link);
        TextField tLabel = new TextField();
        tLabel.setText(link);

        grid.add(new Label(Configuration.bundle.getString("ui.editor.dialog.link.field.url")), 0, 0);
        grid.add(tLink, 1, 0);
        grid.add(new Label(Configuration.bundle.getString("ui.editor.dialog.link.field.label")), 0, 1);
        grid.add(tLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(tLink::requestFocus);

        // Convert the result to a username-password-pair when the login button
        // is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(tLink.getText(), tLabel.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(tLinkTLabel -> SourceText.replaceText(SourceText.getSelection(),
                "[" + tLinkTLabel.getValue() + "](" + tLinkTLabel.getKey() + ")"));

        SourceText.requestFocus();
    }

    @FXML private void handleCodeButtonAction(ActionEvent event) {
        String code = SourceText.getSelectedText();
        if (code.trim().startsWith("```") && code.trim().endsWith("```")) {
            int start = code.trim().indexOf('\n') + 1;
            int end = code.trim().lastIndexOf('\n');
            code = code.substring(start, end);
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new CustomDialog<>();
        dialog.setTitle(Configuration.bundle.getString("ui.editor.dialog.code.title"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.editor.dialog.code.header"));

        // Set the icon (must be included in the project).
        dialog.setGraphic(IconFactory.createCodeIcon());

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 15, 10, 10));

        TextField tLangage = new TextField();
        TextArea tCode = new TextArea();
        tCode.setText(code);

        grid.add(new Label(Configuration.bundle.getString("ui.editor.dialog.code.field.lang")), 0, 0);
        grid.add(tLangage, 1, 0);
        grid.add(new Label(Configuration.bundle.getString("ui.editor.dialog.code.field.code")), 0, 1);
        grid.add(tCode, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(tLangage::requestFocus);

        // Convert the result to a username-password-pair when the login button
        // is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(tLangage.getText(), tCode.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(tLangageTCode -> SourceText.replaceText(SourceText.getSelection(),
                "\n```" + tLangageTCode.getKey() + "\n" + tLangageTCode.getValue() + "\n```\n"));

        SourceText.requestFocus();
    }

    /*
     * Render Toolbar Action
     */

    public void addTreeSummary() {
        mdBox.getSplitPane().getItems().add(0, mdBox.treePane);
        mdBox.getSplitPane().setDividerPositions(0.2);
        SplitPane.setResizableWithParent(mdBox.treePane,Boolean.FALSE);
    }

    @FXML private void handleFullScreeenButtonAction(ActionEvent event) {
        if (mdBox.getSplitPane().getItems().size() > 1) {
            mdBox.getSplitPane().getItems().remove(0);
        } else {
            addTreeSummary();
        }
    }

    private void initRenderTask() {
        renderTask = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        String html = markdownToHtml(SourceText.getText());
                        if (html != null) {
                            return MainApp.getMdUtils().addHeaderAndFooter(html);
                        } else {
                            Thread.sleep(5000);
                            throw new IOException();

                        }
                    }

                };
            }
        };

        renderTask.setOnFailed(t -> renderTask.restart());
        renderTask.setOnSucceeded(t -> {
            yRenderPosition = getVScrollValue(renderView);
            xRenderPosition = getHScrollValue(renderView);
            renderView.getEngine().loadContent(renderTask.valueProperty().getValue());
            renderTask.reset();

        });
    }

    @FXML public void updateRender() {
        if(MainApp.config.isEditorRenderView()) {
            if (renderTask != null) {
                if (renderTask.getState().equals(State.READY)) {
                    renderTask.start();
                }
            }
            renderView.getEngine().getLoadWorker().stateProperty()
                    .addListener((ObservableValue<? extends State> ov, State oldState, State newState) -> {
                        if (newState == State.SUCCEEDED) {
                            scrollTo(renderView, xRenderPosition, yRenderPosition);
                        }
                    });
        }
        performStats();
    }

    @FXML private void handleValidateButtonAction(ActionEvent event) {
        String s = StringEscapeUtils.unescapeHtml(markdownToHtml(SourceText.getText()));
        if(corrector == null) {
        	corrector = new Corrector();
        }
        try {
            String result = corrector.checkHtmlContent(s);
            WebEngine webEngine = renderView.getEngine();
            webEngine.loadContent("<!doctype html><html lang='fr'><head><meta charset='utf-8'><base href='"
                    + MainApp.class.getResource("assets").toExternalForm() + "' /></head><body>" + result + "</body></html>");
            webEngine.setUserStyleSheetLocation(MainApp.class.getResource("assets/static/css/content.css").toExternalForm());
        } catch (DOMException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @FXML private void handleExternalButtonAction(ActionEvent event){
        splitPane.getItems().remove(1);

        Stage stage = new CustomStage(Configuration.bundle.getString("ui.window.externalrender.title"));
        AnchorPane pane = new AnchorPane(renderView);
        AnchorPane.setTopAnchor(renderView, 0.0);
        AnchorPane.setLeftAnchor(renderView, 0.0);
        AnchorPane.setBottomAnchor(renderView, 0.0);
        AnchorPane.setRightAnchor(renderView, 0.0);
        pane.setPrefWidth(600);
        pane.setPrefHeight(500);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

        stage.setOnCloseRequest(e -> {
            BoxRender.setCenter(renderView);
            splitPane.getItems().add(1, BoxRender);
            splitPane.setDividerPositions(0.5);
        });
    }

    @FXML private void handleUnbreakableAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), SourceText.getSelectedText() + "\u00a0");
        SourceText.requestFocus();
    }

    public void performStats() {
        Readability readText = new Readability(SourceText.getText());
        countChars.setValue(Configuration.bundle.getString("ui.statusbar.stats.chars") + readText.getCharacters());
        countWords.setValue(Configuration.bundle.getString("ui.statusbar.stats.words") + readText.getWords());
        countTimes.setValue(FunctionTreeFactory.getNumberOfTextualReadMinutes(SourceText.getText()));
    }

    public void initStats() {
        String fontSize="-fx-font-size: 0.9em;";
        mainApp.getMenuController().gethBottomBox().getChildren().clear();
        mainApp.getMenuController().gethBottomBox().getColumnConstraints().clear();
        mainApp.getMenuController().gethBottomBox().setPadding(new Insets(5, 5, 5, 5));
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        ColumnConstraints c3 = new ColumnConstraints();
        ColumnConstraints c4 = new ColumnConstraints();
        c1.setPercentWidth(50);
        c2.setPercentWidth(20);
        c3.setPercentWidth(15);
        c4.setPercentWidth(15);
        Label chars = new Label();
        Label words = new Label();
        Label times = new Label();
        chars.setStyle(fontSize);
        words.setStyle(fontSize);
        times.setStyle(fontSize);
        mainApp.getMenuController().gethBottomBox().getColumnConstraints().addAll(c1, c2, c3, c4);
        mainApp.getMenuController().gethBottomBox().add(times, 1, 0);
        mainApp.getMenuController().gethBottomBox().add(chars, 2, 0);
        mainApp.getMenuController().gethBottomBox().add(words, 3, 0);

        chars.textProperty().bind(countChars);
        words.textProperty().bind(countWords);
        times.textProperty().bind(countTimes);
        performStats();
    }


    public void handleGoToLineAction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(Configuration.bundle.getString("ui.editor.dialog.goto.title"));
        dialog.setHeaderText(Configuration.bundle.getString("ui.editor.dialog.goto.header"));
        dialog.setContentText(Configuration.bundle.getString("ui.editor.dialog.goto.text"));
        dialog.initOwner(MainApp.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(line -> SourceText.positionCaret(SourceText.position(Integer.parseInt(line)-1, 0).toOffset()));
    }


    public String markdownToHtml(String chaine) {
        PythonInterpreter console = getMdBox().getPyconsole();
        if (console != null) {
            console.set("text", chaine);
            console.exec(
                    "render = mk_instance.convert(text)");
            PyString render = console.get("render", PyString.class);
            return render.toString();
        } else {
            return null;
        }
    }

    private void replaceAction(String defaultString, int defaultOffsetCaret, String beforeString, String afterString) {
        if(SourceText.getSelectedText().isEmpty()){
            SourceText.replaceText(SourceText.getSelection(), defaultString);
            SourceText.moveTo(SourceText.getCaretPosition() - defaultOffsetCaret);
        }else{
            SourceText.replaceText(SourceText.getSelection(), beforeString + SourceText.getSelectedText() + afterString);
        }

        Platform.runLater(SourceText::requestFocus);
    }

    public boolean isSaved() {
        return isSaved.getValue();
    }

    @FXML private void handleFindReplaceDialog(){
        FunctionTreeFactory.OpenFindReplaceDialog(SourceText);
    }

    /**
     * Scrolls to the specified position.
     *
     * @param view web view that shall be scrolled
     * @param x horizontal scroll value
     * @param y vertical scroll value
     */
    public void scrollTo(WebView view, int x, int y) {
        view.getEngine().executeScript("window.scrollTo(" + x + ", " + y + ")");
    }

    /**
     * Returns the vertical scroll value, i.e. thumb position. This is
     * equivalent to {@link javafx.scene.control.ScrollBar#getValue()}.
     *
     * @param view web view that shall be scrolled
     * @return vertical scroll value
     */
    public int getVScrollValue(WebView view) {
        try {
            return (Integer) view.getEngine().executeScript("document.body.scrollTop");
        }
        catch(JSException e) {
            logger.trace(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Returns the horizontal scroll value, i.e. thumb position. This is
     * equivalent to {@link javafx.scene.control.ScrollBar#getValue()}.
     *
     * @param view
     * @return horizontal scroll value
     */
    public int getHScrollValue(WebView view) {
        try {
            return (Integer) view.getEngine().executeScript("document.body.scrollLeft");
        }
        catch(JSException e) {
            logger.trace(e.getMessage(), e);
            return 0;
        }
    }
}
