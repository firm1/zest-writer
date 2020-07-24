package com.zds.zw.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.zw.MainApp;
import com.zds.zw.model.Content;
import com.zds.zw.model.ContentNode;
import com.zds.zw.model.Textual;
import com.zds.zw.utils.Configuration;
import com.zds.zw.utils.FlipTable;
import com.zds.zw.utils.ZMD;
import com.zds.zw.view.com.*;
import com.zds.zw.view.dialogs.ImageInputDialog;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import org.fxmisc.wellbehaved.event.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.InputMap.consume;
import static org.fxmisc.wellbehaved.event.InputMap.sequence;

public class MdTextController {
    @FXML
    public AnchorPane treePane;
    public BooleanPropertyBase currentSaved;
    private boolean zmdStarted = false;
    private MainApp mainApp;
    private ZMD zmd;
    @FXML
    private VBox contentBox;
    @FXML
    private TabPane editorList;
    @FXML
    private Tab home;
    @FXML
    private TreeView<ContentNode> summary;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Button saveButton;
    @FXML
    private ToolBar editorToolBar;
    private WebView currentRenderView;
    private BorderPane currentBoxRender;
    private TextArea currentSourceText;
    private final ObjectPropertyBase<Textual> currentExtract = new SimpleObjectProperty<>(null);
    private final Logger log = LoggerFactory.getLogger(getClass());

    public MdTextController() {
    }

    public boolean isZmdStarted() {
        return zmdStarted;
    }

    public void setZmdStarted(boolean zmdStarted) {
        this.zmdStarted = zmdStarted;
    }

    public TabPane getEditorList() {
        return editorList;
    }

    public void setEditorList(TabPane editorList) {
        this.editorList = editorList;
    }

    public TreeView<ContentNode> getSummary() {
        return summary;
    }

    public void setSummary(TreeView<ContentNode> summary) {
        this.summary = summary;
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }

    public WebView getCurrentRenderView() {
        return currentRenderView;
    }

    public void setCurrentRenderView(WebView currentRenderView) {
        this.currentRenderView = currentRenderView;
    }

    public BorderPane getCurrentBoxRender() {
        return currentBoxRender;
    }

    public void setCurrentBoxRender(BorderPane currentBoxRender) {
        this.currentBoxRender = currentBoxRender;
    }

    public TextArea getCurrentSourceText() {
        return currentSourceText;
    }

    public void setCurrentSourceText(TextArea currentSourceText) {
        this.currentSourceText = currentSourceText;
    }

    public ZMD getZmd() {
        return zmd;
    }

    public void setZmd(ZMD zmd) {
        this.zmd = zmd;
    }

    @FXML
    private void initialize() {
        if (MainApp.getConfig().isEditorRenderView())
            loadConsolePython();

        loadFonts();
        editorList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mainApp.getMenuController().setIsOnReadingTab(!(newValue.getContent() instanceof SplitPane))
        );
        home.setOnSelectionChanged(t -> {
            mainApp.getMenuController().getHBottomBox().getChildren().clear();
            setCurrentExtract(null);
            currentBoxRender = null;
            currentRenderView = null;
            currentSourceText = null;
        });
        editorToolBar.setVisible(MainApp.getConfig().isEditorToolbarView());
        editorToolBar.visibleProperty().bind(currentExtractProperty().isNotNull());
    }

    public Textual getCurrentExtract() {
        return currentExtract.get();
    }

    public void setCurrentExtract(Textual currentExtract) {
        this.currentExtract.set(currentExtract);
    }

    public ObjectPropertyBase<Textual> currentExtractProperty() {
        return currentExtract;
    }

    public void setCurrentSaved(boolean currentSaved) {
        this.currentSaved.set(currentSaved);
    }

    public BooleanPropertyBase currentSavedProperty() {
        return currentSaved;
    }

    public void loadConsolePython() {
        new Thread(() -> {
            zmd = new ZMD();
            log.info("ZMD STARTED");
            setZmdStarted(true);
        }).start();
    }

    /**
     * Load fonts Merriweather and FiraMono for views
     */
    public void loadFonts() {
        new Thread(() -> {
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-Regular.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-Black.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-Bold.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-BoldItalic.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-HeavyItalic.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-Italic.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-Light.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/Merriweather-LightItalic.ttf").toExternalForm(), 10);

            Font.loadFont(MainApp.class.getResource("assets/static/fonts/FiraMono-Regular.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/FiraMono-Bold.ttf").toExternalForm(), 10);

            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-Black.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-BlackItalic.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-Bold.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-BoldItalic.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-ExtraLight.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-ExtraLightItalic.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-Italic.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-Light.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-Regular.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-SemiBold.ttf").toExternalForm(), 10);
            Font.loadFont(MainApp.class.getResource("assets/static/fonts/SourceSansPro-SemiBoldItalic.ttf").toExternalForm(), 10);

        }).start();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        mainApp.contentProperty().addListener(change -> {
            log.info("Détection du changement de contenu");
            FunctionTreeFactory.clearContent(mainApp.getExtracts(), editorList, () -> {
                log.info("Début de la fonction à executer après le clear");
                summary.setRoot(null);
                if (mainApp.contentProperty().isNotNull().get()) {
                    openContent(mainApp.getContent());
                }
                return null;
            });

        });

        mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.TAB, SHORTCUT_DOWN), () -> switchTabTo(true));
        mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.TAB, SHORTCUT_DOWN, SHIFT_DOWN), () -> switchTabTo(false));
        if (FunctionTreeFactory.isMacOs()) {
            mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Z, SHORTCUT_DOWN), this::closeCurrentTab);
        } else {
            mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.W, SHORTCUT_DOWN), this::closeCurrentTab);
        }

        refreshRecentProject();
    }

    /**
     * Refresh contents display at home with config file content
     */
    public void refreshRecentProject() {
        contentBox.getChildren().clear();
        ObjectMapper mapper = new ObjectMapper();
        GridPane gPane = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gPane.getColumnConstraints().addAll(col1, col2);
        gPane.setHgap(10);
        gPane.setVgap(10);
        gPane.setPadding(new Insets(10, 10, 10, 10));
        int row = 0;
        int col = 0;
        int size = 2;
        for (String recentFilePath : MainApp.getConfig().getActions()) {
            File manifest = new File(recentFilePath + File.separator + "manifest.json");
            if (manifest.exists()) {
                BorderPane bPane = new BorderPane();
                bPane.setPadding(new Insets(10, 10, 10, 10));
                bPane.getStyleClass().add("box-content");
                try {
                    Content c = mapper.readValue(manifest, Content.class);
                    c.setRootContent(c, recentFilePath);
                    Hyperlink link = new Hyperlink(c.getTitle());
                    Label description = new Label(c.getDescription());
                    description.setWrapText(true);
                    MaterialDesignIconView type = IconFactory.createContentIcon(c.getType());
                    link.setOnAction(t -> mainApp.setContent(c));
                    bPane.setTop(link);
                    bPane.setBottom(description);
                    bPane.setLeft(type);
                    gPane.add(bPane, col % size, row);
                } catch (IOException e) {
                    log.error("Impossible de lire le contenu répertorié dans : " + recentFilePath, e);
                }
                col++;
                if (col % size == 0) {
                    row++;
                }
            }
        }
        contentBox.getChildren().add(gPane);
    }

    /**
     * Close tab on TabPane with fire close request event
     */
    public void closeCurrentTab() {
        if (editorList.getTabs().size() > 1) {
            Tab selectedTab = editorList.getSelectionModel().getSelectedItem();
            Event.fireEvent(selectedTab, new Event(Tab.TAB_CLOSE_REQUEST_EVENT));
        }
    }

    /**
     * Switch on new tab on TabPane
     *
     * @param right if true, then switch on right side, else switch on left side
     */
    public void switchTabTo(boolean right) {
        int size = editorList.getTabs().size();

        if (size > 0) {
            int selectedIndex = editorList.getSelectionModel().getSelectedIndex();

            if (right) {
                if (selectedIndex < size - 1) {
                    editorList.getSelectionModel().selectNext();
                } else {
                    editorList.getSelectionModel().select(editorList.getTabs().get(0));
                }
            } else {
                if (selectedIndex > 0) {
                    editorList.getSelectionModel().selectPrevious();
                } else {
                    editorList.getSelectionModel().select(editorList.getTabs().get(size - 1));
                }
            }
        }
    }

    /**
     * Select any item on Tree
     *
     * @param item    from which one wants to search
     * @param textual textual open on tab which one wants to select
     * @return TreeItem what you want to select
     */
    public TreeItem<ContentNode> selectItemOnTree(TreeItem<ContentNode> item, Textual textual) {
        for (TreeItem<ContentNode> node : item.getChildren()) {
            if (node.getValue().getFilePath().equals(textual.getFilePath())) {
                return node;
            } else {
                TreeItem<ContentNode> it = selectItemOnTree(node, textual);
                if (it != null) {
                    return it;
                }
            }
        }
        return null;
    }

    public String markdownToHtml(String chaine) {
        if (zmd != null) {
            return zmd.toHtml(chaine);
        } else {
            return null;
        }
    }

    public void createTabExtract(Textual extract) throws IOException {
        log.debug("Tentative de création d'un nouvel onglet pour " + extract.getTitle());
        extract.loadMarkdown();
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/Editor.fxml"));
        Tab writer = loader.load();
        log.trace("Fichier Editor.fxml chargé");
        editorList.getTabs().add(writer);
        editorList.getSelectionModel().select(writer);

        MdConvertController controllerConvert = loader.getController();
        controllerConvert.setMdBox(this, extract);
        writer.setId(extract.getFilePath());

        writer.setOnCloseRequest(t -> {
            log.debug("Demande de fermeture de l'onglet " + writer.getText());
            if (!controllerConvert.getSaved().get()) {
                log.debug("Onglet en cours de rédaction");
                Alert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(Configuration.getBundle().getString("ui.alert.tab.close.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.alert.tab.close.header") + " : " + writer.getText().substring(1));
                alert.setContentText(Configuration.getBundle().getString("ui.alert.tab.close.text"));

                ButtonType buttonTypeYes = new ButtonType(Configuration.getBundle().getString("ui.yes"));
                ButtonType buttonTypeNo = new ButtonType(Configuration.getBundle().getString("ui.no"));
                ButtonType buttonTypeCancel = new ButtonType(Configuration.getBundle().getString("ui.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(400, 200);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() != buttonTypeCancel) {
                        if (result.get() == buttonTypeYes) {
                            handleSaveButtonAction();
                        }
                        Event.fireEvent(writer, new Event(Tab.CLOSED_EVENT));
                    } else {
                        t.consume();
                    }
                }
            } else {
                Event.fireEvent(writer, new Event(Tab.CLOSED_EVENT));
            }
        });

        writer.setOnClosed(t -> {
            log.debug("Execution de la fermeture de l'onglet");
            editorList.getTabs().remove(writer);
            mainApp.getExtracts().remove(extract);
            t.consume();
            if (getSplitPane().getItems().size() <= 1 && editorList.getTabs().size() == 1) {
                addTreeSummary();
            }
        });

        summary.getSelectionModel().select(selectItemOnTree(summary.getRoot(), extract));
        mainApp.getExtracts().add(extract);
        log.info("Nouvel onglet crée pour " + extract.getTitle());
    }

    public MdTextController getThis() {
        return this;
    }

    public void openContent(Content content) {
        String filePath = content.getBasePath();
        mainApp.getExtracts().clear();
        log.debug("Tentative d'ouverture du contenu stocké dans " + filePath);

        // load content informations
        MainApp.getZdsutils().setLocalSlug(content.getSlug());
        TreeItem<ContentNode> rootItem = new TreeItem<>(content);
        rootItem.setExpanded(true);
        summary.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        rootItem = FunctionTreeFactory.buildChild(rootItem);
        summary.setRoot(rootItem);
        summary.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItem<ContentNode> item = summary.getSelectionModel().getSelectedItem();

                if (item.getValue() instanceof Textual) {
                    Textual myTextual = (Textual) item.getValue();
                    if (item.getValue().getFilePath() != null) {
                        if (!mainApp.getExtracts().contains(myTextual)) {
                            try {
                                createTabExtract(myTextual);
                            } catch (IOException e) {
                                log.error("Problème lors de la création de l'extrait", e);
                            }
                        } else {
                            editorList.getSelectionModel().select(FunctionTreeFactory.getTabFromTextual(editorList, myTextual));
                        }
                    }
                }
            }
        });
        summary.setCellFactory(new Callback<TreeView<ContentNode>, TreeCell<ContentNode>>() {
            TreeItem<ContentNode> dragObject;

            @Override
            public TreeCell<ContentNode> call(TreeView<ContentNode> extractTreeView) {
                MdTreeCell treeCell = new MdTreeCell(getThis());

                treeCell.setOnDragDetected(mouseEvent -> {
                    dragObject = treeCell.getTreeItem();
                    if (treeCell.getItem() == null) {
                        return;
                    }
                    Dragboard dragBoard = treeCell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.put(DataFormat.PLAIN_TEXT, treeCell.getTreeItem().toString());
                    dragBoard.setContent(content);
                    mouseEvent.consume();
                });

                treeCell.setOnDragDone(Event::consume);


                treeCell.setOnDragExited(dragEvent -> {
                    if (treeCell.getItem() != null) {
                        treeCell.setGraphic(treeCell.getItem().buildIcon());
                    }
                    dragEvent.consume();
                });


                treeCell.setOnDragOver(dragEvent -> {
                    if (dragObject != null && treeCell.getItem() != null) {
                        if (!dragObject.getValue().isMovableIn(treeCell.getItem(), (Content) summary.getRoot().getValue())) {
                            treeCell.setGraphic(IconFactory.createDeleteIcon());
                        } else {
                            treeCell.setGraphic(IconFactory.createArrowDownIcon());
                            dragEvent.acceptTransferModes(TransferMode.MOVE);
                        }
                    }
                    dragEvent.consume();
                });

                treeCell.setOnDragDropped(dragEvent -> {
                    FunctionTreeFactory.moveToContainer(treeCell.getTreeItem(), dragObject);
                    dragEvent.consume();
                    treeCell.saveManifestJson();
                    dragObject = null;
                });

                return treeCell;
            }
        });
        MainApp.getZdsutils().setGalleryId(null);
        mainApp.getMenuController().activateButtonForOpenContent();
        if (filePath != null && !"null".equals(filePath)) {
            MainApp.getConfig().addActionProject(filePath);
            refreshRecentProject();
        }
        log.info("Contenu stocké dans " + filePath + " ouvert");
    }

    private void replaceAction(String defaultString, int defaultOffsetCaret, String beforeString, String afterString) {
        if (currentSourceText.getSelectedText().isEmpty()) {
            currentSourceText.replaceText(currentSourceText.getSelection(), defaultString);
            currentSourceText.positionCaret(currentSourceText.getCaretPosition() - defaultOffsetCaret);
        } else {
            currentSourceText.replaceText(currentSourceText.getSelection(), beforeString + currentSourceText.getSelectedText() + afterString);
        }

        Platform.runLater(currentSourceText::requestFocus);
    }

    /*
     * Editor Toolbar Action
     */

    @FXML
    public void handleSaveButtonAction() {
        getCurrentExtract().setMarkdown(currentSourceText.getText());
        getCurrentExtract().save();

        setCurrentSaved(true);
        currentSourceText.requestFocus();
    }

    @FXML
    private void handleBoldButtonAction(ActionEvent event) {
        replaceAction("****", 2, "**", "**");
    }

    @FXML
    private void handleItalicButtonAction(ActionEvent event) {
        replaceAction("**", 1, "*", "*");
    }

    @FXML
    private void handleBarredButtonAction(ActionEvent event) {
        replaceAction("~~~~", 2, "~~", "~~");
    }

    @FXML
    private void handleTouchButtonAction(ActionEvent event) {
        replaceAction("||||", 2, "||", "||");
    }

    @FXML
    private void handleExpButtonAction(ActionEvent event) {
        replaceAction("^^", 1, "^", "^");
    }

    @FXML
    private void handleIndButtonAction(ActionEvent event) {
        replaceAction("~~", 1, "~", "~");
    }

    @FXML
    private void handleCenterButtonAction(ActionEvent event) {
        replaceAction("\n->  <-", 3, "\n-> ", " <-");
    }

    @FXML
    private void handleRightButtonAction(ActionEvent event) {
        replaceAction("\n->  ->", 3, "\n-> ", " ->\n");
    }

    @FXML
    private void handleImgButtonAction(ActionEvent event) {
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/ImageInput.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.getBundle().getString("ui.dialog.upload.img.title"));

        ImageInputDialog imageController = loader.getController();
        imageController.setSourceText(currentSourceText, MainApp.getZdsutils(), getMainApp().getMenuController(), getMainApp().getContent());
        imageController.setStage(dialogStage);

        dialogStage.show();
    }

    @FXML
    private void handleBulletButtonAction(ActionEvent event) {
        if (currentSourceText.getSelectedText().isEmpty()) {
            currentSourceText.replaceText(currentSourceText.getSelection(), "- ");
        } else {
            StringBuilder sb = new StringBuilder();
            String[] lines = currentSourceText.getSelectedText().split("\n");
            for (String line : lines) {
                sb.append("- ").append(line).append("\n");
            }

            currentSourceText.replaceText(currentSourceText.getSelection(), sb.toString());
        }

        currentSourceText.requestFocus();
    }

    @FXML
    private void handleNumberedButtonAction(ActionEvent event) {
        if (currentSourceText.getSelectedText().isEmpty()) {
            currentSourceText.replaceText(currentSourceText.getSelection(), "1. ");
        } else {
            StringBuilder sb = new StringBuilder();
            String[] lines = currentSourceText.getSelectedText().split("\n");
            int i = 1;
            for (String line : lines) {
                sb.append(i).append(". ").append(line).append("\n");
                i++;
            }

            currentSourceText.replaceText(currentSourceText.getSelection(), sb.toString());
        }

        currentSourceText.requestFocus();
    }

    @FXML
    private void handleHeaderButtonAction(ActionEvent event) {
        String pref = "#";
        String selection = currentSourceText.getSelectedText();
        if(!selection.strip().startsWith("#")) {
            pref+=" ";
        }
        currentSourceText.replaceText(currentSourceText.getSelection(), pref + selection);
        currentSourceText.requestFocus();
    }

    @FXML
    private void handleQuoteButtonAction(ActionEvent event) {
        replaceAction("> ", 0, "> ", "\n\n");
    }

    @FXML
    private void handleBlocButtonAction(ActionEvent event) {
        StringBuilder text = new StringBuilder();
        String[] lines = currentSourceText.getSelectedText().split("\n");
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
        result.ifPresent(s -> currentSourceText.replaceText(currentSourceText.getSelection(), "\n[[" + s + "]]\n" + text.toString()));

        currentSourceText.requestFocus();
    }

    @FXML
    private void handleTableButtonAction(ActionEvent event) throws IOException {
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
            currentSourceText.replaceText(currentSourceText.getSelection(), "\n\n" + tablestring + "\n\n");
            currentSourceText.requestFocus();
        });
    }

    @FXML
    private void handleLinkButtonAction(ActionEvent event) {
        String link = currentSourceText.getSelectedText();

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

        result.ifPresent(tLinkTLabel -> currentSourceText.replaceText(currentSourceText.getSelection(),
                "[" + tLinkTLabel.getValue() + "](" + tLinkTLabel.getKey() + ")"));

        currentSourceText.requestFocus();
    }

    @FXML
    private void handleCodeButtonAction(ActionEvent event) {
        String code = currentSourceText.getSelectedText();
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

        result.ifPresent(tLangageTCode -> currentSourceText.replaceText(currentSourceText.getSelection(),
                "\n```" + tLangageTCode.getKey() + "\n" + tLangageTCode.getValue() + "\n```\n"));

        currentSourceText.requestFocus();
    }

    /*
     * Render Toolbar Action
     */

    public void addTreeSummary() {
        getSplitPane().getItems().add(0, treePane);
        getSplitPane().setDividerPositions(0.2);
        SplitPane.setResizableWithParent(treePane, Boolean.FALSE);
    }

    @FXML
    private void handleFullScreeenButtonAction(ActionEvent event) {
        if (getSplitPane().getItems().size() > 1) {
            getSplitPane().getItems().remove(0);
        } else {
            addTreeSummary();
        }
    }

    @FXML
    private void handleExternalButtonAction(ActionEvent event) {
        SplitPane selectedSplitPane = (SplitPane) editorList.getSelectionModel().getSelectedItem().getContent();
        selectedSplitPane.getItems().remove(1);

        Stage stage = new CustomStage(Configuration.getBundle().getString("ui.window.externalrender.title"));
        AnchorPane pane = new AnchorPane(currentRenderView);
        AnchorPane.setTopAnchor(currentRenderView, 0.0);
        AnchorPane.setLeftAnchor(currentRenderView, 0.0);
        AnchorPane.setBottomAnchor(currentRenderView, 0.0);
        AnchorPane.setRightAnchor(currentRenderView, 0.0);
        pane.setPrefWidth(600);
        pane.setPrefHeight(500);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

        stage.setOnCloseRequest(e -> {
            currentBoxRender.setCenter(currentRenderView);
            selectedSplitPane.getItems().add(1, currentBoxRender);
            selectedSplitPane.setDividerPositions(0.5);
        });
    }

    @FXML
    private void handleUnbreakableAction() {
        currentSourceText.replaceText(currentSourceText.getSelection(), currentSourceText.getSelectedText() + "\u00a0");
        currentSourceText.requestFocus();
    }

    public void handleGoToLineAction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(Configuration.getBundle().getString("ui.editor.dialog.goto.title"));
        dialog.setHeaderText(Configuration.getBundle().getString("ui.editor.dialog.goto.header"));
        dialog.setContentText(Configuration.getBundle().getString("ui.editor.dialog.goto.text"));
        dialog.initOwner(MainApp.getPrimaryStage());
        Optional<String> result = dialog.showAndWait();

        String[] splitText = currentSourceText.getText().split("\n");

        result.ifPresent(line -> {
            int position = 0;
            for(int i=0; i<Math.min(splitText.length, Integer.parseInt(line)) - 1; i++) {
                position += splitText[i].length() + 1;
            }
            currentSourceText.positionCaret(position);
        });
    }

    @FXML
    private void handleFindReplaceDialog() {
        FunctionTreeFactory.openFindReplaceDialog(currentSourceText);
    }

    public void initKeyMapping(TextArea sourceText) {
        Platform.runLater(() -> {
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.S, SHORTCUT_DOWN), e -> handleSaveButtonAction())));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.G, SHORTCUT_DOWN), e -> handleBoldButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.I, SHORTCUT_DOWN), e -> handleItalicButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.B, SHORTCUT_DOWN), e -> handleBarredButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.K, SHORTCUT_DOWN), e -> handleTouchButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.PLUS, SHORTCUT_DOWN), e -> handleExpButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.EQUALS, SHORTCUT_DOWN), e -> handleIndButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.E, SHORTCUT_DOWN), e -> handleCenterButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.D, SHIFT_DOWN, SHORTCUT_DOWN), e -> handleRightButtonAction(null))));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.SPACE, SHORTCUT_DOWN), e -> handleUnbreakableAction())));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.L, SHORTCUT_DOWN), e -> handleGoToLineAction())));
            Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.F, SHORTCUT_DOWN), e -> handleFindReplaceDialog())));
            if (FunctionTreeFactory.isMacOs()) {
                Nodes.addInputMap(sourceText, sequence(consume(keyPressed(KeyCode.Q, SHORTCUT_DOWN), e -> currentSourceText.selectAll())));
            }
        });
    }
}
