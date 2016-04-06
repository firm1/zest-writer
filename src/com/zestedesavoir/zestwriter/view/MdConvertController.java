package com.zestedesavoir.zestwriter.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringEscapeUtils;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.w3c.dom.DOMException;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.FlipTable;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;

public class MdConvertController {
    private MdTextController mdBox;
    private Tab tab;
    private Textual extract;

    @FXML
    private WebView renderView;
    @FXML
    private StyleClassedTextArea SourceText;
    @FXML
    private Button SaveButton;
    @FXML
    private Button RefreshButton;
    @FXML
    private BorderPane BoxRender;
    @FXML
    private Button FullScreeen;

    private Corrector corrector;

    private Service<String> renderTask;
    private int xRenderPosition = 0;
    private int yRenderPosition = 0;
    private boolean isSaved = true;

    public MdConvertController() {
        super();
    }

    public MdTextController getMdBox() {
        return mdBox;
    }


    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    @FXML
    private void initialize() {
        renderView.getEngine().setUserStyleSheetLocation(getClass().getResource("content.css").toExternalForm());
        SourceText.getStyleClass().add("markdown-editor");
        SourceText.getStylesheets().add(getClass().getResource("editor.css").toExternalForm());
        SourceText.setParagraphGraphicFactory(LineNumberFactory.get(SourceText));
    }

    @FXML
    private void HandleBoldButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "**" + SourceText.getSelectedText() + "**");
    }

    @FXML
    private void HandleItalicButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "*" + SourceText.getSelectedText() + "*");
    }

    @FXML
    private void HandleQuoteButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "\n\n>" + SourceText.getSelectedText() + "\n\n");
    }

    @FXML
    private void HandleTableButtonAction(ActionEvent event) throws IOException {
        // Create the custom dialog.
        Dialog<Pair<ObservableList, ObservableList<ZRow>>> dialog = new Dialog<>();
        dialog.setTitle("Editeur de tableau");
        dialog.setHeaderText("");

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(this.getClass().getResource("static/icons/table.png").toString()));

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("TableEditor.fxml"));
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
                    data[i][j] = (String) datas.getValue().get(i).getRow().get(j);
                }
            }
            String tablestring = FlipTable.of(headers, data);
            SourceText.replaceText(SourceText.getSelection(), "\n\n" + tablestring + "\n\n");

        });
    }

    @FXML
    private void HandleLinkButtonAction(ActionEvent event) {
        String link = SourceText.getSelectedText();

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("DÃ©tail du lien");
        dialog.setHeaderText("");

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(this.getClass().getResource("static/icons/link.png").toString()));

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
    }

    @FXML
    private void HandleCodeButtonAction(ActionEvent event) {
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
        dialog.setGraphic(new ImageView(this.getClass().getResource("static/icons/code.png").toString()));

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
    }

    public void undo() {
    	SourceText.getUndoManager().undo();
    }

    public void redo() {
    	SourceText.getUndoManager().redo();
    }

    @FXML
    public void HandleSaveButtonAction(ActionEvent event) {
        extract.setMarkdown(SourceText.getText());
        extract.save();
        tab.setText(extract.getTitle());
        this.isSaved = true;
    }

    @FXML
    private void HandleFullScreeenButtonAction(ActionEvent event) {
        if (mdBox.getSplitPane().getItems().size() > 1) {
            mdBox.getSplitPane().getItems().remove(0);
        } else {
            mdBox.getSplitPane().getItems().add(0, mdBox.getSummary());
        }
    }

    public void setMdBox(MdTextController mdBox, Textual extract, Tab tab) throws IOException {
        this.mdBox = mdBox;
        this.tab = tab;
        this.extract = extract;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/Editor.fxml"));
        SplitPane writer = loader.load();

        SourceText.replaceText(extract.getMarkdown());
        SourceText.textProperty().addListener((observableValue, s, s2) -> {
            tab.setText("! " + extract.getTitle());
            this.isSaved = false;
            SourceText.getUndoManager().mark();
            updateRender();
        });
        updateRender();
        tab.getContent().addEventFilter(KeyEvent.KEY_PRESSED, t -> {
            if (t.getCode().equals(KeyCode.S) && t.isControlDown()) {
                HandleSaveButtonAction(null);
            }else if(t.getCode().equals(KeyCode.G) && t.isControlDown()) {
                // put in bold
                HandleBoldButtonAction(null);
            }else if(t.getCode().equals(KeyCode.I) && t.isControlDown()) {
                // put in italic
                HandleItalicButtonAction(null);
            }else if(t.getCode().equals(KeyCode.B) && t.isControlDown()) {
                // put it barred
                HandleBarredButtonAction(null);
            }else if(t.getCode().equals(KeyCode.K) && t.isControlDown()) {
                // put it touch
                HandleTouchButtonAction(null);
            }else if(t.getCode().equals(KeyCode.PLUS) && t.isControlDown() && t.isShiftDown()) {
                // put it exp
                HandleExpButtonAction(null);
            }else if(t.getCode().equals(KeyCode.EQUALS) && t.isControlDown()) {
                // put it ind
                HandleIndButtonAction(null);
            }else if(t.getCode().equals(KeyCode.E) && t.isControlDown()) {
                // put it center
                HandleCenterButtonAction(null);
            }else if(t.getCode().equals(KeyCode.D) && t.isControlDown() && t.isShiftDown()) {
                // put it right
                HandleRightButtonAction(null);
            }else if(t.getCode().equals(KeyCode.SPACE) && t.isControlDown() && t.isShiftDown()) {
                // unbreakable space
                HandleUnbreakableAction(null);
            } else if(t.getCode().equals(KeyCode.L) && t.isControlDown()) {
                // go to line
                HandleGoToLineAction();
            }

        });
    }

    public void updateRender() {
        if (renderTask != null) {
            if (renderTask.isRunning()) {
                renderTask.cancel();
            }
        }

        WebEngine webEngine = renderView.getEngine();

        webEngine.getLoadWorker().stateProperty()
                .addListener((ObservableValue<? extends State> ov, State oldState, State newState) -> {
                    if (newState == State.SUCCEEDED) {
                        scrollTo(renderView, xRenderPosition, yRenderPosition);
                    }
                });

        renderTask = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() {
                        StringBuilder content = new StringBuilder();
                        content.append("<!doctype html><html><head><meta charset='utf-8'><base href='");
                        if (!isCancelled()) {
                            content.append(MainApp.class.getResource("view").toExternalForm());
                        }
                        content.append("' /></head><body>");
                        if (!isCancelled()) {
                            content.append(StringEscapeUtils.unescapeHtml(markdownToHtml(SourceText.getText())));
                        }
                        content.append("<script type=\"text/x-mathjax-config\">"+
                            "MathJax.Hub.Config({"+
                                "tex2jax: {"+
                                    "inlineMath: [['$', '$']],"+
                                    "displayMath: [['$$','$$']],"+
                                    "processEscapes: true,"+
                                "},"+
                                "\"HTML-CSS\": {matchFontHeight: false},"+
                                "TeX: { extensions: ['color.js', 'cancel.js', 'enclose.js', 'bbox.js', 'mathchoice.js', 'newcommand.js', 'verb.js', 'unicode.js', 'autobold.js', 'mhchem.js'] },"+
                                "messageStyle: \"none\","+
                            "});"+
                        "</script>");
                        content.append("<script type='text/javascript' src='");
                        content.append(MainApp.class.getResource("view").toExternalForm());
                        content.append("/static/js/MathJax/MathJax.js?config=TeX-AMS-MML_HTMLorMML'></script>");
                        content.append("</body></html>");
                        return content.toString();
                    }

                };
            }
        };
        renderTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue,
                Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    break;
                case CANCELLED:
                    break;
                case SUCCEEDED:
                    yRenderPosition = getVScrollValue(renderView);
                    xRenderPosition = getHScrollValue(renderView);
                    webEngine.loadContent(renderTask.valueProperty().getValue());
                    break;
            }
        });
        renderTask.start();
    }

    @FXML
    public void HandleValidateButtonAction(ActionEvent event) {
        String s = StringEscapeUtils.unescapeHtml(markdownToHtml(SourceText.getText()));
        if(corrector == null) {
        	corrector = new Corrector();
        }
        try {
            String result = corrector.checkHtmlContent(s);
            WebEngine webEngine = renderView.getEngine();
            webEngine.loadContent("<!doctype html><html lang='fr'><head><meta charset='utf-8'><base href='file://"
                    + getClass().getResource(".").getPath() + "' /></head><body>" + result + "</body></html>");
            webEngine.setUserStyleSheetLocation(getClass().getResource("content.css").toExternalForm());
        } catch (DOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void HandleGoToLineAction() {
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Aller à la ligne");
    	dialog.setHeaderText(null);
    	dialog.setContentText("Numéro de ligne: ");

    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(line -> {
    		SourceText.position(Integer.parseInt(line), 0);
    	});
    }

    public String markdownToHtml(String chaine) {
        PythonInterpreter console = getMdBox().getPyconsole();
        console.set("text", chaine);
        console.exec(
                "render = Markdown(extensions=(ZdsExtension({'inline': False, 'emoticons': smileys}),),safe_mode = 'escape', enable_attributes = False, tab_length = 4, output_format = 'html5', smart_emphasis = True, lazy_ol = True).convert(text)");
        PyString render = console.get("render", PyString.class);
        return render.toString();
    }

    @FXML
    private void HandleBarredButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "~~" + SourceText.getSelectedText() + "~~");
    }

    @FXML
    private void HandleTouchButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "||" + SourceText.getSelectedText() + "||");
    }

    private void HandleUnbreakableAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), SourceText.getSelectedText() + "\u00a0");
    }

    @FXML
    private void HandleExpButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "^" + SourceText.getSelectedText() + "^");
    }

    @FXML
    private void HandleIndButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "~" + SourceText.getSelectedText() + "~");
    }

    @FXML
    private void HandleCenterButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "\n-> " + SourceText.getSelectedText() + " <-");
    }

    @FXML
    private void HandleRightButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "\n-> " + SourceText.getSelectedText() + " ->");
    }

    @FXML
    private void HandleBulletButtonAction(ActionEvent event) {
        String text = "";
        String[] lines = SourceText.getSelectedText().split("\n");
        for (String line : lines) {
            text += "- " + line + "\n";
        }

        SourceText.replaceText(SourceText.getSelection(), text);
    }

    @FXML
    private void HandleNumberedButtonAction(ActionEvent event) {
        String text = "";
        String[] lines = SourceText.getSelectedText().split("\n");
        int i = 1;
        for (String line : lines) {
            text += i + ". " + line + "\n";
            i++;
        }

        SourceText.replaceText(SourceText.getSelection(), text);
    }

    @FXML
    private void HandleHeaderButtonAction(ActionEvent event) {
        SourceText.replaceText(SourceText.getSelection(), "\n# " + SourceText.getSelectedText());
    }

    @FXML
    private void HandleBlocButtonAction(ActionEvent event) {
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
