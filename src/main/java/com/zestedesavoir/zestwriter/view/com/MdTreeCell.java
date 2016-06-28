package com.zestedesavoir.zestwriter.view.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.*;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.dialogs.BaseDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MdTreeCell extends TreeCell<ContentNode>{
    private MainApp mainApp;
	private MdTextController index;
	private String baseFilePath;
	private final Logger logger;
    private ContextMenu addMenu = new ContextMenu();
    private Content content;


    public MdTreeCell(MainApp mainApp, MdTextController index) {
        this.mainApp = mainApp;
		this.index = index;
        if(index.getMainApp().getContents().size() > 0) {
            this.content = index.getMainApp().getContents().stream().findFirst().get();
            this.baseFilePath = this.content.getBasePath();
        }
        else {
            this.content = null;
        }
		this.logger = LoggerFactory.getLogger(getClass());
	}

	public void initContextMenu(ContentNode item) {
        MenuItem addMenuItem1 = new MenuItem(Configuration.bundle.getString("ui.actions.add_extract.label"));
        MenuItem addMenuItem2 = new MenuItem(Configuration.bundle.getString("ui.actions.add_container.label"));
        MenuItem addMenuItem3 = new MenuItem(Configuration.bundle.getString("ui.actions.rename.label"));
        MenuItem addMenuItem4 = new MenuItem(Configuration.bundle.getString("ui.actions.delete.label"));
        MenuItem addMenuItem5 = new MenuItem(Configuration.bundle.getString("ui.actions.edit.label"));
        Menu menuStats = new Menu(Configuration.bundle.getString("ui.actions.stats.label"));
        MenuItem menuStatCountHisto = new MenuItem(Configuration.bundle.getString("ui.actions.stats.count.histo"));
        MenuItem menuStatCountPie = new MenuItem(Configuration.bundle.getString("ui.actions.stats.count.pie"));
        menuStats.getItems().add(menuStatCountHisto);
        menuStats.getItems().add(menuStatCountPie);
        addMenuItem1.setGraphic(IconFactory.createFileIcon());
        addMenuItem2.setGraphic(IconFactory.createAddFolderIcon());
        addMenuItem3.setGraphic(IconFactory.createEditIcon());
        addMenuItem4.setGraphic(IconFactory.createRemoveIcon());
        addMenuItem5.setGraphic(IconFactory.createEditIcon());
        menuStats.setGraphic(IconFactory.createStatsIcon());
        menuStatCountHisto.setGraphic(IconFactory.createStatsHistoIcon());
        menuStatCountPie.setGraphic(IconFactory.createStatsPieIcon());
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
        if(item instanceof Container) {
            addMenu.getItems().add(new SeparatorMenuItem());
            addMenu.getItems().add(menuStats);
        }
        if (item.canDelete()) {
            addMenu.getItems().add(new SeparatorMenuItem());
            addMenu.getItems().add(addMenuItem4);
        }

        addMenuItem4.setOnAction(t -> {
            Alert alert = new CustomAlert(AlertType.CONFIRMATION, mainApp.getPrimaryStage());
            alert.setTitle(Configuration.bundle.getString("ui.dialog.delete.title"));
            alert.setHeaderText(Configuration.bundle.getString("ui.dialog.delete.header"));
            alert.setContentText(Configuration.bundle.getString("ui.dialog.delete.text"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                logger.debug("Tentative de suppression");
                if(getTreeItem().getValue() instanceof Content) {
                    Content deleteContent = (Content) getTreeItem().getValue();
                    // delete last projects
                    MainApp.config.delActionProject(deleteContent.getFilePath());
                    // delete physically file
                    deleteContent.delete();
                    // delete in logical tree
                    index.getMainApp().getContents().clear();
                    index.refreshRecentProject();
                } else {
                    // delete in logical tree
                    Container parentContainer = (Container) getTreeItem().getParent().getValue();
                    parentContainer.getChildren().remove(getItem());
                    // delete in gui tree
                    getTreeItem().getParent().getChildren().remove(getTreeItem());
                    // delete physically file
                    getItem().delete();
                    saveManifestJson();
                }
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
                        FunctionTreeFactory.getUniqueFilePath(getItem().getFilePath() + "/" + ZdsHttp.toSlug(result.get()), "md").substring(baseFilePath.length()+1));
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
                String baseSlug = FunctionTreeFactory.getUniqueDirPath(getItem().getFilePath() + "/" + slug);
                Container container = new Container("container",
                        slug,
                        result.get(),
                        (baseSlug + "/" + "introduction.md").substring(baseFilePath.length()+1),
                        (baseSlug + "/" + "conclusion.md").substring(baseFilePath.length()+1),
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
                } catch (IOException e) {
                    logger.error("Erreur lors de la créeation de "+introFile.getAbsolutePath(), e);
                }
                try {
                    if (!concluFile.exists()) {
                        concluFile.createNewFile();
                    }
                } catch (IOException e) {
                    logger.error("Erreur lors de la créeation de "+concluFile.getAbsolutePath(), e);
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

        menuStatCountHisto.setOnAction(t -> {
            logger.debug("Tentative de calcul des statistiques de type histogramme");
            BaseDialog dialog = new BaseDialog(Configuration.bundle.getString("ui.actions.stats.label"), Configuration.bundle.getString("ui.actions.stats.header")+" "+getItem().getTitle());
            dialog.getDialogPane().setPrefSize(800, 600);
            dialog.getDialogPane().getButtonTypes().addAll(new ButtonType(Configuration.bundle.getString("ui.actions.stats.close"), ButtonBar.ButtonData.CANCEL_CLOSE));

            // draw
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final BarChart<String,Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setCategoryGap(5);
            barChart.setBarGap(5);

            xAxis.setLabel(Configuration.bundle.getString("ui.actions.stats.xaxis"));
            yAxis.setLabel(Configuration.bundle.getString("ui.actions.stats.yaxis"));

            XYChart.Series series1 = new XYChart.Series();
            series1.setName(Configuration.bundle.getString("ui.actions.stats.type.histogram"));
            Container container = (Container) getItem();
            Function<Textual, Integer> performCount = (Textual ch) -> {
                String md = ch.readMarkdown();
                Readability readText = new Readability(md);
                return readText.getCharacters();
            };
            Map<Textual, Integer> stat = container.doOnTextual(performCount);
            for(Map.Entry<Textual, Integer> st:stat.entrySet()) {
                if(!(st.getKey() instanceof MetaAttribute)) {
                    series1.getData().add(new XYChart.Data(st.getKey().getTitle(), st.getValue()));
                }
            }
            barChart.getData().addAll(series1);
            dialog.getDialogPane().setContent(barChart);
            dialog.setResizable(true);
            dialog.showAndWait();
        });

        menuStatCountPie.setOnAction(t -> {
            logger.debug("Tentative de calcul des statistiques de type Camembert");
            BaseDialog dialog = new BaseDialog(Configuration.bundle.getString("ui.actions.stats.label"), Configuration.bundle.getString("ui.actions.stats.header")+" "+getItem().getTitle());
            dialog.getDialogPane().setPrefSize(800, 600);
            dialog.getDialogPane().getButtonTypes().addAll(new ButtonType(Configuration.bundle.getString("ui.actions.stats.close"), ButtonBar.ButtonData.CANCEL_CLOSE));

            // draw
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            Container container = (Container) getItem();
            Function<Textual, Integer> performCount = (Textual ch) -> {
                String md = ch.readMarkdown();
                Readability readText = new Readability(md);
                return readText.getCharacters();
            };
            Map<Textual, Integer> stat = container.doOnTextual(performCount);
            for(Map.Entry<Textual, Integer> st:stat.entrySet()) {
                if(!(st.getKey() instanceof MetaAttribute)) {
                    pieChartData.add(new PieChart.Data(st.getKey().getTitle(), st.getValue()));
                }
            }
            final PieChart chart = new PieChart(pieChartData);

            chart.setTitle(Configuration.bundle.getString("ui.actions.stats.type.pie"));
            chart.setLegendVisible(false);

            dialog.getDialogPane().setContent(chart);
            dialog.setResizable(true);
            dialog.showAndWait();
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
