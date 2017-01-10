package com.zestedesavoir.zestwriter.view;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.ContentNode;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Corrector;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.com.CustomStyledClassedTextArea;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import javafx.application.Platform;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

public class MdConvertController {
    public static final Pattern recognizeNumber = Pattern.compile("^(\\s*)([\\d][\\.]) (\\s*)(.*)");
    public static final Pattern recognizeBullet = Pattern.compile("^(\\s*)([*|-]) (\\s*)(.*)");
    public static Corrector corrector;
    private final Logger logger = LoggerFactory.getLogger(MdConvertController.class);
    private MdTextController mdBox;
    private Service<String> renderTask;
    private int xRenderPosition = 0;
    private int yRenderPosition = 0;
    private StringProperty countChars = new SimpleStringProperty();
    private StringProperty countWords = new SimpleStringProperty();
    private StringProperty countTimes = new SimpleStringProperty();
    private BooleanPropertyBase needRefresh = new SimpleBooleanProperty(false);
    private BooleanPropertyBase saved  = new SimpleBooleanProperty(true);
    @FXML private WebView renderView;
    @FXML private SplitPane splitPane;
    @FXML private BorderPane boxRender;
    @FXML private Tab tab;
    @FXML private CustomStyledClassedTextArea sourceText;

    public void setMdBox(MdTextController mdBox, Textual extract) {
        this.mdBox = mdBox;
        initCurrentComponents(extract);
        tab.setText(extract.getTitle());

        Platform.runLater(() -> {
            saved.addListener((observableValue, s, s2) -> {
                if(s2) {
                    tab.setText(extract.getTitle());
                } else {
                    tab.setText("! " + extract.getTitle());
                }
            });
            sourceText.replaceText(extract.getMarkdown());
            initStats();
            sourceText.getUndoManager().forgetHistory();
            sourceText.textProperty().addListener((observableValue, s, s2) -> {
                sourceText.getUndoManager().mark();
                getMdBox().setCurrentSaved(false);
                if(renderTask.getState().equals(State.READY)) {
                    renderTask.start();
                    needRefresh.set(false);
                } else {
                    needRefresh.set(true);
                }
            });
            if(renderTask.getState().equals(State.READY)) {
                renderTask.start();
            }
        });

        tab.setOnSelectionChanged(t -> {
            initCurrentComponents(extract);
            TreeItem<ContentNode> selected = mdBox.selectItemOnTree(mdBox.getSummary().getRoot(), extract);
            Platform.runLater(() -> {
                if(selected != null) {
                    mdBox.getSummary().getSelectionModel().select(selected);
                }
                sourceText.requestFocus();
                initStats();
            });
        });
    }

    private void initCurrentComponents(Textual extract) {
        mdBox.currentSaved = saved;
        mdBox.setCurrentRenderView(renderView);
        mdBox.setCurrentSourceText(sourceText);
        mdBox.setCurrentBoxRender(boxRender);
        mdBox.setCurrentExtract(extract);
        mdBox.getSaveButton().disableProperty().bind(saved);
        mdBox.initKeyMapping(sourceText);
    }

    public MdTextController getMdBox() {
        return mdBox;
    }

    @FXML private void initialize() {
        sourceText.getStylesheets().add(MainApp.class.getResource("css/editor.css").toExternalForm());
        sourceText.setStyle("-fx-font-family: \"" + MainApp.getConfig().getEditorFont() + "\";-fx-font-size: " + MainApp.getConfig().getEditorFontsize() + ";");

        if(MainApp.getConfig().isEditorLinenoView())
            sourceText.setParagraphGraphicFactory(LineNumberFactory.get(sourceText));

        if(MainApp.getConfig().isEditorRenderView()) {
            initRenderTask();
        } else {
            splitPane.getItems().remove(1);
        }

        Platform.runLater(sourceText::requestFocus);
    }


    private void initRenderTask() {
        renderTask = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        String html = getMdBox().markdownToHtml(sourceText.getText());
                        if (html != null) {
                            return MainApp.getMdUtils().addHeaderAndFooter(html);
                        } else {
                            renderTask.wait(5000);
                            throw new IOException();
                        }
                    }
                };
            }
        };

        renderTask.setOnFailed(t -> renderTask.restart());
        renderTask.setOnSucceeded(t -> {
            Platform.runLater(() -> {
                yRenderPosition = getVScrollValue(renderView);
                xRenderPosition = getHScrollValue(renderView);
                renderView.getEngine().loadContent(renderTask.getValue());
                performStats();
                renderTask.reset();
                if(needRefresh.getValue()) {
                    renderTask.start();
                }
            });
        });
        renderView.getEngine().getLoadWorker().stateProperty()
            .addListener((ObservableValue<? extends State> ov, State oldState, State newState) -> {
                if (newState == State.SUCCEEDED) {
                    Platform.runLater(() -> scrollTo(renderView, xRenderPosition, yRenderPosition));
                }
            });
    }



    public void performStats() {
        Readability readText = new Readability(sourceText.getText());
        countChars.setValue(Configuration.getBundle().getString("ui.statusbar.stats.chars") + readText.getCharacters());
        countWords.setValue(Configuration.getBundle().getString("ui.statusbar.stats.words") + readText.getWords());
        countTimes.setValue(FunctionTreeFactory.getNumberOfTextualReadMinutes(sourceText.getText()));
    }

    public void initStats() {
        String fontSize="-fx-font-size: 0.9em;";
        getMdBox().getMainApp().getMenuController().getHBottomBox().getChildren().clear();
        getMdBox().getMainApp().getMenuController().getHBottomBox().getColumnConstraints().clear();
        getMdBox().getMainApp().getMenuController().getHBottomBox().setPadding(new Insets(5, 5, 5, 5));
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
        getMdBox().getMainApp().getMenuController().getHBottomBox().getColumnConstraints().addAll(c1, c2, c3, c4);
        getMdBox().getMainApp().getMenuController().getHBottomBox().add(times, 1, 0);
        getMdBox().getMainApp().getMenuController().getHBottomBox().add(chars, 2, 0);
        getMdBox().getMainApp().getMenuController().getHBottomBox().add(words, 3, 0);

        chars.textProperty().bind(countChars);
        words.textProperty().bind(countWords);
        times.textProperty().bind(countTimes);
        performStats();
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
