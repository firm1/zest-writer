package com.zestedesavoir.zestwriter.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.ContentNode;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

public class MdTextController {
    private boolean pythonStarted=false;
    private final Logger logger;
    @FXML public AnchorPane treePane;
    private MainApp mainApp;
    private PythonInterpreter pyconsole;
    @FXML private VBox contentBox;
    @FXML private TabPane editorList;
    @FXML private Tab home;
    @FXML private TreeView<ContentNode> summary;
    @FXML private SplitPane splitPane;

    public MdTextController() {
        super();
        logger = LoggerFactory.getLogger(MdTextController.class);
    }

    @FXML private void initialize() {
        if(MainApp.getConfig().isEditorRenderView())
            loadConsolePython();

        loadFonts();
        editorList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mainApp.getMenuController().setIsOnReadingTab(! (newValue.getContent() instanceof SplitPane))
        );
        home.setOnSelectionChanged(t -> mainApp.getMenuController().gethBottomBox().getChildren().clear());
    }

    public TabPane getEditorList() {
        return editorList;
    }

    public boolean isPythonStarted() {
        return pythonStarted;
    }

    public void setPythonStarted(boolean pythonStarted) {
        this.pythonStarted = pythonStarted;
    }

    public void loadConsolePython() {
        new Thread(() -> {
            pyconsole = new PythonInterpreter();
            pyconsole.exec("from markdown import Markdown");
            pyconsole.exec("from markdown.extensions.zds import ZdsExtension");
            pyconsole.exec("from smileys_definition import smileys");
            pyconsole.exec("mk_instance = Markdown(extensions=(ZdsExtension(inline=False, emoticons=smileys, js_support=False, ping_url=None),),safe_mode = 'escape', enable_attributes = False, tab_length = 4, output_format = 'html5', smart_emphasis = True, lazy_ol = True)");
            logger.info("PYTHON STARTED");
            setPythonStarted(true);
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
        }).start();
    }

    public PythonInterpreter getPyconsole() {
        return pyconsole;
    }

    public void setPyconsole(PythonInterpreter pyconsole) {
        this.pyconsole = pyconsole;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public TreeView<ContentNode> getSummary() {
        return summary;
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        mainApp.contentProperty().addListener(change -> {
            logger.info("Détection du changement de contenu");
            FunctionTreeFactory.clearContent(mainApp.getExtracts(), editorList, () -> {
                logger.info("Début de la fonction à executer après le clear");
                summary.setRoot(null);
                if (mainApp.contentProperty().isNotNull().get()) {
                    openContent(mainApp.getContent());
                }
                return null;
            });

        });

        mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.TAB, SHORTCUT_DOWN), () -> switchTabTo(true));
        mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.TAB, SHORTCUT_DOWN, SHIFT_DOWN), () -> switchTabTo(false));
        if(FunctionTreeFactory.isMacOs()) {
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
        gPane.getColumnConstraints().addAll(col1,col2);
        gPane.setHgap(10);
        gPane.setVgap(10);
        gPane.setPadding(new Insets(10, 10, 10, 10));
        int row=0;
        int col=0;
        int size=2;
        for(String recentFilePath: MainApp.getConfig().getActions()) {
            File manifest = new File(recentFilePath + File.separator + "manifest.json");
            if(manifest.exists()) {
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
                    logger.error("Impossible de lire le contenu répertorié dans : " + recentFilePath, e);
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
     * @param right if true, then switch on right side, else switch on left side
     */
    public void switchTabTo(boolean right) {
        int size = editorList.getTabs().size();

        if (size > 0) {
            TabPaneSkin skin = (TabPaneSkin) editorList.getSkin();
            TabPaneBehavior tabPaneBehavior = skin.getBehavior();

            int selectedIndex = editorList.getSelectionModel().getSelectedIndex();

            if (right) {
                if (selectedIndex < size -1) {
                    tabPaneBehavior.selectNextTab();
                } else {
                    tabPaneBehavior.selectTab(editorList.getTabs().get(0));
                }
            } else {
                if (selectedIndex > 0) {
                    tabPaneBehavior.selectPreviousTab();
                } else {
                    tabPaneBehavior.selectTab(editorList.getTabs().get(size - 1));
                }
            }
        }
    }

    /**
     * Select any item on Tree
     * @param item from which one wants to search
     * @param textual textual open on tab which one wants to select
     * @return TreeItem what you want to select
     */
    public TreeItem<ContentNode> selectItemOnTree(TreeItem<ContentNode> item, Textual textual) {
        for(TreeItem<ContentNode> node: item.getChildren()) {
            if(node.getValue().getFilePath().equals(textual.getFilePath())) {
                return node;
            } else {
                TreeItem<ContentNode> it = selectItemOnTree(node, textual);
                if(it != null) {
                    return it;
                }
            }
        }
        return null;
    }

    public void createTabExtract(Textual extract) throws IOException {
        logger.debug("Tentative de création d'un nouvel onglet pour "+extract.getTitle());
        extract.loadMarkdown();
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/Editor.fxml"));
        SplitPane writer = loader.load();
        logger.trace("Fichier Editor.fxml chargé");

        Tab tab = new Tab();
        tab.setText(extract.getTitle());
        tab.setContent(writer);
        editorList.getTabs().add(tab);
        editorList.getSelectionModel().select(tab);

        MdConvertController controllerConvert = loader.getController();
        controllerConvert.setMdBox(this, extract, tab);

        tab.setOnCloseRequest(t -> {
            if(!controllerConvert.isSaved()) {
                Alert alert = new CustomAlert(AlertType.CONFIRMATION);
                alert.setTitle(Configuration.getBundle().getString("ui.alert.tab.close.title"));
                alert.setHeaderText(Configuration.getBundle().getString("ui.alert.tab.close.header")+" : "+tab.getText().substring(1));
                alert.setContentText(Configuration.getBundle().getString("ui.alert.tab.close.text"));

                ButtonType buttonTypeYes = new ButtonType(Configuration.getBundle().getString("ui.yes"));
                ButtonType buttonTypeNo = new ButtonType(Configuration.getBundle().getString("ui.no"));
                ButtonType buttonTypeCancel = new ButtonType(Configuration.getBundle().getString("ui.cancel"), ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(400, 200);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() != buttonTypeCancel) {
                        if (result.get() == buttonTypeYes) {
                            controllerConvert.handleSaveButtonAction(null);
                        }
                        Event.fireEvent(tab, new Event(Tab.CLOSED_EVENT));
                    } else {
                        t.consume();
                    }
                }
            } else {
                Event.fireEvent(tab, new Event(Tab.CLOSED_EVENT));
            }
        });

        tab.setOnClosed(t -> {
            editorList.getTabs().remove(tab);
            mainApp.getExtracts().remove(extract);
            t.consume();
            if (getSplitPane().getItems().size() <= 1 && editorList.getTabs().size() == 1) {
                controllerConvert.addTreeSummary();
            }
        });

        summary.getSelectionModel().select(selectItemOnTree(summary.getRoot(), extract));

        mainApp.getExtracts().put(extract, tab);
        logger.info("Nouvel onglet crée pour "+extract.getTitle());
    }

    public MdTextController getThis() {
        return this;
    }

    public void openContent(Content content) {
    	String filePath = content.getBasePath();
        mainApp.getExtracts().clear();
        logger.debug("Tentative d'ouverture du contenu stocké dans "+filePath);

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

                if(item.getValue() instanceof Textual) {
                    if (item.getValue().getFilePath() != null) {
                        if (!mainApp.getExtracts().containsKey(item.getValue())) {
                            try {
                                createTabExtract((Textual)item.getValue());
                            } catch (IOException e) {
                                logger.error("Problème lors de la création de l'extrait", e);
                            }
                        } else {
                            TabPaneSkin skin = (TabPaneSkin) editorList.getSkin();
                            TabPaneBehavior tabPaneBehavior = skin.getBehavior();
                            tabPaneBehavior.selectTab(mainApp.getExtracts().get(item.getValue()));
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
                    if(treeCell.getItem() != null) {
                        treeCell.setGraphic(treeCell.getItem().buildIcon());
                    }
                    dragEvent.consume();
                });


                treeCell.setOnDragOver(dragEvent -> {
                    if(dragObject != null && treeCell.getItem() != null) {
                        if (!dragObject.getValue().isMovableIn(treeCell.getItem(), (Content) summary.getRoot().getValue()))
                        {
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
        if(filePath != null && !"null".equals(filePath)) {
            MainApp.getConfig().addActionProject(filePath);
            refreshRecentProject();
        }
        logger.info("Contenu stocké dans "+filePath+" ouvert");
    }

}
