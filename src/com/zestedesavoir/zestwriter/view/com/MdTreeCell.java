package com.zestedesavoir.zestwriter.view.com;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.model.License;
import com.zestedesavoir.zestwriter.model.TypeContent;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MenuController;
import com.zestedesavoir.zestwriter.view.dialogs.EditContentDialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;

public class MdTreeCell extends TreeCell<ExtractFile>{
	private TextField textField;
	private TreeView<ExtractFile> Summary;
	private String baseFilePath;
	private final Logger logger;
	private ObjectMapper mapper;
    private ContextMenu addMenu = new ContextMenu();


    public MdTreeCell(TreeView<ExtractFile> summary, String baseFilePath, ObjectMapper mapper) {
		Summary = summary;
		this.baseFilePath = baseFilePath;
		this.logger = LoggerFactory.getLogger(getClass());
		this.mapper = mapper;
	}

	public void initContextMenu(ExtractFile item) {

        MenuItem addMenuItem1 = new MenuItem("Ajouter un extrait");
        MenuItem addMenuItem2 = new MenuItem("Ajouter un conteneur");
        MenuItem addMenuItem3 = new MenuItem("Renommer");
        MenuItem addMenuItem4 = new MenuItem("Supprimer");
        MenuItem addMenuItem5 = new MenuItem("Editer");
        addMenuItem1.setGraphic(IconFactory.createFileIcon());
        addMenuItem2.setGraphic(IconFactory.createAddFolderIcon());
        addMenuItem3.setGraphic(IconFactory.createEditIcon());
        addMenuItem4.setGraphic(IconFactory.createRemoveIcon());
        addMenuItem5.setGraphic(IconFactory.createEditIcon());
        addMenu.getItems().clear();
        if (item.canTakeContainer(FunctionTreeFactory.getAncestorContainerCount(getTreeItem()), FunctionTreeFactory.getDirectChildCount(getTreeItem()))) {
            addMenu.getItems().add(addMenuItem2);
        }
        if (item.canTakeExtract(FunctionTreeFactory.getDescendantContainerCount(getTreeItem()))) {
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
            logger.debug("Tentative d'ajout d'un nouvel extrait");
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
                        (getItem().getFilePath() + "/" + ZdsHttp.toSlug(result.get()) + ".md").substring(baseFilePath.length()+1));
                TreeItem<ExtractFile> newChild = new TreeItem<>(extract);
                int level = Math.max(getTreeItem().getChildren().size() - 1, 0);
                getTreeItem().getChildren().add(level, newChild);
                // create file
                File extFile = new File(extract.getFilePath());
                if (!extFile.exists()) {
                    try {
                        extFile.createNewFile();
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
                saveManifestJson();
            }
        });

        addMenuItem2.setOnAction(t -> {
            logger.debug("Tentative d'ajout d'un nouveau conteneur");
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
                        (getItem().getFilePath() + "/" + slug + "/" + "introduction.md").substring(baseFilePath.length()+1),
                        (getItem().getFilePath() + "/" + slug + "/" + "conclusion.md").substring(baseFilePath.length()+1));
                TreeItem<ExtractFile> newChild = new TreeItem<>(extract);
                ExtractFile extIntro = new ExtractFile(
                        "Introduction",
                        slug,
                        baseFilePath,
                        (getItem().getFilePath() + "/" + slug + "/" + "introduction.md").substring(baseFilePath.length()+1),
                        null);
                TreeItem<ExtractFile> newChildIntro = new TreeItem<>(extIntro);
                ExtractFile extConclu = new ExtractFile(
                        "Conclusion",
                        slug,
                        baseFilePath,
                        null,
                        (getItem().getFilePath() + "/" + slug + "/" + "conclusion.md").substring(baseFilePath.length()+1));
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
                        logger.error("", e);
                    }
                }
                if (!concluFile.exists()) {
                    try {
                        concluFile.createNewFile();
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
                saveManifestJson();
            }
        });

        addMenuItem3.setOnAction(t -> {
            logger.debug("Tentative de rennomage d'un conteneur ou extrait");
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
            logger.debug("Tentative d'édition d'un contenu");
            try {
                Map json = mapper.readValue(new File(baseFilePath + File.separator + "manifest.json"), Map.class);
                Map<String, Object> mp = new HashMap<>();
                License lic = EditContentDialog.licOptions.get(EditContentDialog.licOptions.indexOf(new License(json.get("licence").toString(), "")));
                TypeContent typco = EditContentDialog.typeOptions.get(EditContentDialog.typeOptions.indexOf(new TypeContent(json.get("type").toString(), "")));
                mp.put("title", json.get("title").toString());
                mp.put("description", json.get("description").toString());
                mp.put("type", typco);
                mp.put("licence", lic);
                Map<String,Object> paramContent= FunctionTreeFactory.initContentDialog(mp);
                if(paramContent != null) {
                    Summary.getRoot().getValue().setTitle(paramContent.get("title").toString());
                    Summary.getRoot().getValue().setDescription(paramContent.get("description").toString());
                    Summary.getRoot().getValue().setType(paramContent.get("type").toString());
                    Summary.getRoot().getValue().setLicence(paramContent.get("licence").toString());
                    saveManifestJson();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error("", e);
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
                setGraphic(IconFactory.createFolderIcon());
            } else {
                setGraphic(IconFactory.createFileIcon());
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

    public void saveManifestJson() {
        Map<String, Object> res = getMapFromTreeItem(Summary.getRoot(), new HashMap<>());
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(baseFilePath + File.separator + "manifest.json"), res);
            logger.info("Fichier manifest sauvegardé");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("", e);
        }
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
}
