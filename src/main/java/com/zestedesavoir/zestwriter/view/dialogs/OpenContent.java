package com.zestedesavoir.zestwriter.view.dialogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OpenContent{
    private MainApp mainApp;
    private Stage openContentWindow;
    private List<Content> sortedContents;
    @FXML private GridPane globalPane;
    @FXML private TextField search;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setOpenContentWindow(Stage openContentWindow) {
        this.openContentWindow = openContentWindow;
    }

    @FXML public void initialize() {
        loadProject();
        refreshProject(2, null);
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            int threshold = 4;
            if(newValue.length() >= threshold) {
                refreshProject(2, newValue);
            } else if(oldValue.length() > newValue.length()) {
                refreshProject(2, newValue);
            }
        });
    }

    public void refreshProject(int colNumber, String keywords) {
        globalPane.getChildren().clear();
        for(int i=0; i<colNumber; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(380);
            globalPane.getColumnConstraints().add(col);
        }
        int row=0;
        int col=0;
        List<Content> sortedPaths = sortedContents.stream()
                .filter(c -> {
                    if(keywords == null ) {
                        return true;
                    } else {
                        return c.getTitle().toLowerCase().contains(keywords.toLowerCase()) || c.getDescription().toLowerCase().contains(keywords.toLowerCase());
                    }
                })
                .collect(Collectors.toList());

        for(Content c: sortedPaths) {
            BorderPane bPane = new BorderPane();
            bPane.setPadding(new Insets(10, 10, 10, 10));
            bPane.getStyleClass().add("box-content");
            bPane.getStyleClass().add("open-content");
            Hyperlink link = new Hyperlink(c.getTitle());
            Label description = new Label(c.getDescription());
            description.setWrapText(true);
            MaterialDesignIconView type = IconFactory.createContentIcon(c.getType());
            link.setOnAction(t -> {mainApp.setContent(c); openContentWindow.close();});
            BorderPane.setAlignment(link, Pos.TOP_CENTER);
            BorderPane.setMargin(link, new Insets(10, 10, 10, 10));
            BorderPane.setAlignment(description, Pos.TOP_LEFT);
            BorderPane.setMargin(description, new Insets(0, 10, 0, 10));
            bPane.setTop(link);
            bPane.setCenter(description);
            bPane.setLeft(type);
            globalPane.add(bPane, col % colNumber, row);
            col++;
            if (col % colNumber == 0) {
                row++;
            }
        }
    }
    public void loadProject() {
        ObjectMapper mapper = new ObjectMapper();
        File baseDirectory = new File(MainApp.getConfig().getOfflineSaver().getBaseDirectory());
        if(baseDirectory.exists() && baseDirectory.isDirectory()) {
            String[] projects = baseDirectory.list((dir, name) -> new File(dir, name).isDirectory());

            sortedContents = Arrays.asList(projects).stream()
                    .map(f -> new File(baseDirectory, f).getAbsolutePath())
                    .filter(f -> new File(f, "manifest.json").exists())
                    .map(f -> new Pair<>(f, new File(f, "manifest.json")))
                    .sorted((o1, o2) -> {
                        Date d1 = new Date(o1.getValue().lastModified());
                        Date d2 = new Date(o2.getValue().lastModified());
                        return d2.compareTo(d1);
                    })
                    .map(man -> {
                        try {
                            Content c = mapper.readValue(man.getValue(), Content.class);
                            c.setRootContent(c, man.getKey());
                            return new Pair<>(man.getKey(), c);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .map(Pair::getValue)
                    .collect(Collectors.toList());
        }
    }

    @FXML public void handleOpenButtonAction() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Configuration.getBundle().getString("ui.menu.dialog.content.open.title"));
        File defaultDirectory;

        if(MainApp.getConfig().getWorkspaceFactory() == null){
            MainApp.getConfig().loadWorkspace();
        }
        defaultDirectory = new File(MainApp.getZdsutils().getOfflineContentPathDir());
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(MainApp.getPrimaryStage());

        if(selectedDirectory != null){
            File manifest = new File(selectedDirectory.getAbsolutePath() + File.separator + "manifest.json");
            ObjectMapper mapper = new ObjectMapper();
            Content content;
            try{
                content = mapper.readValue(manifest, Content.class);
                content.setRootContent(content, selectedDirectory.getAbsolutePath());
                mainApp.setContent(content);
                openContentWindow.close();
            }catch(IOException e){
                MainApp.getLogger().error(e.getMessage(), e);
            }
        }
    }

}
