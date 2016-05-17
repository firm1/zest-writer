package com.zestedesavoir.zestwriter.view;

import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringEscapeUtils;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.FlipTable;
import com.zestedesavoir.zestwriter.view.com.CustomStyledClassedTextArea;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import com.zestedesavoir.zestwriter.view.dialogs.FindReplaceDialog;

import javafx.application.Platform;
import javafx.beans.property.BooleanPropertyBase;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MdConvertController {
    private MainApp mainApp;
    private Configuration config;
    private MdTextController mdBox;
    private Tab tab;
    private Textual extract;
    private Corrector corrector;
    private Service<String> renderTask;
    private final Logger logger;
    private int xRenderPosition = 0;
    private int yRenderPosition = 0;
    private BooleanPropertyBase isSaved = new BooleanPropertyBase(true) {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Object getBean() {
            return null;
        }
    };

    @FXML private WebView renderView;
    @FXML private Button SaveButton;
    @FXML private Button RefreshButton;
    @FXML private BorderPane BoxEditor;
    @FXML private BorderPane BoxRender;
    @FXML private Button FullScreeen;
    private CustomStyledClassedTextArea SourceText;

    public MdConvertController() {
        super();
        logger = LoggerFactory.getLogger(MdConvertController.class);
        SourceText = new CustomStyledClassedTextArea();
    }

    public MdTextController getMdBox() {
        return mdBox;
    }

    public void setMdBox(MdTextController mdBox, Textual extract, Tab tab) throws IOException {
        this.mainApp = mdBox.getMainApp();
        this.config = mainApp.getConfig();
        this.mdBox = mdBox;
        this.tab = tab;
        this.extract = extract;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("fxml/Editor.fxml"));
        loader.load();

        if(mainApp.getConfig().getEditorToolbarView().equals("no")){
            BoxEditor.setTop(null);
            BoxRender.setTop(null);
        }

        BoxEditor.setCenter(SourceText);
        SourceText.setStyle("-fx-font-family: \"" + config.getEditorFont() + "\";-fx-font-size: " + config.getEditorFontsize() + ";");
        initRenderTask();
        Platform.runLater(() -> {
            SourceText.replaceText(extract.getMarkdown());
            SourceText.getUndoManager().forgetHistory();
        });
        SourceText.textProperty().addListener((observableValue, s, s2) -> {
            tab.setText("! " + extract.getTitle());
            this.isSaved.setValue(false);
            SourceText.getUndoManager().mark();
            updateRender();
        });

        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, SHORTCUT_DOWN), () -> HandleSaveButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.G, SHORTCUT_DOWN), () -> HandleBoldButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.I, SHORTCUT_DOWN), () -> HandleItalicButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.B, SHORTCUT_DOWN), () -> HandleBarredButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.K, SHORTCUT_DOWN), () -> HandleTouchButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.PLUS, SHORTCUT_DOWN), () -> HandleExpButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.EQUALS, SHORTCUT_DOWN), () -> HandleIndButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.E, SHORTCUT_DOWN), () -> HandleCenterButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, SHORTCUT_DOWN, SHIFT_DOWN), () -> HandleRightButtonAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.SPACE, SHORTCUT_DOWN), () -> HandleUnbreakableAction(null));
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.L, SHORTCUT_DOWN), this::HandleGoToLineAction);
        tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, SHORTCUT_DOWN), this::HandleFindReplaceDialog);
        if(FunctionTreeFactory.isMacOs()) {
            tab.getContent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Q, SHORTCUT_DOWN), () -> SourceText.selectAll());
        }

        tab.setOnSelectionChanged(t -> {
            if(tab.isSelected()) {
                Platform.runLater(() -> {
                    SourceText.requestFocus();
                });
            }
        });

        Platform.runLater(() -> {
            SourceText.requestFocus();
        });
    }


    @FXML private void initialize() {
        SourceText.getStyleClass().add("markdown-editor");
        SourceText.getStylesheets().add(MainApp.class.getResource("css/editor.css").toExternalForm());
        SourceText.setParagraphGraphicFactory(LineNumberFactory.get(SourceText));
        SaveButton.disableProperty().bind(isSaved);
    }

    /*
     * Editor Toolbar Action
     */

    @FXML public void HandleSaveButtonAction(ActionEvent event) {
        extract.setMarkdown(SourceText.getText());
        extract.save();
        tab.setText(extract.getTitle());
        this.isSaved.setValue(true);

        SourceText.requestFocus();
    }

    @FXML private void HandleBoldButtonAction(ActionEvent event) {
        replaceAction("****", 2, "**", "**");
    }

    @FXML private void HandleItalicButtonAction(ActionEvent event) {
        replaceAction("**", 1, "*", "*");
    }

    @FXML private void HandleBarredButtonAction(ActionEvent event) {
        replaceAction("~~~~", 2, "~~", "~~");
    }

    @FXML private void HandleTouchButtonAction(ActionEvent event) {
        replaceAction("||||", 2, "||", "||");
    }

    @FXML private void HandleExpButtonAction(ActionEvent event) {
        replaceAction("^^", 1, "^", "^");
    }

    @FXML private void HandleIndButtonAction(ActionEvent event) {
        replaceAction("~~", 1, "~", "~");
    }

    @FXML private void HandleCenterButtonAction(ActionEvent event) {
        replaceAction("\n->  <-", 3, "\n-> ", " <-");
    }

    @FXML private void HandleRightButtonAction(ActionEvent event) {
        replaceAction("\n->  ->", 3, "\n-> ", " ->\n");
    }

    @FXML private void HandleBulletButtonAction(ActionEvent event) {
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

    @FXML private void HandleNumberedButtonAction(ActionEvent event) {
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

    @FXML private void HandleHeaderButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "# " + SourceText.getSelectedText());
        SourceText.requestFocus();
    }

    @FXML private void HandleQuoteButtonAction(ActionEvent event) {
        replaceAction("> ", 0, "> ", "\n\n");
    }

    @FXML private void HandleBlocButtonAction(ActionEvent event) {
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

        ChoiceDialog<String> dialog = new ChoiceDialog<>("information", choices);
        dialog.setTitle("Choix du bloc");
        dialog.setHeaderText("Votre type de bloc");
        dialog.setContentText("Type de bloc: ");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            SourceText.replaceText(SourceText.getSelection(), "\n[[" + result.get() + "]]\n" + text);
        }

        SourceText.requestFocus();
    }

    @FXML private void HandleTableButtonAction(ActionEvent event) throws IOException {
        // Create the custom dialog.
        Dialog<Pair<ObservableList, ObservableList<ZRow>>> dialog = new Dialog<>();
        dialog.setTitle("Editeur de tableau");
        dialog.setHeaderText("");

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(MainApp.class.getResource("assets/static/icons/table.png").toString()));

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("fxml/TableEditor.fxml"));
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

    @FXML private void HandleLinkButtonAction(ActionEvent event) {
        String link = SourceText.getSelectedText();

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Détail du lien");
        dialog.setHeaderText("");

        // Set the icon (must be included in the project).
        dialog.setGraphic(IconFactory.createLinkIcon());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tLink = new TextField();
        tLink.setText(link);
        TextField tLabel = new TextField();
        tLabel.setText(link);

        grid.add(new Label("Lien:"), 0, 0);
        grid.add(tLink, 1, 0);
        grid.add(new Label("Titre du lien"), 0, 1);
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

    @FXML private void HandleCodeButtonAction(ActionEvent event) {
        String code = SourceText.getSelectedText();
        if (code.trim().startsWith("```") && code.trim().endsWith("```")) {
            int start = code.trim().indexOf('\n') + 1;
            int end = code.trim().lastIndexOf('\n');
            code = code.substring(start, end);
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Editeur de code");
        dialog.setHeaderText("");

        // Set the icon (must be included in the project).
        dialog.setGraphic(IconFactory.createCodeIcon());

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tLangage = new TextField();
        TextArea tCode = new TextArea();
        tCode.setText(code);

        grid.add(new Label("Langage:"), 0, 0);
        grid.add(tLangage, 1, 0);
        grid.add(new Label("Code"), 0, 1);
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

    @FXML private void HandleFullScreeenButtonAction(ActionEvent event) {
        if (mdBox.getSplitPane().getItems().size() > 1) {
            mdBox.getSplitPane().getItems().remove(0);
        } else {
            mdBox.getSplitPane().getItems().add(0, mdBox.treePane);
            mdBox.getSplitPane().setDividerPositions(0.2);
            SplitPane.setResizableWithParent(mdBox.treePane,Boolean.FALSE);
        }
    }

    private void initRenderTask() {
        StringBuilder before = new StringBuilder();
        StringBuilder after = new StringBuilder();

        before.append("<!doctype html><html><head><meta charset='utf-8'><base href='");
        before.append(MainApp.class.getResource("assets").toExternalForm());
        before.append("/' />");
        before.append("<link rel=\"stylesheet\" href=\"");
        before.append(MainApp.class.getResource("css/content.css").toExternalForm());
        before.append("\" />");
        before.append("<link rel=\"stylesheet\" href=\"");
        before.append(MainApp.class.getResource("assets/static").toExternalForm());
        before.append("/js/katex/katex.min.css\" />");
        before.append("<script src=\"").append(MainApp.class.getResource("assets/static").toExternalForm());
        before.append("/js/katex/katex.min.js\"></script>");
        before.append("<script src=\"").append(MainApp.class.getResource("assets/static").toExternalForm());
        before.append("/js/katex/contrib/auto-render.min.js\"></script>");
        before.append("</head><body>");

        after.append("<script>"+
                "renderMathInElement("+
                "document.body,"+
                "{"+
                "delimiters: ["+
                "{left: \"$$\", right: \"$$\", display: true},"+
                "{left: \"$\", right: \"$\", display: false},"+
                "]"+
                "}"+
                ");"+
                "</script>");
        after.append("</body></html>");

        renderTask = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        return before.toString()+markdownToHtml(SourceText.getText())+after.toString();
                    }

                };
            }
        };
        renderTask.setOnFailed(t -> {
            renderTask.reset();
        });
        renderTask.setOnSucceeded(t -> {
            yRenderPosition = getVScrollValue(renderView);
            xRenderPosition = getHScrollValue(renderView);
            renderView.getEngine().loadContent(renderTask.valueProperty().getValue());
            renderTask.reset();

        });
    }

    @FXML private void updateRender() {

        if (renderTask != null) {
            if (!renderTask.isRunning()) {
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

    @FXML private void HandleValidateButtonAction(ActionEvent event) {
        String s = StringEscapeUtils.unescapeHtml(markdownToHtml(SourceText.getText()));
        if(corrector == null) {
        	corrector = new Corrector();
        }
        try {
            String result = corrector.checkHtmlContent(s);
            WebEngine webEngine = renderView.getEngine();
            webEngine.loadContent("<!doctype html><html lang='fr'><head><meta charset='utf-8'><base href='file://"
                    + MainApp.class.getResource(".").getPath() + "' /></head><body>" + result + "</body></html>");
            webEngine.setUserStyleSheetLocation(MainApp.class.getResource("css/content.css").toExternalForm());
        } catch (DOMException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @FXML private void HandleUnbreakableAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), SourceText.getSelectedText() + "\u00a0");
        SourceText.requestFocus();
    }



    public void HandleGoToLineAction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Aller à la ligne");
        dialog.setHeaderText(null);
        dialog.setContentText("Numéro de ligne: ");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(line -> SourceText.positionCaret(SourceText.position(Integer.parseInt(line)-1, 0).toOffset()));
    }


    public String markdownToHtml(String chaine) {
        PythonInterpreter console = getMdBox().getPyconsole();
        if(console != null) {
            console.set("text", chaine);
            console.exec(
                    "render = Markdown(extensions=(ZdsExtension({'inline': False, 'emoticons': smileys}),),safe_mode = 'escape', enable_attributes = False, tab_length = 4, output_format = 'html5', smart_emphasis = True, lazy_ol = True).convert(text)");
            PyString render = console.get("render", PyString.class);
            return render.toString();
        } else {
            return "<p>Chargement ...</p>";
        }
    }

    private void replaceAction(String defaultString, int defaultOffsetCaret, String beforeString, String afterString) {
        if(SourceText.getSelectedText().isEmpty()){
            SourceText.replaceText(SourceText.getSelection(), defaultString);
            SourceText.moveTo(SourceText.getCaretPosition() - defaultOffsetCaret);
        }else{
            SourceText.replaceText(SourceText.getSelection(), beforeString + SourceText.getSelectedText() + afterString);
        }

        Platform.runLater(() -> {
            SourceText.requestFocus();
        });
    }

    public boolean isSaved() {
        return isSaved.getValue();
    }

    public void setSaved(boolean isSaved) {
        this.isSaved.setValue(isSaved);
    }

    @FXML private void HandleFindReplaceDialog(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("fxml/FindReplaceDialog.fxml"));

        try{
            AnchorPane optionsDialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Rechecher / Remplacer");

            Scene scene = new Scene(optionsDialog);
            dialogStage.setScene(scene);
            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("assets/static/icons/logo.png")));
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            FindReplaceDialog findReplaceDialog = loader.getController();
            findReplaceDialog.setMainApp(mainApp);
            findReplaceDialog.setWindow(dialogStage);
            findReplaceDialog.setMdConvertController(this);

            dialogStage.show();
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
    }


    public StyleClassedTextArea getSourceText(){
        return SourceText;
    }

    /**
     * Scrolls to the specified position.
     *
     * @param view
     *            web view that shall be scrolled
     * @param x
     *            horizontal scroll value
     * @param y
     *            vertical scroll value
     */
    public void scrollTo(WebView view, int x, int y) {
        view.getEngine().executeScript("window.scrollTo(" + x + ", " + y + ")");
    }

    /**
     * Returns the vertical scroll value, i.e. thumb position. This is
     * equivalent to {@link javafx.scene.control.ScrollBar#getValue().
     *
     * @param view
     * @return vertical scroll value
     */
    public int getVScrollValue(WebView view) {
        return (Integer) view.getEngine().executeScript("document.body.scrollTop");
    }

    /**
     * Returns the horizontal scroll value, i.e. thumb position. This is
     * equivalent to {@link javafx.scene.control.ScrollBar#getValue()}.
     *
     * @param view
     * @return horizontal scroll value
     */
    public int getHScrollValue(WebView view) {
        return (Integer) view.getEngine().executeScript("document.body.scrollLeft");
    }
}
