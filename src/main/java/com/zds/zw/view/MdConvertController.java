package com.zds.zw.view;

import com.zds.zw.MainApp;
import com.zds.zw.model.ContentNode;
import com.zds.zw.model.Textual;
import com.zds.zw.utils.Configuration;
import com.zds.zw.utils.readability.Readability;
import com.zds.zw.view.com.FunctionTreeFactory;
import javafx.application.Platform;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MdConvertController {
    //public static Corrector corrector;
    private final Logger logger = LoggerFactory.getLogger(MdConvertController.class);
    private MdTextController mdBox;
    private int xRenderPosition = 0;
    private int yRenderPosition = 0;
    private final StringProperty countChars = new SimpleStringProperty();
    private final StringProperty countWords = new SimpleStringProperty();
    private final StringProperty countTimes = new SimpleStringProperty();
    private final BooleanPropertyBase needRefresh = new SimpleBooleanProperty(false);
    private final BooleanPropertyBase saved  = new SimpleBooleanProperty(true);
    @FXML private WebView renderView;
    @FXML private SplitPane splitPane;
    @FXML private BorderPane boxRender;
    @FXML private Tab tab;
    private TextArea sourceText;
    @FXML private BorderPane container;
    Thread async = null;


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
            sourceText.replaceText(new IndexRange(0, sourceText.getLength()), extract.getMarkdown());
            initStats();

            String contentHtml = getMdBox().markdownToHtml(sourceText.getText());
            if (contentHtml != null) {
                renderView.getEngine().loadContent(MainApp.getMdUtils().addHeaderAndFooter(contentHtml));
            }

            sourceText.textProperty().addListener((observable, oldValue, newValue) -> {
                getMdBox().setCurrentSaved(false);
                if(async != null) {
                    async.stop();
                }
                async = setTimeout(() -> {
                    yRenderPosition = getVScrollValue(renderView);
                    xRenderPosition = getHScrollValue(renderView);
                    String html = getMdBox().markdownToHtml(sourceText.getText());
                    if (html != null) {
                        renderView.getEngine().loadContent(MainApp.getMdUtils().addHeaderAndFooter(html));
                        scrollTo(renderView, xRenderPosition, yRenderPosition);
                    }
                }, 500);
            });
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

    public static Thread setTimeout(Runnable runnable, int delay){
        Thread process = new Thread(() -> {
            try {
                Thread.sleep(delay);
                Platform.runLater(() -> {
                    runnable.run();
                });
            }
            catch (Exception e){
                System.err.println(e);
            }
        });
        process.start();
        return process;
    }

    public BooleanPropertyBase getSaved() {
        return saved;
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

    public MdConvertController() {
        sourceText = new TextArea();
        sourceText.setStyle("markdown-editor");
        sourceText.setWrapText(true);
    }

    @FXML private void initialize() {
        container.setCenter(sourceText);
        sourceText.getStylesheets().add(MainApp.class.getResource("css/editor.css").toExternalForm());
        sourceText.setStyle("-fx-font-family: \"" + MainApp.getConfig().getEditorFont() + "\";-fx-font-size: " + MainApp.getConfig().getEditorFontsize() + ";");

        if(!MainApp.getConfig().isEditorRenderView()) {
            splitPane.getItems().remove(1);
        }

        Platform.runLater(sourceText::requestFocus);
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
        view.getEngine().executeScript("window.scrollTo(" + x + ", " + y + ");");
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
