package com.zestedesavoir.zestwriter.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.map.ObjectMapper;
import org.python.util.PythonInterpreter;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.model.License;
import com.zestedesavoir.zestwriter.model.TypeContent;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Pair;

@SuppressWarnings("FieldCanBeLocal")
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

    private static ObservableList<TypeContent> typeOptions = FXCollections.observableArrayList(new TypeContent("ARTICLE", "Article"), new TypeContent("TUTORIAL","Tutoriel"));
    private static ObservableList<License> licOptions = FXCollections.observableArrayList(
            new License("CC BY", "Licence CC BY"),
            new License("CC BY-SA", "Licence CC BY-SA"),
            new License("CC BY-ND", "Licence CC BY-ND"),
            new License("CC BY-NC", "Licence CC BY-NC"),
            new License("CC BY-NC-SA", "Licence CC BY-NC-SA"),
            new License("CC BY-NC-ND", "Licence CC BY-NC-ND"),
            new License("Tous droits réservés", "Tout droits réservés"),
            new License("CC 0", "Licence CC 0"));

    @FXML
    private void initialize() {
        loadConsolePython();
        loadFonts();

    }

    public void loadConsolePython() {
        new Thread(() -> {
            pyconsole = new PythonInterpreter();
            pyconsole.exec("from markdown import Markdown");
            System.out.print("1 .. ");
            pyconsole.exec("from markdown.extensions.zds import ZdsExtension");
            System.out.print("2 .. ");
            pyconsole.exec("from smileys_definition import smileys");
            System.out.print("3 .. ");
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
    }

    private MaterialDesignIconView createFolderIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FOLDER_MULTIPLE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#084561");
        return icon;
    }

    public static MaterialDesignIconView createAddFolderIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FOLDER_PLUS);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#084561");
        return icon;
    }

    private MaterialDesignIconView createDeleteIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.CLOSE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#f44336");
        return icon;
    }

    private MaterialDesignIconView createRemoveIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.DELETE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#f44336");
        return icon;
    }

    private MaterialDesignIconView createFileIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FILE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#ef9708");
        return icon;
    }

    private MaterialDesignIconView createEditIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.BORDER_COLOR);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#084561");
        return icon;
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
                // TODO Auto-generated catch block
                e.printStackTrace();
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

        extract.loadMarkdown();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/Editor.fxml"));
        SplitPane writer = loader.load();

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
        });

        mainApp.getExtracts().put(extract, tab);
    }

    public Map<String, Object> getMapFromTreeItem(TreeItem<ExtractFile> node, Map<String, Object> map) {
        if (node.getValue().getOject().getValue() != null) {
            map.put("slug", node.getValue().getSlug().getValue());
            map.put("object", node.getValue().getOject().getValue());
            map.put("title", node.getValue().getTitle().getValue());
            if (node.getValue().isRoot()) {
                map.put("type", node.getValue().getType().getValue());
                map.put("version", Integer.parseInt(node.getValue().getVersion().getValue()));
                map.put("description", node.getValue().getDescription().getValue());
                map.put("licence", node.getValue().getLicence().getValue());
            }
            if (node.getValue().isContainer()) {
                map.put("introduction", node.getValue().getIntroduction().getValue());
                map.put("conclusion", node.getValue().getConclusion().getValue());
            } else {
                map.put("text", node.getValue().getText().getValue());
            }

            List<Map<String, Object>> tabs = new ArrayList<>();
            for (TreeItem<ExtractFile> child : node.getChildren()) {
                Map<String, Object> h = getMapFromTreeItem(child, new HashMap<>());
                if (h != null) {
                    tabs.add(h);
                }
            }

            if (tabs.size() > 0) {
                map.put("children", tabs);
            }
            return map;
        }
        return null;
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
                String buildPath = baseFilePath + File.separator + intro_path.substring(0, intro_path.length() - 15);
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
                                e.printStackTrace();
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
                TreeCell<ExtractFile> treeCell = new TreeCell<ExtractFile>() {
                    private TextField textField;

                    private ContextMenu addMenu = new ContextMenu();

                    public void initContextMenu(ExtractFile item) {

                        MenuItem addMenuItem1 = new MenuItem("Ajouter un extrait");
                        MenuItem addMenuItem2 = new MenuItem("Ajouter un conteneur");
                        MenuItem addMenuItem3 = new MenuItem("Renommer");
                        MenuItem addMenuItem4 = new MenuItem("Supprimer");
                        MenuItem addMenuItem5 = new MenuItem("Editer");
                        addMenuItem1.setGraphic(createFileIcon());
                        addMenuItem2.setGraphic(createAddFolderIcon());
                        addMenuItem3.setGraphic(createEditIcon());
                        addMenuItem4.setGraphic(createRemoveIcon());
                        addMenuItem5.setGraphic(createEditIcon());
                        addMenu.getItems().clear();
                        if (item.canTakeContainer(getAncestorContainerCount(getTreeItem()), getDirectChildCount(getTreeItem()))) {
                            addMenu.getItems().add(addMenuItem2);
                        }
                        if (item.canTakeExtract(getDescendantContainerCount(getTreeItem()))) {
                            addMenu.getItems().add(addMenuItem1);
                        }
                        if (item.isEditable()) {
                            addMenu.getItems().add(addMenuItem3);
                        }
                        if (item.isRoot()) {
                            addMenu.getItems().add(addMenuItem5);
                        }
                        if (item.canDelete()) {
                            addMenu.getItems().add(new SeparatorMenuItem());
                            addMenu.getItems().add(addMenuItem4);
                        }

                        addMenuItem4.setOnAction(t -> {
                            Alert alert = new Alert(AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation de suppression");
                            alert.setHeaderText(null);
                            alert.setContentText("Êtes vous sur de vouloir supprimer ?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                getTreeItem().getParent().getChildren().remove(getTreeItem());
                                getItem().deleteExtract();
                                saveManifestJson();
                            }
                        });

                        addMenuItem1.setOnAction(t -> {
                            TextInputDialog dialog = new TextInputDialog("Extrait");
                            ExtractFile extract;
                            dialog.setTitle("Nouvel extrait");
                            dialog.setHeaderText(null);
                            dialog.setContentText("Titre de l'extrait:");

                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                extract = new ExtractFile(
                                        result.get(),
                                        ZdsHttp.toSlug(result.get()),
                                        baseFilePath,
                                        (getItem().getFilePath() + File.separator + ZdsHttp.toSlug(result.get()) + ".md").substring(baseFilePath.length()+1));
                                TreeItem<ExtractFile> newChild = new TreeItem<>(extract);
                                int level = Math.max(getTreeItem().getChildren().size() - 1, 0);
                                getTreeItem().getChildren().add(level, newChild);
                                // create file
                                File extFile = new File(extract.getFilePath());
                                if (!extFile.exists()) {
                                    try {
                                        extFile.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                saveManifestJson();
                            }
                        });

                        addMenuItem2.setOnAction(t -> {
                            TextInputDialog dialog = new TextInputDialog("Conteneur");

                            dialog.setTitle("Nouveau conteneur");
                            dialog.setHeaderText(null);
                            dialog.setContentText("Titre du conteneur:");

                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                String slug = ZdsHttp.toSlug(result.get());
                                String slugRoot = Summary.getRoot().getValue().getSlug().getValue();
                                ExtractFile extract = new ExtractFile(
                                        result.get(),
                                        slug,
                                        baseFilePath,
                                        (getItem().getFilePath() + File.separator + slug + File.separator + "introduction.md").substring(baseFilePath.length()+1),
                                        (getItem().getFilePath() + File.separator + slug + File.separator + "conclusion.md").substring(baseFilePath.length()+1));
                                TreeItem<ExtractFile> newChild = new TreeItem<>(extract);
                                ExtractFile extIntro = new ExtractFile(
                                        "Introduction",
                                        slug,
                                        baseFilePath,
                                        (getItem().getFilePath() + File.separator + slug + File.separator + "introduction.md").substring(baseFilePath.length()+1),
                                        null);
                                TreeItem<ExtractFile> newChildIntro = new TreeItem<>(extIntro);
                                ExtractFile extConclu = new ExtractFile(
                                        "Conclusion",
                                        slug,
                                        baseFilePath,
                                        null,
                                        (getItem().getFilePath() + File.separator + slug + File.separator + "conclusion.md").substring(baseFilePath.length()+1));
                                TreeItem<ExtractFile> newChildConclu = new TreeItem<>(extConclu);
                                newChild.getChildren().add(newChildIntro);
                                newChild.getChildren().add(newChildConclu);
                                getTreeItem().getChildren().add(getTreeItem().getChildren().size() - 1, newChild);
                                // create files
                                File dirFile = new File(extract.getFilePath());
                                File introFile = new File(extIntro.getFilePath());
                                File concluFile = new File(extConclu.getFilePath());

                                if (!dirFile.exists() && !dirFile.isDirectory()) {
                                    dirFile.mkdir();
                                }
                                if (!introFile.exists()) {
                                    try {
                                        introFile.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!concluFile.exists()) {
                                    try {
                                        concluFile.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                saveManifestJson();
                            }
                        });

                        addMenuItem3.setOnAction(t -> {
                            TreeItem<ExtractFile> item1 = Summary.getSelectionModel().getSelectedItem();
                            TextInputDialog dialog = new TextInputDialog(item1.getValue().getTitle().getValue());
                            dialog.setTitle("Renommer  " + item1.getValue().getTitle().getValue());
                            dialog.setHeaderText(null);
                            dialog.setContentText("Nouveau titre : ");

                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                if (!result.get().trim().equals("")) {
                                    item1.getValue().setTitle(result.get());
                                    setText(result.get());
                                }
                            }

                        });

                        addMenuItem5.setOnAction(t -> {
                            try {
                                Map json = mapper.readValue(new File(filePath + File.separator + "manifest.json"), Map.class);
                                Map<String, Object> mp = new HashMap<>();
                                License lic = licOptions.get(licOptions.indexOf(new License(json.get("licence").toString(), "")));
                                TypeContent typco = typeOptions.get(typeOptions.indexOf(new TypeContent(json.get("type").toString(), "")));
                                mp.put("title", json.get("title").toString());
                                mp.put("description", json.get("description").toString());
                                mp.put("type", typco);
                                mp.put("licence", lic);
                                Map<String,Object> paramContent= mainApp.getIndex().createContentDialog(mp);
                                if(paramContent != null) {
                                    Summary.getRoot().getValue().setTitle(paramContent.get("title").toString());
                                    Summary.getRoot().getValue().setDescription(paramContent.get("description").toString());
                                    Summary.getRoot().getValue().setType(paramContent.get("type").toString());
                                    Summary.getRoot().getValue().setLicence(paramContent.get("licence").toString());
                                    saveManifestJson();
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        });
                    }

                    protected void updateItem(ExtractFile item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(getString());
                            if (getItem().isContainer()) {
                                setGraphic(createFolderIcon());
                            } else {
                                setGraphic(createFileIcon());
                            }
                            setContextMenu(addMenu);
                            initContextMenu(item);
                        }
                    }

                    private void createTextField() {
                        textField = new TextField(getString());
                        textField.setOnKeyReleased(t -> {
                            if (t.getCode() == KeyCode.ENTER) {
                                ExtractFile extract = getItem();
                                extract.setTitle(textField.getText());
                                commitEdit(extract);
                                saveManifestJson();
                            } else if (t.getCode() == KeyCode.ESCAPE) {
                                cancelEdit();
                            }
                        });
                    }

                    private String getString() {
                        return getItem() == null ? "" : getItem().getTitle().getValue();
                    }
                };

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
                        treeCell.setGraphic(createFolderIcon());
                    } else {
                        treeCell.setGraphic(createFileIcon());
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
                                getDescendantContainerCount(itemToMove) + getAncestorContainerCount(newParent),
                                getDescendantContainerCount(newParent),
                                getDescendantContainerCount(newParent.getParent()))
                           )
                        {
                            treeCell.setGraphic(createDeleteIcon());
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
                    saveManifestJson();
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
    }

    public static int getAncestorContainerCount(TreeItem<ExtractFile> node) {
        if (node.getParent() != null) {
            return getAncestorContainerCount(node.getParent()) + 1;
        } else {
            return 1;
        }
    }

    public static int getDirectChildCount(TreeItem<ExtractFile> node) {
        int sum =0;
        for(TreeItem<ExtractFile> item:node.getChildren()) {
            if(!item.getValue().isContainer()) {
                sum++;
            }
        }
        return sum;
    }

    /*
     * Count container descendants of TreeItem node
     * List all children of node and count recursively any child which are container
     */
    public static int getDescendantContainerCount(TreeItem<ExtractFile> node) {
        int maxDepth = 0;
        if(node != null ) {
            for (TreeItem<ExtractFile> n : node.getChildren()) {
                if (n.getValue().isContainer()) {
                    maxDepth = Math.max(maxDepth, getDescendantContainerCount(n) + 1);
                }
            }
        }
        return maxDepth;
    }

    public void saveManifestJson() {
        Map<String, Object> res = getMapFromTreeItem(Summary.getRoot(), new HashMap<>());
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(baseFilePath + File.separator + "manifest.json"), res);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Map<String,Object> createContentDialog(Map<String, Object> defaultParam) {
       if(defaultParam == null) {
           defaultParam = new HashMap<>();
           defaultParam.put("title", "");
           defaultParam.put("description", "");
           defaultParam.put("type", typeOptions.get(1));
           defaultParam.put("licence", licOptions.get(6));
       }
       // Create wizard
       Dialog<Pair<String, String>> dialog = new Dialog<>();
       dialog.setTitle("Nouveau contenu");
       dialog.setHeaderText("Créez un nouveau contenus pour ZdS");

       // Set the icon (must be included in the project).
       dialog.setGraphic(MdTextController.createAddFolderIcon());

       // Set the button types.
       ButtonType validButtonType = new ButtonType("Enregistrer", ButtonData.OK_DONE);
       dialog.getDialogPane().getButtonTypes().addAll(validButtonType, ButtonType.CANCEL);

       // Create the username and password labels and fields.
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(20, 150, 10, 10));

       TextField title = new TextField(defaultParam.get("title").toString());
       TextField subtitle = new TextField(defaultParam.get("description").toString());
       ComboBox<TypeContent> type = new ComboBox<>(typeOptions);
       type.setValue((TypeContent) defaultParam.get("type"));

       ComboBox<License> license = new ComboBox<>(licOptions);
       license.setValue((License) defaultParam.get("licence"));

       grid.add(new Label("Titre du contenu :"), 0, 0);
       grid.add(title, 1, 0);
       grid.add(new Label("Description du contenu:"), 0, 1);
       grid.add(subtitle, 1, 1);
       grid.add(new Label("Type de contenu:"), 0, 2);
       grid.add(type, 1, 2);
       grid.add(new Label("Licence du contenu:"), 0, 3);
       grid.add(license, 1, 3);

       // Enable/Disable login button depending on whether a username was entered.
       Node validButton = dialog.getDialogPane().lookupButton(validButtonType);

       dialog.getDialogPane().setContent(grid);

       Platform.runLater(title::requestFocus);

       dialog.setResultConverter(dialogButton -> {
           if(dialogButton == validButtonType) {
               return new Pair<>("", "");
           }
           return null;
       });

       Optional<Pair<String, String>> result = dialog.showAndWait();
       if(result.isPresent()) {
           Map<String, Object> map = new HashMap<>();
           map.put("title",title.getText());
           map.put("description",subtitle.getText());
           map.put("type",type.getValue().getCode());
           map.put("licence",license.getValue().getCode());
           return map;
       } else {
           return null;
       }

    }

}
