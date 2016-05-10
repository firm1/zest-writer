package com.zestedesavoir.zestwriter.view;

import java.io.IOException;
import java.util.Optional;

import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.ContentNode;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import com.zestedesavoir.zestwriter.view.com.MdTreeCell;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class MdTextController {
    private MainApp mainApp;
    private Configuration config;
    private PythonInterpreter pyconsole;
    private final Logger logger;

    @FXML private Hyperlink recentFileLink1;
    @FXML private Hyperlink recentFileLink2;
    @FXML private Hyperlink recentFileLink3;
    @FXML private Hyperlink recentFileLink4;
    @FXML private Hyperlink recentFileLink5;

    @FXML private TabPane EditorList;
    @FXML private TreeView<ContentNode> Summary;
    @FXML private SplitPane splitPane;
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
        this.config = mainApp.getConfig();

        mainApp.getContents().addListener((ListChangeListener<Content>) change -> {
            if(FunctionTreeFactory.clearContent(mainApp.getExtracts(), EditorList)) {
                for(Content content:mainApp.getContents()) {
                    openContent(content);
                }
            }
        });

        mainApp.getScene().addEventFilter(KeyEvent.KEY_PRESSED, t -> {
            if (t.getCode().equals(KeyCode.TAB) && t.isControlDown()) {
                int size = EditorList.getTabs().size();

                if (size > 0) {
                    TabPaneSkin skin = (TabPaneSkin) EditorList.getSkin();
                    TabPaneBehavior tabPaneBehavior = skin.getBehavior();

                    int selectedIndex = EditorList.getSelectionModel().getSelectedIndex();

                    if (!t.isShiftDown()) {
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

                    t.consume();
                }

            } else if(t.getCode().equals(KeyCode.W) && t.isControlDown()) {
                if (EditorList.getTabs().size() > 1) {
                    Tab selectedTab = EditorList.getSelectionModel().getSelectedItem();
                    Platform.runLater(() -> {
                        Event.fireEvent(selectedTab, new Event(Tab.TAB_CLOSE_REQUEST_EVENT));
                    });
                }
            }
        });

    }

    public void createTabExtract(Textual extract) throws IOException {
        logger.debug("Tentative de création d'un nouvel onglet pour "+extract.getTitle());
        extract.loadMarkdown();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("fxml/Editor.fxml"));
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
                Alert alert = new Alert(AlertType.CONFIRMATION);
                IconFactory.addAlertLogo(alert);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Confirmation de fermeture");
                alert.setContentText("Vous avez modifié cet extrait. Voulez-vous enregistrer les modifications ?");

                ButtonType buttonTypeYes = new ButtonType("Oui");
                ButtonType buttonTypeNo = new ButtonType("Non");
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(480, 320);

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
        mainApp.getZdsutils().setLocalType(content.getType().toLowerCase());
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
        logger.info("Contenu stocké dans "+filePath+" ouvert");
    }

}
