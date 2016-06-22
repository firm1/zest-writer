package com.zestedesavoir.zestwriter.view;

import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.*;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.ContentNode;
import com.zestedesavoir.zestwriter.model.Textual;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class MdTextController {
    private MainApp mainApp;
    private PythonInterpreter pyconsole;
    private final Logger logger;

    @FXML private VBox contentBox;
    @FXML private TabPane EditorList;
    @FXML private TreeView<ContentNode> Summary;
    @FXML private SplitPane splitPane;
    @FXML public AnchorPane treePane;
    @FXML private Tab Home;

    @FXML private void initialize() {
        loadConsolePython();
        loadFonts();
    }

    public TabPane getEditorList() {
        return EditorList;
    }

    public void loadConsolePython() {
        new Thread(() -> {
            pyconsole = new PythonInterpreter();
            pyconsole.exec("from markdown import Markdown");
            pyconsole.exec("from markdown.extensions.zds import ZdsExtension");
            pyconsole.exec("from smileys_definition import smileys");
            logger.info("PYTHON STARTED");
        }).start();
    }

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

    public MdTextController() {
        super();
        logger = LoggerFactory.getLogger(MdTextController.class);
    }

    public PythonInterpreter getPyconsole() {
        return pyconsole;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public TreeView<ContentNode> getSummary() {
        return Summary;
    }

    public void setPyconsole(PythonInterpreter pyconsole) {
        this.pyconsole = pyconsole;
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        mainApp.getContents().addListener((ListChangeListener<Content>) change -> {
            if(FunctionTreeFactory.clearContent(mainApp.getExtracts(), EditorList)) {
                for(Content content:mainApp.getContents()) {
                    openContent(content);
                }
            }
        });

        mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.TAB, SHORTCUT_DOWN), () -> switchTabTo(true));
        mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.TAB, SHORTCUT_DOWN, SHIFT_DOWN), () -> switchTabTo(false));
        if(FunctionTreeFactory.isMacOs()) {
            mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Z, SHORTCUT_DOWN), () -> closeCurrentTab());
        } else {
            mainApp.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.W, SHORTCUT_DOWN), () -> closeCurrentTab());
        }

        refreshRecentProject();
    }

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
        int row=0, col=0, size=2;
        for(String recentFilePath:mainApp.getConfig().getActions()) {
            File manifest = new File(recentFilePath + File.separator + "manifest.json");
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
                link.setOnAction(t -> {
                    mainApp.getContents().clear();
                    FunctionTreeFactory.clearContent(mainApp.getExtracts(), mainApp.getIndex().getEditorList());
                    mainApp.getContents().add(c);
                });
                bPane.setTop(link);
                bPane.setBottom(description);
                bPane.setLeft(type);
                gPane.add(bPane, col%size, row);
            } catch (IOException e) {
                logger.error("Impossible de lire le contenu répertorié dans : "+recentFilePath, e);
            }
            col++;
            if(col%size==0) row++;
        }
        contentBox.getChildren().add(gPane);
    }

    public void closeCurrentTab() {
        if (EditorList.getTabs().size() > 1) {
            Tab selectedTab = EditorList.getSelectionModel().getSelectedItem();
            Platform.runLater(() -> {
                Event.fireEvent(selectedTab, new Event(Tab.TAB_CLOSE_REQUEST_EVENT));
            });
        }
    }

    public void switchTabTo(boolean right) {
        int size = EditorList.getTabs().size();

        if (size > 0) {
            TabPaneSkin skin = (TabPaneSkin) EditorList.getSkin();
            TabPaneBehavior tabPaneBehavior = skin.getBehavior();

            int selectedIndex = EditorList.getSelectionModel().getSelectedIndex();

            if (right) {
                if (selectedIndex < size -1) {
                    tabPaneBehavior.selectNextTab();
                } else {
                    tabPaneBehavior.selectTab(EditorList.getTabs().get(0));
                }
            } else {
                if (selectedIndex > 0) {
                    tabPaneBehavior.selectPreviousTab();
                } else {
                    tabPaneBehavior.selectTab(EditorList.getTabs().get(size - 1));
                }
            }
        }
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
        EditorList.getTabs().add(tab);
        EditorList.getSelectionModel().select(tab);

        MdConvertController controller = loader.getController();
        controller.setMdBox(this, extract, tab);

        tab.setOnCloseRequest(t -> {
            if(!controller.isSaved()) {
                Alert alert = new CustomAlert(AlertType.CONFIRMATION);
                IconFactory.addAlertLogo(alert);
                alert.setTitle(Configuration.bundle.getString("ui.alert.tab.close.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.alert.tab.close.header"));
                alert.setContentText(Configuration.bundle.getString("ui.alert.tab.close.text"));

                ButtonType buttonTypeYes = new ButtonType(Configuration.bundle.getString("ui.yes"));
                ButtonType buttonTypeNo = new ButtonType(Configuration.bundle.getString("ui.no"));
                ButtonType buttonTypeCancel = new ButtonType(Configuration.bundle.getString("ui.cancel"), ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(400, 200);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() != buttonTypeCancel) {
                        if (result.get() == buttonTypeYes) {
                            controller.HandleSaveButtonAction(null);
                        }
                        Event.fireEvent(tab, new Event(Tab.CLOSED_EVENT));
                    }
                }
            } else {
                Event.fireEvent(tab, new Event(Tab.CLOSED_EVENT));
            }
        });

        tab.setOnClosed(t -> {
            EditorList.getTabs().remove(tab);
            mainApp.getExtracts().remove(extract);
            t.consume();
        });

        mainApp.getExtracts().put(extract, tab);
        logger.info("Nouvel onglet crée pour "+extract.getTitle());
    }

    public MdTextController getThis() {
        return this;
    }

    public void openContent(Content content) {
    	String filePath = content.getBasePath();
        logger.debug("Tentative d'ouverture du contenu stocké dans "+filePath);

        // load content informations
        mainApp.getZdsutils().setLocalSlug(content.getSlug());
        TreeItem<ContentNode> rootItem = new TreeItem<>(content);
        rootItem.setExpanded(true);
        Summary.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        rootItem = FunctionTreeFactory.buildChild(rootItem);
        Summary.setRoot(rootItem);
        Summary.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItem<ContentNode> item = Summary.getSelectionModel().getSelectedItem();

                if(item.getValue() instanceof Textual) {
                    if (item.getValue().getFilePath() != null) {
                        if (!mainApp.getExtracts().containsKey(item.getValue())) {
                            try {
                                createTabExtract((Textual)item.getValue());
                            } catch (IOException e) {
                                logger.error(e.getMessage(), e);
                            }
                        } else {
                            TabPaneSkin skin = (TabPaneSkin) EditorList.getSkin();
                            TabPaneBehavior tabPaneBehavior = skin.getBehavior();
                            tabPaneBehavior.selectTab(mainApp.getExtracts().get(item.getValue()));
                        }
                    }
                }
            }
        });
        Summary.setCellFactory(new Callback<TreeView<ContentNode>, TreeCell<ContentNode>>() {
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
                        if (!dragObject.getValue().isMoveableIn(treeCell.getItem(), ((Content)Summary.getRoot().getValue())))
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
        mainApp.getZdsutils().setGalleryId(null);
        mainApp.getMenuController().activateButtonForOpenContent();
        if(filePath != null && !filePath.equals("null") ) {
            mainApp.getConfig().addActionProject(filePath);
            refreshRecentProject();
        }
        logger.info("Contenu stocké dans "+filePath+" ouvert");
    }

}
