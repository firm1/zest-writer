package com.zestedesavoir.zestwriter.view.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.model.Container;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.ContentNode;
import com.zestedesavoir.zestwriter.model.Extract;
import com.zestedesavoir.zestwriter.model.MetaContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MdTextController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class MdTreeCell extends TreeCell<ContentNode>{
	private MdTextController index;
	private String baseFilePath;
	private final Logger logger;
    private ContextMenu addMenu = new ContextMenu();
    private Content content;


    public MdTreeCell(MdTextController index) {
		this.index = index;
		this.content = index.getMainApp().getContents().get(0);
		this.baseFilePath = ((Content) index.getSummary().getRoot().getValue()).getBasePath();
		this.logger = LoggerFactory.getLogger(getClass());
	}

	public void initContextMenu(ContentNode item) {
        MenuItem addMenuItem1 = new MenuItem(Configuration.bundle.getString("ui.actions.add_extract.label"));
        MenuItem addMenuItem2 = new MenuItem(Configuration.bundle.getString("ui.actions.add_container.label"));
        MenuItem addMenuItem3 = new MenuItem(Configuration.bundle.getString("ui.actions.rename.label"));
        MenuItem addMenuItem4 = new MenuItem(Configuration.bundle.getString("ui.actions.delete.label"));
        MenuItem addMenuItem5 = new MenuItem(Configuration.bundle.getString("ui.actions.edit.label"));
        addMenuItem1.setGraphic(IconFactory.createFileIcon());
        addMenuItem2.setGraphic(IconFactory.createAddFolderIcon());
        addMenuItem3.setGraphic(IconFactory.createEditIcon());
        addMenuItem4.setGraphic(IconFactory.createRemoveIcon());
        addMenuItem5.setGraphic(IconFactory.createEditIcon());
        addMenu.getItems().clear();

        if (item.canTakeContainer(((Content)index.getSummary().getRoot().getValue()))) {
            addMenu.getItems().add(addMenuItem2);
        }
        if (item.canTakeExtract()) {
            addMenu.getItems().add(addMenuItem1);
        }
        if (item.isEditable()) {
            addMenu.getItems().add(addMenuItem3);
        }
        if (item instanceof Content) {
            addMenu.getItems().add(addMenuItem5);
        }
        if (item.canDelete()) {
            addMenu.getItems().add(new SeparatorMenuItem());
            addMenu.getItems().add(addMenuItem4);
        }

        addMenuItem4.setOnAction(t -> {
            Alert alert = new CustomAlert(AlertType.CONFIRMATION);
            alert.setTitle(Configuration.bundle.getString("ui.dialog.delete.title"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.delete.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.delete.text"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                logger.debug("Tentative de suppression");
                // delete in logical tree
                Container parentContainer = (Container) getTreeItem().getParent().getValue();
                parentContainer.getChildren().remove(getItem());
                // delete in gui tree
                getTreeItem().getParent().getChildren().remove(getTreeItem());
                // delete physically file
                getItem().delete();
                saveManifestJson();
            }
        });

        addMenuItem1.setOnAction(t -> {
            logger.debug("Tentative d'ajout d'un nouvel extrait");
            TextInputDialog dialog = new TextInputDialog("Extrait");
            Extract extract;
            dialog.setTitle(Configuration.bundle.getString("ui.dialog.add_extract.title"));
            dialog.setHeaderText(Configuration.bundle.getString("ui.dialog.add_extract.header"));
            dialog.setContentText(Configuration.bundle.getString("ui.dialog.add_extract.text")+" :");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                extract = new Extract("extract",
                        ZdsHttp.toSlug(result.get()),
                        result.get(),
                        (getItem().getFilePath() + "/" + ZdsHttp.toSlug(result.get()) + ".md").substring(baseFilePath.length()+1));
                extract.setRootContent(content, baseFilePath);
                ((Container)getItem()).getChildren().add(extract);
                // create file
                File extFile = new File(extract.getFilePath());
                if (!extFile.exists()) {
                    try {
                        extFile.createNewFile();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                saveManifestJson();
                index.openContent(content);
            }
        });

        addMenuItem2.setOnAction(t -> {
            logger.debug("Tentative d'ajout d'un nouveau conteneur");
            TextInputDialog dialog = new TextInputDialog(Configuration.bundle.getString("ui.dialog.add_container"));

            dialog.setTitle(Configuration.bundle.getString("ui.dialog.add_container.title"));
            dialog.setHeaderText(Configuration.bundle.getString("ui.dialog.add_container.header"));
            dialog.setContentText(Configuration.bundle.getString("ui.dialog.add_container.text")+" :");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String slug = ZdsHttp.toSlug(result.get());
                Container container = new Container("container",
                        slug,
                        result.get(),
                        (getItem().getFilePath() + "/" + slug + "/" + "introduction.md").substring(baseFilePath.length()+1),
                        (getItem().getFilePath() + "/" + slug + "/" + "conclusion.md").substring(baseFilePath.length()+1),
                        new ArrayList<>());
                container.setBasePath(baseFilePath);
                ((Container)getItem()).getChildren().add(container);

                // create files
                File dirFile = new File(container.getFilePath());
                File introFile = new File(container.getIntroduction().getFilePath());
                File concluFile = new File(container.getConclusion().getFilePath());

                if (!dirFile.exists() && !dirFile.isDirectory()) {
                    dirFile.mkdir();
                }
                try {
                    if (!introFile.exists()) {
                        introFile.createNewFile();
                    }
                    if (!concluFile.exists()) {
                        concluFile.createNewFile();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                saveManifestJson();
                index.openContent(content);
            }
        });

        addMenuItem3.setOnAction(t -> {
            logger.debug("Tentative de rennomage d'un conteneur ou extrait");
            TreeItem<ContentNode> item1 = index.getSummary().getSelectionModel().getSelectedItem();
            TextInputDialog dialog = new TextInputDialog(item1.getValue().getTitle());
            dialog.setTitle(Configuration.bundle.getString("ui.dialog.rename.title")+"  " + item1.getValue().getTitle());
            dialog.setHeaderText(Configuration.bundle.getString("ui.dialog.rename.header"));
            dialog.setContentText(Configuration.bundle.getString("ui.dialog.rename.text")+" : ");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (!result.get().trim().equals("")) {
                    item1.getValue().setTitle(result.get());
                    saveManifestJson();
                    index.openContent(content);
                }
            }

        });

        addMenuItem5.setOnAction(t -> {
            logger.debug("Tentative d'édition d'un contenu");
            try {
                Map<String,Object> paramContent= FunctionTreeFactory.initContentDialog(content);
                if(paramContent != null) {
                    index.getSummary().getRoot().getValue().setTitle(paramContent.get("title").toString());
                    ((Content) index.getSummary().getRoot().getValue()).setDescription(paramContent.get("description").toString());
                    ((Content) index.getSummary().getRoot().getValue()).setType(paramContent.get("type").toString());
                    ((Content) index.getSummary().getRoot().getValue()).setLicence(paramContent.get("licence").toString());
                    saveManifestJson();
                    index.openContent(content);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    protected void updateItem(ContentNode item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getString());
            setGraphic(getItem().buildIcon());
            setContextMenu(addMenu);
            initContextMenu(item);
        }
    }

    private String getString() {
        return getItem() == null ? "" : getItem().getTitle();
    }

    public void saveManifestJson() {
        Content c = (Content) index.getSummary().getRoot().getValue();

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(baseFilePath + File.separator + "manifest.json"), c);
            logger.info("Fichier manifest sauvegardé");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
