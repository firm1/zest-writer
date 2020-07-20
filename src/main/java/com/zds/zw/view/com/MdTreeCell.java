package com.zds.zw.view.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.zw.MainApp;
import com.zds.zw.model.*;
import com.zds.zw.utils.Configuration;
import com.zds.zw.utils.HtmlToPlainText;
import com.zds.zw.utils.ZdsHttp;
import com.zds.zw.utils.readability.Readability;
import com.zds.zw.view.MdTextController;
import com.zds.zw.view.MenuController;
import com.zds.zw.view.dialogs.BaseDialog;
import com.zds.zw.view.task.ComputeIndexService;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MdTreeCell extends TreeCell<ContentNode>{
    private final Logger logger;
    private MdTextController index;
	private String baseFilePath;
    private final ContextMenu addMenu = new ContextMenu();
    private Content content;


    public MdTreeCell(MdTextController index) {
		this.index = index;
        this.content = index.getMainApp().getContent();
        if(index.getMainApp().contentProperty().isNotNull().get()) {
            this.baseFilePath = this.content.getBasePath();
        }
		this.logger = LoggerFactory.getLogger(getClass());
	}

    private void closeTab(Textual closedTextual) {
        for (Textual entry : index.getMainApp().getExtracts()) {
            if (entry.equals(closedTextual)) {
                index.getEditorList().getTabs().remove(FunctionTreeFactory.getTabFromTextual(index.getEditorList(), entry));
                index.getMainApp().getExtracts().remove(entry);
                break;
            }
        }
    }

	private void initContextMenu(ContentNode item) {
        TreeItem<ContentNode> parentTree = getTreeItem().getParent();
        long countContainerParent;
        if(parentTree != null) {
            countContainerParent = parentTree.getChildren().stream()
                    .filter(x -> x.getValue() instanceof Container)
                    .count();
        } else {
            countContainerParent = 0;
        }
        MenuItem addMenuItem1 = new MenuItem(Configuration.getBundle().getString("ui.actions.add_extract.label"));
        MenuItem addMenuItem2 = new MenuItem(Configuration.getBundle().getString("ui.actions.add_container.label"));
        MenuItem addMenuItem3 = new MenuItem(Configuration.getBundle().getString("ui.actions.rename.label"));
        MenuItem addMenuItem4 = new MenuItem(Configuration.getBundle().getString("ui.actions.delete.label"));
        MenuItem addMenuItem5 = new MenuItem(Configuration.getBundle().getString("ui.actions.edit.label"));
        MenuItem addMenuItem6 = new MenuItem(Configuration.getBundle().getString("ui.actions.merge_extracts.label"));
        MenuItem addMenuItem7 = new MenuItem(Configuration.getBundle().getString("ui.actions.unpack_extracts.label"));
        Menu menuStats = new Menu(Configuration.getBundle().getString("ui.actions.stats.label"));
        MenuItem menuStatCountHisto = new MenuItem(Configuration.getBundle().getString("ui.actions.stats.count.histo"));
        MenuItem menuStatReadability = new MenuItem(Configuration.getBundle().getString("ui.actions.stats.readability"));
        MenuItem menuStatMistakes = new MenuItem(Configuration.getBundle().getString("ui.actions.stats.mistake"));
        menuStats.getItems().add(menuStatCountHisto);
        menuStats.getItems().add(menuStatReadability);
        menuStats.getItems().add(menuStatMistakes);
        addMenuItem1.setGraphic(IconFactory.createFileIcon());
        addMenuItem2.setGraphic(IconFactory.createAddFolderIcon());
        addMenuItem3.setGraphic(IconFactory.createEditIcon());
        addMenuItem4.setGraphic(IconFactory.createRemoveIcon());
        addMenuItem5.setGraphic(IconFactory.createEditIcon());
        addMenuItem6.setGraphic(IconFactory.createMoveIcon());
        addMenuItem7.setGraphic(IconFactory.createDeleteIcon());
        menuStats.setGraphic(IconFactory.createStatsIcon());
        menuStatCountHisto.setGraphic(IconFactory.createStatsHistoIcon());
        menuStatReadability.setGraphic(IconFactory.createStatsHistoIcon());
        menuStatMistakes.setGraphic(IconFactory.createAbcIcon());
        addMenu.getItems().clear();

        if(item instanceof Container) {
            addMenu.getItems().add(addMenuItem1);
            addMenu.getItems().add(addMenuItem2);
            if (!item.canTakeContainer((Content) index.getSummary().getRoot().getValue())) {
                addMenuItem2.setDisable(true);
            }
            if (!item.canTakeExtract()) {
                addMenuItem1.setDisable(true);
            }
        }
        if (item.isEditable()) {
            addMenu.getItems().add(addMenuItem3);
        }
        if (item instanceof Content) {
            addMenu.getItems().add(addMenuItem5);
            if(((Content) item).isArticle() || ((Content) item).isOpinion()) {
                addMenuItem2.setDisable(true);
                addMenuItem6.setDisable(true);
            }
        }
        if(item instanceof Container) {
            addMenu.getItems().add(new SeparatorMenuItem());
            addMenu.getItems().add(menuStats);
        }
        if (item.canMergeExtracts((Content) index.getSummary().getRoot().getValue())) {
            addMenu.getItems().add(new SeparatorMenuItem());
            addMenu.getItems().add(addMenuItem6);
        }
        if(item instanceof Container && countContainerParent == 1) {
            addMenu.getItems().add(new SeparatorMenuItem());
            addMenu.getItems().add(addMenuItem7);

        }
        if (item.canDelete()) {
            addMenu.getItems().add(new SeparatorMenuItem());
            addMenu.getItems().add(addMenuItem4);
        }


        addMenuItem4.setOnAction(t -> {
            Alert alert = new CustomAlert(AlertType.CONFIRMATION);
            alert.setTitle(Configuration.getBundle().getString("ui.dialog.delete.title"));
            alert.setHeaderText(Configuration.getBundle().getString("ui.dialog.delete.header"));
            alert.setContentText(Configuration.getBundle().getString("ui.dialog.delete.text"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                logger.debug("Tentative de suppression");
                if(getTreeItem().getValue() instanceof Content) {
                    FunctionTreeFactory.clearContent(index.getMainApp().getExtracts(), index.getMainApp().getIndex().getEditorList(), () -> {
                        Content deleteContent = (Content) getTreeItem().getValue();
                        // delete last projects
                        MainApp.getConfig().delActionProject(deleteContent.getFilePath());
                        // delete physically file
                        deleteContent.delete();
                        // delete in logical tree
                        index.getMainApp().setContent(null);
                        index.refreshRecentProject();
                        return null;
                    });
                } else {
                    // close tabs
                    if(getTreeItem().getValue() instanceof Textual) {
                        closeTab((Textual) getTreeItem().getValue());
                    } else if (getTreeItem().getValue() instanceof Container) {
                        Container deleteContainer = (Container) getTreeItem().getValue();
                        deleteContainer.doOnTextual(textual -> {
                            closeTab(textual);
                            return null;
                        });
                    }
                    // delete in logical tree
                    Container parentContainer = (Container) getTreeItem().getParent().getValue();
                    parentContainer.getChildren().remove(getItem());
                    // delete in gui tree
                    getTreeItem().getParent().getChildren().remove(getTreeItem());
                    // delete physically file
                    getItem().delete();
                    saveManifestJson();
                }
                index.refreshRecentProject();
            }
        });

        addMenuItem1.setOnAction(t -> {
            logger.debug("Tentative d'ajout d'un nouvel extrait");
            TextInputDialog dialog = new TextInputDialog(Configuration.getBundle().getString("ui.dialog.add_extract"));
            Extract extract;
            dialog.setTitle(Configuration.getBundle().getString("ui.dialog.add_extract.title"));
            dialog.setHeaderText(Configuration.getBundle().getString("ui.dialog.add_extract.header"));
            dialog.setContentText(Configuration.getBundle().getString("ui.dialog.add_extract.text")+" :");

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
                FunctionTreeFactory.performCreateNewFile(extFile);
                saveManifestJson();
                // add visual node
                getTreeItem().getChildren().add(getTreeItem().getChildren().size()-1, FunctionTreeFactory.buildChild(new TreeItem<>((ContentNode) extract)));
            }
        });

        addMenuItem2.setOnAction(t -> {
            logger.debug("Tentative d'ajout d'un nouveau conteneur");
            TextInputDialog dialog = new TextInputDialog(Configuration.getBundle().getString("ui.dialog.add_container"));

            dialog.setTitle(Configuration.getBundle().getString("ui.dialog.add_container.title"));
            dialog.setHeaderText(Configuration.getBundle().getString("ui.dialog.add_container.header"));
            dialog.setContentText(Configuration.getBundle().getString("ui.dialog.add_container.text")+" :");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String slug = ZdsHttp.toSlug(result.get());
                String baseSlug = FunctionTreeFactory.getUniqueDirPath(getItem().getFilePath() + "/" + slug);
                Container container = new Container("container",
                        slug,
                        result.get(),
                        (baseSlug + "/" + Constant.DEFAULT_INTRODUCTION_FILENAME).substring(baseFilePath.length()+1),
                        (baseSlug + "/" + Constant.DEFAULT_CONCLUSION_FILENAME).substring(baseFilePath.length()+1),
                        new ArrayList<>(),
                        "true");
                container.setBasePath(baseFilePath);
                ((Container)getItem()).getChildren().add(container);

                // create files
                File dirFile = new File(container.getFilePath());
                FunctionTreeFactory.generateMetadataAttributes(container);

                if (!dirFile.exists() && !dirFile.isDirectory()) {
                    if(!dirFile.mkdir()) {
                        logger.error("Probleme pour créer le répertoire "+dirFile.getAbsolutePath());
                    }
                }
                saveManifestJson();
                // refresh visual
                getTreeItem().getChildren().add(getTreeItem().getChildren().size()-1, FunctionTreeFactory.buildChild(new TreeItem<>((ContentNode) container)));
            }
        });

        addMenuItem3.setOnAction(t -> {
            logger.debug("Tentative de renommage d'un conteneur ou extrait");
            TreeItem<ContentNode> item1 = index.getSummary().getSelectionModel().getSelectedItem();
            TextInputDialog dialog = new TextInputDialog(item1.getValue().getTitle());
            dialog.setTitle(Configuration.getBundle().getString("ui.dialog.rename.title")+"  " + item1.getValue().getTitle());
            dialog.setHeaderText(Configuration.getBundle().getString("ui.dialog.rename.header"));
            dialog.setContentText(Configuration.getBundle().getString("ui.dialog.rename.text")+" : ");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (!"".equals(result.get().trim())) {
                    index.getMainApp().getExtracts().stream()
                            .filter(x -> x.equals(item1.getValue()))
                            .forEach(x -> FunctionTreeFactory.getTabFromTextual(index.getEditorList(), x).setText(result.get()));
                    item1.getValue().renameTitle(result.get());
                    saveManifestJson();
                    index.getSummary().refresh();
                }
            }   
        });

        addMenuItem5.setOnAction(t -> {
            logger.debug("Tentative d'édition d'un contenu");
            try {
                Map<String,Object> paramContent= FunctionTreeFactory.initContentDialog(content);
                if(paramContent != null) {
                    index.getSummary().getRoot().getValue().renameTitle(paramContent.get("title").toString());
                    ((Content) index.getSummary().getRoot().getValue()).setDescription(paramContent.get("description").toString());
                    ((Content) index.getSummary().getRoot().getValue()).setType(paramContent.get("type").toString());
                    ((Content) index.getSummary().getRoot().getValue()).setLicence(paramContent.get("licence").toString());
                    baseFilePath = ((Content) index.getSummary().getRoot().getValue()).getBasePath();
                    saveManifestJson();
                    // refresh visual
                    index.getSummary().refresh();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        addMenuItem6.setOnAction(t -> {
            logger.debug("Tentative de déplacement des extraits");
            TextInputDialog dialog = new TextInputDialog(Configuration.getBundle().getString("ui.dialog.add_container"));

            dialog.setTitle(Configuration.getBundle().getString("ui.dialog.add_container.title"));
            dialog.setHeaderText(Configuration.getBundle().getString("ui.dialog.add_container.header"));
            dialog.setContentText(Configuration.getBundle().getString("ui.dialog.add_container.text")+" :");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                // lists extracts
                List<Extract> extracts = ((Container)getItem()).getChildren().stream()
                        .map(ext -> (Extract) ext)
                        .collect(Collectors.toList());

                String slug = ZdsHttp.toSlug(result.get());
                String baseSlug = FunctionTreeFactory.getUniqueDirPath(getItem().getFilePath() + "/" + slug);
                Container container = new Container("container",
                        slug,
                        result.get(),
                        (baseSlug + "/" + Constant.DEFAULT_INTRODUCTION_FILENAME).substring(baseFilePath.length()+1),
                        (baseSlug + "/" + Constant.DEFAULT_CONCLUSION_FILENAME).substring(baseFilePath.length()+1),
                        new ArrayList<>(),
                        "true");
                container.setBasePath(baseFilePath);
                ((Container)getItem()).getChildren().add(container);

                // create files
                File dirFile = new File(container.getFilePath());
                FunctionTreeFactory.generateMetadataAttributes(container);

                if (!dirFile.exists() && !dirFile.isDirectory()) {
                    if(!dirFile.mkdir()) {
                        logger.error("Probleme pour créer le répertoire "+dirFile.getAbsolutePath());
                    }
                }

                // move physical file to new directory
                extracts.forEach(extract -> {
                    try {
                        FileUtils.moveFileToDirectory(new File(extract.getFilePath()), new File(container.getFilePath()), false);
                    } catch (IOException e) {
                        logger.error("Erreur lors du déplacement d'un extrait", e);
                    }
                });
                // move logical file to new directory
                extracts.forEach(extract -> {
                    ((Container) getItem()).getChildren().remove(extract);
                    String oldText = extract.getText();
                    if (oldText.contains("/")) {
                        oldText = oldText.substring(oldText.indexOf('/') + 1);
                    }
                    extract.setText((baseSlug + "/" + oldText).substring(baseFilePath.length() + 1));

                });
                container.getChildren().addAll(extracts);

                saveManifestJson();
                index.openContent(content);
            }
        });

        addMenuItem7.setOnAction(t -> {
            logger.debug("Tentative de suppression de conteneur");
            // move extracts on parents
            Container parentNode = (Container) getTreeItem().getParent().getValue();
            Container currentNode = (Container) getItem();
            currentNode.getChildren().forEach(x -> parentNode.getChildren().add(x));
            parentNode.getChildren().remove(currentNode);
            saveManifestJson();
            // resfresh visual
            index.openContent(content);
        });

        menuStatCountHisto.setOnAction(t -> {
            logger.debug("Tentative de calcul des statistiques de type histogramme");
            BaseDialog dialog = new BaseDialog(Configuration.getBundle().getString("ui.actions.stats.label"), Configuration.getBundle().getString("ui.actions.stats.header")+" "+getItem().getTitle());
            dialog.getDialogPane().setPrefSize(800, 600);
            dialog.getDialogPane().getButtonTypes().addAll(new ButtonType(Configuration.getBundle().getString("ui.actions.stats.close"), ButtonBar.ButtonData.CANCEL_CLOSE));

            // draw
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setLegendVisible(false);
            barChart.setCategoryGap(5);
            barChart.setBarGap(5);

            xAxis.setLabel(Configuration.getBundle().getString("ui.actions.stats.xaxis"));
            yAxis.setLabel(Configuration.getBundle().getString("ui.actions.stats.yaxis"));

            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            Container container = (Container) getItem();
            Function<Textual, Integer> performCount = (Textual ch) -> {
                String md = ch.readMarkdown();
                Readability readText = new Readability(md);
                return readText.getCharacters();
            };
            Map<Textual, Integer> stat = container.doOnTextual(performCount);
            for(Map.Entry<Textual, Integer> st:stat.entrySet()) {
                if(!(st.getKey() instanceof MetaAttribute)) {
                    series1.getData().add(new XYChart.Data(st.getKey().getLimitedTitle(), st.getValue()));
                } else {
                    MetaAttribute attribute = (MetaAttribute) st.getKey();
                    series1.getData().add(new XYChart.Data(attribute.getLimitedExpandTitle(), st.getValue()));
                }
            }
            barChart.getData().addAll(series1);
            dialog.getDialogPane().setContent(barChart);
            dialog.setResizable(true);
            dialog.showAndWait();
        });

        menuStatReadability.setOnAction(t -> {
            logger.debug("Tentative de calcul des statistiques de lisiblité");

            Container container = (Container) getItem();
            Function<Textual, Double> performGuning = (Textual ch) -> {
                String htmlText = StringEscapeUtils.unescapeHtml4(MenuController.markdownToHtml(index, ch.readMarkdown()));
                String plainText = new HtmlToPlainText().getPlainText(Jsoup.parse(htmlText), "pre", "table");
                if("".equals(plainText.trim())){
                    return 100.0;
                }else{
                    Readability rd = new Readability(plainText);
                    return rd.getGunningFog();
                }
            };
            Function<Textual, Double> performFlesch = (Textual ch) -> {
                String htmlText = StringEscapeUtils.unescapeHtml4(MenuController.markdownToHtml(index, ch.readMarkdown()));
                String plainText = new HtmlToPlainText().getPlainText(Jsoup.parse(htmlText), "pre", "table");
                if("".equals(plainText.trim())){
                    return 100.0;
                }else{
                    Readability rd = new Readability(plainText);
                    return rd.getFleschReadingEase();
                }
            };

            ComputeIndexService computeGuningService = new ComputeIndexService(performGuning, container);
            index.getMainApp().getMenuController().getHBottomBox().getChildren().clear();
            index.getMainApp().getMenuController().getHBottomBox().getChildren().addAll(index.getMainApp().getMenuController().getLabelField());
            index.getMainApp().getMenuController().getLabelField().textProperty().bind(computeGuningService.messageProperty());
            computeGuningService.setOnSucceeded(g -> {
                ComputeIndexService computeFleshService = new ComputeIndexService(performFlesch, container);
                computeFleshService.setOnSucceeded(f -> {
                    displayIndexChart(((ComputeIndexService) g.getSource()).getValue(), ((ComputeIndexService) f.getSource()).getValue());
                    index.getMainApp().getMenuController().getHBottomBox().getChildren().clear();
                });
                computeFleshService.setOnFailed(f -> index.getMainApp().getMenuController().getHBottomBox().getChildren().clear());
                computeFleshService.start();
            });
            computeGuningService.setOnFailed(g -> index.getMainApp().getMenuController().getHBottomBox().getChildren().clear());
            computeGuningService.start();
        });

    }

    private XYChart.Series<String, Number> getSeriesIndex(Map<String, Double> statIndex, String title) {
        XYChart.Series<String, Number> series = new XYChart.Series();
        series.setName(title);

        for(Map.Entry<String, Double> st:statIndex.entrySet()) {
            series.getData().add(new XYChart.Data(st.getKey(), st.getValue()));
        }
        return series;
    }

    private void displayTypoChart(Map<String, Double> statT) {
        BaseDialog dialog = new BaseDialog(Configuration.getBundle().getString("ui.actions.stats.label"), Configuration.getBundle().getString("ui.actions.stats.header")+" "+getItem().getTitle());
        dialog.getDialogPane().setPrefSize(800, 600);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType(Configuration.getBundle().getString("ui.actions.stats.close"), ButtonBar.ButtonData.CANCEL_CLOSE));

        // draw
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String,Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(Configuration.getBundle().getString("ui.actions.stats.mistake"));
        lineChart.setLegendVisible(false);

        xAxis.setLabel(Configuration.getBundle().getString("ui.actions.stats.xaxis"));
        yAxis.setLabel(Configuration.getBundle().getString("ui.actions.stats.mistakes.yaxis"));

        XYChart.Series<String, Number> series1 = new XYChart.Series();
        for(Map.Entry<String, Double> st:statT.entrySet()) {
            series1.getData().add(new XYChart.Data(st.getKey(), st.getValue()));
        }

        lineChart.getData().addAll(series1);
        dialog.getDialogPane().setContent(lineChart);
        dialog.setResizable(true);
        dialog.showAndWait();

    }

    public void displayIndexChart(Map<String, Double> statG, Map<String, Double> statF) {
        BaseDialog dialog = new BaseDialog(Configuration.getBundle().getString("ui.actions.stats.label"), Configuration.getBundle().getString("ui.actions.stats.header")+" "+getItem().getTitle());
        dialog.getDialogPane().setPrefSize(800, 600);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType(Configuration.getBundle().getString("ui.actions.stats.close"), ButtonBar.ButtonData.CANCEL_CLOSE));

        // draw
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String,Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(Configuration.getBundle().getString("ui.actions.stats.readability"));

        xAxis.setLabel(Configuration.getBundle().getString("ui.actions.stats.xaxis"));
        yAxis.setLabel(Configuration.getBundle().getString("ui.actions.readable.yaxis"));

        XYChart.Series<String, Number> series1  = getSeriesIndex(statG, Configuration.getBundle().getString("ui.menu.edit.readable.gunning_index"));
        XYChart.Series<String, Number> series2 = getSeriesIndex(statF, Configuration.getBundle().getString("ui.menu.edit.readable.flesch_index"));

        lineChart.getData().addAll(series1, series2);
        dialog.getDialogPane().setContent(lineChart);
        dialog.setResizable(true);
        xAxis.setTickLabelRotation(0);
        dialog.showAndWait();
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
