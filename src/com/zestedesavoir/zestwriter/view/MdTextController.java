package com.zestedesavoir.zestwriter.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.codehaus.jackson.map.ObjectMapper;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import com.zestedesavoir.zestwriter.view.com.MdTreeCell;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class MdTextController {
    private MainApp mainApp;
    private PythonInterpreter pyconsole;

    @FXML
    private TabPane EditorList;

    @FXML
    private TreeView<ExtractFile> Summary;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Tab Home;

    private Map jsonData;
    private String baseFilePath;
    private final Logger logger;



    @FXML
    private void initialize() {
        loadConsolePython();
        loadFonts();

    }

    public void loadConsolePython() {
        new Thread(() -> {
            pyconsole = new PythonInterpreter();
            pyconsole.exec("from markdown import Markdown");
            pyconsole.exec("from markdown.extensions.zds import ZdsExtension");
            pyconsole.exec("from smileys_definition import smileys");
            System.out.println("PYTHON START");
        }).start();
    }

    public void loadFonts() {
        new Thread(new Runnable() {
            public void run() {
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Regular.ttf").toExternalForm(), 10);
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Black.ttf").toExternalForm(), 10);
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Bold.ttf").toExternalForm(), 10);
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-BoldItalic.ttf").toExternalForm(), 10);
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-HeavyItalic.ttf").toExternalForm(), 10);
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Italic.ttf").toExternalForm(), 10);
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Light.ttf").toExternalForm(), 10);
                Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-LightItalic.ttf").toExternalForm(), 10);

                Font.loadFont(this.getClass().getResource("static/fonts/FiraMono-Regular.ttf").toExternalForm(), 10);
            }
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

    public TreeView<ExtractFile> getSummary() {
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

        mainApp.getContents().addListener((MapChangeListener<String, String>) change -> {
            try {
                if (mainApp.getContents().containsKey("dir")) {
                    openContent(mainApp.getContents().get("dir"));
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        });

        mainApp.getScene().addEventFilter(KeyEvent.KEY_PRESSED, t -> {
            if (t.getCode().equals(KeyCode.TAB) && t.isControlDown()) {
                int size = EditorList.getTabs().size();

                if (size > 0) {
                    TabPaneSkin skin = (TabPaneSkin) EditorList.getSkin();
                    TabPaneBehavior tabPaneBehavior = (TabPaneBehavior) skin.getBehavior();

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
                    Event closeRequestEvent = new Event(Tab.TAB_CLOSE_REQUEST_EVENT);
                    Event.fireEvent(selectedTab, closeRequestEvent);
                    Event closedEvent = new Event(Tab.CLOSED_EVENT);
                    Event.fireEvent(selectedTab, closedEvent);
                    EditorList.getTabs().remove(selectedTab);
                }
            }
        });

    }

    public void createTabExtract(ExtractFile extract) throws IOException {

        logger.debug("Tentative de création d'un nouvel onglet pour "+extract.getTitle().getValue());
        extract.loadMarkdown();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/Editor.fxml"));
        SplitPane writer = loader.load();
        logger.trace("Fichier Editor.fxml chargé");

        Tab tab = new Tab();
        tab.setText(extract.getTitle().getValue());
        tab.setContent(writer);
        EditorList.getTabs().add(tab);
        EditorList.getSelectionModel().select(tab);

        MdConvertController controller = loader.getController();
        controller.setMdBox(this, extract, tab);

        tab.setOnCloseRequest(t -> {
            if(!controller.isSaved()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
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
                if(result.get() != buttonTypeCancel) {
                    if (result.get() == buttonTypeYes){
                        controller.HandleSaveButtonAction(null);
                    }
                } else {
                    t.consume();
                }
            }
        });

        tab.setOnClosed(t -> {
            mainApp.getExtracts().remove(extract);
            t.consume();
        });

        mainApp.getExtracts().put(extract, tab);
        logger.info("Nouvel onglet crée pour "+extract.getTitle().getValue());
    }



    public TreeItem<ExtractFile> addChild(TreeItem<ExtractFile> node, Map container, String path) {
        if (container.get("object").equals("container")) {
            node.getValue().setConclusion(container.get("conclusion").toString());
            node.getValue().setIntroduction(container.get("introduction").toString());
            if (container.containsKey("introduction")) {
                TreeItem<ExtractFile> itemIntro = new TreeItem<>(
                        new ExtractFile("Introduction",
                                container.get("slug").toString(),
                                baseFilePath,
                                container.get("introduction").toString(),
                                null));
                node.getChildren().add(itemIntro);
            }
            if (container.containsKey("children")) {
                List children = (ArrayList) container.get("children");
                String intro_path = container.get("introduction").toString();
                for (Object child : children) {
                    Map childMap = (Map) child;
                    TreeItem<ExtractFile> item = new TreeItem<>(
                            new ExtractFile(
                                    childMap.get("title").toString(),
                                    childMap.get("slug").toString(),
                                    baseFilePath,
                                    "",
                                    ""));
                    node.getChildren().add(addChild(item, childMap, path));
                }
            }
            if (container.containsKey("conclusion")) {
                TreeItem<ExtractFile> itemConclu = new TreeItem<>(
                        new ExtractFile("Conclusion",
                                container.get("slug").toString(),
                                baseFilePath,
                                container.get("conclusion").toString(),
                                null));
                node.getChildren().add(itemConclu);
            }
            return node;
        } else {
            if (container.get("object").equals("extract")) {
                return new TreeItem<>(
                        new ExtractFile(
                                container.get("title").toString(),
                                container.get("slug").toString(),
                                baseFilePath,
                                container.get("text").toString()));
            }
        }
        return null;

    }

    public void openContent(String filePath) throws IOException {

    	for(Entry<ExtractFile, Tab> entry:mainApp.getExtracts().entrySet()) {
    		Platform.runLater(() -> {
    			Event.fireEvent(entry.getValue(), new Event(Tab.TAB_CLOSE_REQUEST_EVENT));
	            Event.fireEvent(entry.getValue(), new Event(Tab.CLOSED_EVENT));
	            EditorList.getTabs().remove(entry.getValue());
    		});
    	}
    	mainApp.getExtracts().clear();
        logger.debug("Tentative d'ouverture du contenu stocké dans "+filePath);
        this.baseFilePath = filePath;
        ObjectMapper mapper = new ObjectMapper();
        jsonData = mapper.readValue(new File(filePath + File.separator + "manifest.json"), Map.class);

        // load content informations
        String contentTitle = jsonData.get("title").toString();
        String contentSlug = jsonData.get("slug").toString();
        mainApp.getZdsutils().setLocalSlug(contentSlug);
        mainApp.getZdsutils().setLocalType(jsonData.get("type").toString().toLowerCase());
        TreeItem<ExtractFile> rootItem = new TreeItem<>(
                new ExtractFile(
                        contentTitle,
                        contentSlug,
                        baseFilePath,
                        jsonData.get("version").toString(),
                        jsonData.get("description").toString(),
                        jsonData.get("type").toString(),
                        jsonData.get("licence").toString(),
                        jsonData.get("introduction").toString(),
                        jsonData.get("conclusion").toString()));
        rootItem.setExpanded(true);
        Summary.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        rootItem = addChild(rootItem, jsonData, filePath);
        Summary.setRoot(rootItem);
        Summary.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItem<ExtractFile> item = Summary.getSelectionModel().getSelectedItem();

                if(!item.getValue().isContainer()) {
                    if (item.getValue().getFilePath() != null) {
                        if (!mainApp.getExtracts().containsKey(item.getValue())) {
                            try {
                                createTabExtract(item.getValue());
                            } catch (IOException e) {
                                logger.error("", e);
                            }
                        } else {
                            TabPaneSkin skin = (TabPaneSkin) EditorList.getSkin();
                            TabPaneBehavior tabPaneBehavior = (TabPaneBehavior) skin.getBehavior();
                            tabPaneBehavior.selectTab(mainApp.getExtracts().get(item.getValue()));
                        }
                    }
                }
            }
        });

        Summary.setCellFactory(new Callback<TreeView<ExtractFile>, TreeCell<ExtractFile>>() {

            @Override
            public TreeCell<ExtractFile> call(TreeView<ExtractFile> extractTreeView) {
            	MdTreeCell treeCell = new MdTreeCell(Summary, baseFilePath, mapper);

                treeCell.setOnDragDetected(mouseEvent -> {
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
                    if (treeCell.getItem().isContainer()) {
                        treeCell.setGraphic(IconFactory.createFolderIcon());
                    } else {
                        treeCell.setGraphic(IconFactory.createFileIcon());
                    }
                    dragEvent.consume();
                });


                treeCell.setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        String valueToMove = dragEvent.getDragboard().getString();
                        TreeItem<ExtractFile> itemToMove = search(Summary.getRoot(), valueToMove);
                        TreeItem<ExtractFile> newParent = treeCell.getTreeItem();
                        if (!itemToMove.getValue().isMoveableIn(
                                treeCell.getItem(),
                                FunctionTreeFactory.getDescendantContainerCount(itemToMove) + FunctionTreeFactory.getAncestorContainerCount(newParent),
                                FunctionTreeFactory.getDescendantContainerCount(newParent),
                                FunctionTreeFactory.getDescendantContainerCount(newParent.getParent()))
                           )
                        {
                            treeCell.setGraphic(IconFactory.createDeleteIcon());
                        } else {
                            dragEvent.acceptTransferModes(TransferMode.MOVE);
                        }
                        dragEvent.consume();
                    }
                });

                treeCell.setOnDragDropped(dragEvent -> {
                    String valueToMove = dragEvent.getDragboard().getString();
                    TreeItem<ExtractFile> itemToMove = search(Summary.getRoot(), valueToMove);
                    TreeItem<ExtractFile> newParent = treeCell.getTreeItem();
                    // Remove from former parent.
                    itemToMove.getParent().getChildren().remove(itemToMove);

                    if (newParent.getValue().isContainer()) {
                        int position = newParent.getChildren().size();
                        // Add to new parent.
                        newParent.getChildren().add(position - 1, itemToMove);
                    } else {
                        //if(oldParent.equals(newParent.getParent())) {
                        int position = newParent.getParent().getChildren().indexOf(newParent);
                        // Add after new item.
                        newParent.getParent().getChildren().add(position + 1, itemToMove);
                        //}
                    }

                    newParent.setExpanded(true);
                    dragEvent.consume();

                    // save json file
                    treeCell.saveManifestJson();
                });

                return treeCell;
            }

            private TreeItem<ExtractFile> search(final TreeItem<ExtractFile> currentNode, final String valueToSearch) {
                TreeItem<ExtractFile> result = null;
                if (currentNode.toString().equals(valueToSearch)) {
                    result = currentNode;
                } else if (!currentNode.isLeaf()) {
                    for (TreeItem<ExtractFile> child : currentNode.getChildren()) {
                        result = search(child, valueToSearch);
                        if (result != null) {
                            break;
                        }
                    }
                }
                return result;
            }
        });
        logger.info("Contenu stocké dans "+filePath+" ouvert");
    }
}
