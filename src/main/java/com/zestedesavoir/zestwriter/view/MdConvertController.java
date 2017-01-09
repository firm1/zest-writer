package com.zestedesavoir.zestwriter.view;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.ContentNode;
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
    private BooleanPropertyBase saved = new SimpleBooleanProperty(true);

    @FXML private WebView renderView;
    @FXML private Button saveButton;
    @FXML private SplitPane splitPane;
    @FXML private BorderPane boxEditor;
    @FXML private BorderPane boxRender;
    private CustomStyledClassedTextArea sourceText;
    public static final Pattern recognizeNumber = Pattern.compile("^(\\s*)([\\d][\\.]) (\\s*)(.*)");
    public static final Pattern recognizeBullet = Pattern.compile("^(\\s*)([*|-]) (\\s*)(.*)");

    public MdConvertController() {
        super();
        logger = LoggerFactory.getLogger(MdConvertController.class);
        sourceText = new CustomStyledClassedTextArea();
    }

    public MdTextController getMdBox() {
        return mdBox;
    }

    public StyleClassedTextArea getSourceText(){
        return sourceText;
    }

    public void setMdBox(MdTextController mdBox, Textual extract, Tab tab) throws IOException {
        this.mainApp = mdBox.getMainApp();
        this.mdBox = mdBox;
        this.tab = tab;
        this.extract = extract;

        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/Editor.fxml"));
        loader.load();

        if(!MainApp.getConfig().isEditorToolbarView()){
            boxEditor.setTop(null);
            boxRender.setTop(null);
        }

        boxEditor.setCenter(sourceText);
        sourceText.setStyle("-fx-font-family: \"" + MainApp.getConfig().getEditorFont() + "\";-fx-font-size: " + MainApp.getConfig().getEditorFontsize() + ";");
        if(MainApp.getConfig().isEditorRenderView()) {
            initRenderTask();
        } else {
            splitPane.getItems().remove(1);
        }
        Platform.runLater(() -> {
            sourceText.replaceText(extract.getMarkdown());
            initStats();
            sourceText.getUndoManager().forgetHistory();
            sourceText.textProperty().addListener((observableValue, s, s2) -> {
                tab.setText("! " + extract.getTitle());
                setSaved(false);
                sourceText.getUndoManager().mark();
                updateRender();
            });
            updateRender();
        });

        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.S, SHORTCUT_DOWN)).act( ev -> handleSaveButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.G, SHORTCUT_DOWN)).act( ev -> handleBoldButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.I, SHORTCUT_DOWN)).act( ev -> handleItalicButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.B, SHORTCUT_DOWN)).act( ev -> handleBarredButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.K, SHORTCUT_DOWN)).act( ev -> handleTouchButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.PLUS, SHORTCUT_DOWN)).act( ev -> handleExpButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.EQUALS, SHORTCUT_DOWN)).act( ev -> handleIndButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.E, SHORTCUT_DOWN)).act( ev -> handleCenterButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.D, SHIFT_DOWN, SHORTCUT_DOWN)).act( ev -> handleRightButtonAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.SPACE, SHORTCUT_DOWN)).act( ev -> handleUnbreakableAction(null)).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.L, SHORTCUT_DOWN)).act( ev -> handleGoToLineAction()).create());
        EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                EventHandlerHelper.on(keyPressed(KeyCode.F, SHORTCUT_DOWN)).act( ev -> handleFindReplaceDialog()).create());
        if(FunctionTreeFactory.isMacOs()) {
            EventHandlerHelper.install(sourceText.onKeyPressedProperty(),
                    EventHandlerHelper.on(keyPressed(KeyCode.Q, SHORTCUT_DOWN)).act( ev -> sourceText.selectAll()).create());
        }
        if(MainApp.getConfig().isEditorSmart()) {
            EventHandlerHelper.install(sourceText.onKeyReleasedProperty(),
                    EventHandlerHelper.on(keyReleased(KeyCode.TAB)).act(ev -> handleSmartTab()).create());
            EventHandlerHelper.install(sourceText.onKeyReleasedProperty(),
                    EventHandlerHelper.on(keyReleased(KeyCode.ENTER)).act(ev -> handleSmartEnter()).create());
        }

        tab.setOnSelectionChanged(t -> {
            TreeItem<ContentNode> selected = mdBox.selectItemOnTree(mdBox.getSummary().getRoot(), extract);
            if(selected != null) {
                mdBox.getSummary().getSelectionModel().select(selected);
            }
            if(tab.isSelected()) {
                Platform.runLater(() -> {
                    sourceText.requestFocus();
                    initStats();
                });
            }
        });

        Platform.runLater(sourceText::requestFocus);
    }


    private void handleSmartEnter() {
        int precLine = sourceText.getCurrentParagraph() - 1;
        if(precLine >= 0) {
            String line = sourceText.getParagraph(precLine).toString();
            Matcher matcher = recognizeBullet.matcher(line);
            //TODO: find how combine recognize bullet and number together for breaking following if
            if(!matcher.matches()) {
                matcher = recognizeNumber.matcher(line);
            }
            if(matcher.matches()) {
                if("".equals(matcher.group(4).trim())) {
                    int positionCaret = sourceText.getCaretPosition();
                    sourceText.deleteText(positionCaret-line.length() - 1, positionCaret);
                } else {
                    sourceText.replaceSelection(matcher.group(1)+matcher.group(2)+" ");
                }
            }
        }
    }

    private void handleSmartTab() {
        int caseLine = sourceText.getCurrentParagraph();
        if(caseLine >= 0) {
            String line = sourceText.getParagraph(caseLine).toString();
            Matcher matcher = recognizeBullet.matcher(line);
            //TODO: find how combine recognize bullet and number together for breaking following if
            if(!matcher.matches()) {
                matcher = recognizeNumber.matcher(line);
            }
            if(matcher.matches()) {
                int positionCaret = sourceText.getCaretPosition();
                int delta = matcher.group(1).length() + matcher.group(2).length() + matcher.group(3).length() + 1;
                sourceText.replaceText(positionCaret-delta, positionCaret, "    "+matcher.group(2)+" "+matcher.group(4));
            }
        }
    }


    @FXML private void initialize() {
        sourceText.getStyleClass().add("markdown-editor");
        sourceText.getStylesheets().add(MainApp.class.getResource("css/editor.css").toExternalForm());

        if(MainApp.getConfig().isEditorLinenoView())
            sourceText.setParagraphGraphicFactory(LineNumberFactory.get(sourceText));

        saveButton.disableProperty().bind(savedProperty());
    }

    /*
     * Editor Toolbar Action
     */

    @FXML public void handleSaveButtonAction(ActionEvent event) {
        extract.setMarkdown(sourceText.getText());
        extract.save();
        tab.setText(extract.getTitle());
        setSaved(true);

        sourceText.requestFocus();
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

        Stage dialogStage = new CustomStage(loader, Configuration.getBundle().getString("ui.dialog.upload.img.title"));

        ImageInputDialog imageController = loader.getController();
        imageController.setSourceText(sourceText, MainApp.getZdsutils(), mainApp.getMenuController(), mainApp.getContent());
        imageController.setStage(dialogStage);

        dialogStage.show();
    }
    @FXML private void handleBulletButtonAction(ActionEvent event) {
        if(sourceText.getSelectedText().isEmpty()){
            sourceText.replaceText(sourceText.getSelection(), "- ");
        }else{
            StringBuilder sb = new StringBuilder();
            String[] lines = sourceText.getSelectedText().split("\n");
            for(String line : lines){
                sb.append("- ").append(line).append("\n");
            }

            sourceText.replaceText(sourceText.getSelection(), sb.toString());
        }

        sourceText.requestFocus();
    }

    @FXML private void handleNumberedButtonAction(ActionEvent event) {
        if(sourceText.getSelectedText().isEmpty()){
            sourceText.replaceText(sourceText.getSelection(), "1. ");
        }else{
            StringBuilder sb = new StringBuilder();
            String[] lines = sourceText.getSelectedText().split("\n");
            int i = 1;
            for(String line : lines){
                sb.append(i).append(". ").append(line).append("\n");
                i++;
            }

            sourceText.replaceText(sourceText.getSelection(), sb.toString());
        }

        sourceText.requestFocus();
    }

    @FXML private void handleHeaderButtonAction(ActionEvent event) {
        sourceText.replaceText(sourceText.getSelection(), "# " + sourceText.getSelectedText());
        sourceText.requestFocus();
    }

    @FXML private void handleQuoteButtonAction(ActionEvent event) {
        replaceAction("> ", 0, "> ", "\n\n");
    }

    @FXML private void handleBlocButtonAction(ActionEvent event) {
        StringBuilder text = new StringBuilder();
        String[] lines = sourceText.getSelectedText().split("\n");
        for (String line : lines) {
            text.append("| ").append(line).append("\n");
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
        dialog.initOwner(MainApp.getPrimaryStage());
        dialog.setTitle(Configuration.getBundle().getString("ui.editor.dialog.bloc.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.editor.dialog.bloc.header"));
        dialog.setContentText(Configuration.getBundle().getString("ui.editor.dialog.bloc.text"));

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> sourceText.replaceText(sourceText.getSelection(), "\n[[" + s + "]]\n" + text.toString()));

        sourceText.requestFocus();
    }

    @FXML private void handleTableButtonAction(ActionEvent event) throws IOException {
        // Create the custom dialog.
        Dialog<Pair<ObservableList, ObservableList<ZRow>>> dialog = new CustomDialog<>();
        dialog.setTitle(Configuration.getBundle().getString("ui.editor.button.table"));
        dialog.setHeaderText("");

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/TableEditor.fxml"));
        BorderPane tableEditor = loader.load();
        TableView<ZRow> tbView = (TableView) tableEditor.getCenter();

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
            sourceText.replaceText(sourceText.getSelection(), "\n\n" + tablestring + "\n\n");
            sourceText.requestFocus();
        });
    }

    @FXML private void handleLinkButtonAction(ActionEvent event) {
        String link = sourceText.getSelectedText();

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new CustomDialog<>();
        dialog.setTitle(Configuration.getBundle().getString("ui.editor.dialog.link.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.editor.dialog.link.header"));

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

        grid.add(new Label(Configuration.getBundle().getString("ui.editor.dialog.link.field.url")), 0, 0);
        grid.add(tLink, 1, 0);
        grid.add(new Label(Configuration.getBundle().getString("ui.editor.dialog.link.field.label")), 0, 1);
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

        result.ifPresent(tLinkTLabel -> sourceText.replaceText(sourceText.getSelection(),
                "[" + tLinkTLabel.getValue() + "](" + tLinkTLabel.getKey() + ")"));

        sourceText.requestFocus();
    }

    @FXML private void handleCodeButtonAction(ActionEvent event) {
        String code = sourceText.getSelectedText();
        if (code.trim().startsWith("```") && code.trim().endsWith("```")) {
            int start = code.trim().indexOf('\n') + 1;
            int end = code.trim().lastIndexOf('\n');
            code = code.substring(start, end);
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new CustomDialog<>();
        dialog.setTitle(Configuration.getBundle().getString("ui.editor.dialog.code.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.editor.dialog.code.header"));

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

        grid.add(new Label(Configuration.getBundle().getString("ui.editor.dialog.code.field.lang")), 0, 0);
        grid.add(tLangage, 1, 0);
        grid.add(new Label(Configuration.getBundle().getString("ui.editor.dialog.code.field.code")), 0, 1);
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

        result.ifPresent(tLangageTCode -> sourceText.replaceText(sourceText.getSelection(),
                "\n```" + tLangageTCode.getKey() + "\n" + tLangageTCode.getValue() + "\n```\n"));

        sourceText.requestFocus();
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
                        String html = markdownToHtml(sourceText.getText());
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
        if(MainApp.getConfig().isEditorRenderView()) {
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
        //performStats();
    }

    @FXML private void handleValidateButtonAction(ActionEvent event) {
        String s = StringEscapeUtils.unescapeHtml(markdownToHtml(sourceText.getText()));
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

        Stage stage = new CustomStage(Configuration.getBundle().getString("ui.window.externalrender.title"));
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
            boxRender.setCenter(renderView);
            splitPane.getItems().add(1, boxRender);
            splitPane.setDividerPositions(0.5);
        });
    }

    @FXML private void handleUnbreakableAction(ActionEvent event) {
        sourceText.replaceText(sourceText.getSelection(), sourceText.getSelectedText() + "\u00a0");
        sourceText.requestFocus();
    }

    public void performStats() {
        Readability readText = new Readability(sourceText.getText());
        countChars.setValue(Configuration.getBundle().getString("ui.statusbar.stats.chars") + readText.getCharacters());
        countWords.setValue(Configuration.getBundle().getString("ui.statusbar.stats.words") + readText.getWords());
        countTimes.setValue(FunctionTreeFactory.getNumberOfTextualReadMinutes(sourceText.getText()));
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
        dialog.setTitle(Configuration.getBundle().getString("ui.editor.dialog.goto.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.editor.dialog.goto.header"));
        dialog.setContentText(Configuration.getBundle().getString("ui.editor.dialog.goto.text"));
        dialog.initOwner(MainApp.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(line -> sourceText.positionCaret(sourceText.position(Integer.parseInt(line)-1, 0).toOffset()));
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
        if(sourceText.getSelectedText().isEmpty()){
            sourceText.replaceText(sourceText.getSelection(), defaultString);
            sourceText.moveTo(sourceText.getCaretPosition() - defaultOffsetCaret);
        }else{
            sourceText.replaceText(sourceText.getSelection(), beforeString + sourceText.getSelectedText() + afterString);
        }

        Platform.runLater(sourceText::requestFocus);
    }

    public boolean isSaved() {
        return saved.get();
    }

    public BooleanPropertyBase savedProperty() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved.set(saved);
    }

    @FXML private void handleFindReplaceDialog(){
        FunctionTreeFactory.openFindReplaceDialog(sourceText);
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
