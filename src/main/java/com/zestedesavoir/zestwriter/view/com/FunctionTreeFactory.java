package com.zestedesavoir.zestwriter.view.com;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.*;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Theme;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.dialogs.EditContentDialog;
import com.zestedesavoir.zestwriter.view.dialogs.FindReplaceDialog;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class FunctionTreeFactory {

    public static boolean isMacOs() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static Map<String,Object> initContentDialog(Content defaultContent) {
        if(defaultContent == null) {
            defaultContent = new Content("container",
                    "",
                    "",
                    "introduction.md",
                    "conclusion.md",
                    new ArrayList<>(),
                    2,
                    EditContentDialog.licOptions.get(6).getCode(),
                    "",
                    EditContentDialog.typeOptions.get(1).getCode());
        }
        // Create wizard
        EditContentDialog dialog = new EditContentDialog(defaultContent);

        Optional<Pair<String, Map<String, Object>>> result = dialog.showAndWait();
        if(result.isPresent()) {
            return result.get().getValue();
        } else {
            return null;
        }

     }

    public static void clearContent(ObservableMap<Textual, Tab> extracts, TabPane editorList, Supplier<Void> doAfter) {

        if(extracts.size() == 0) {
            doAfter.get();
        }

        for(Entry<Textual, Tab> entry:extracts.entrySet()) {
            Platform.runLater(() -> {
                Event.fireEvent(entry.getValue(), new Event(Tab.TAB_CLOSE_REQUEST_EVENT));

                if(editorList.getTabs().size() <= 1) {
                    extracts.clear();
                    doAfter.get();
                }
            });
        }
    }

    public static TreeItem<ContentNode> buildChild(TreeItem<ContentNode> node) {
        if(node.getValue() instanceof Container) {
            Container container = (Container) node.getValue();
            TreeItem<ContentNode> itemIntro = new TreeItem<>((ContentNode) container.getIntroduction());
            node.getChildren().add(itemIntro);
            for(MetaContent child:container.getChildren()) {
                TreeItem<ContentNode> itemChild = new TreeItem<>((ContentNode) child);
                node.getChildren().add(buildChild(itemChild));
            }
            TreeItem<ContentNode> itemConclu = new TreeItem<>((ContentNode) container.getConclusion());
            node.getChildren().add(itemConclu);
        }
        else if(node.getValue() instanceof Extract) {
            Extract extract = (Extract) node.getValue();
            return new TreeItem<>(extract);
        }
        return node;
    }

    public static void moveToContainer(TreeItem<ContentNode> dest, TreeItem<ContentNode> src) {
        // remove in model
        ((Container) src.getParent().getValue()).getChildren().remove(src.getValue());
        // remove in ui
        src.getParent().getChildren().remove(src);

        if (dest.getValue() instanceof Container) {
            Container destination = (Container)dest.getValue();
            int position = destination.getChildren().size();
            // update model
            destination.getChildren().add(position, (MetaContent) src.getValue());
            //update ui
            dest.getChildren().add(position + 1, src);

        } else {
            Container destParent = (Container) dest.getParent().getValue();
            int position = destParent.getChildren().indexOf(dest.getValue());
            // update model
            destParent.getChildren().add(position + 1, (MetaContent)src.getValue());
            // update ui
            dest.getParent().getChildren().add(position + 2, src);
        }
    }

    public static String padding(int number) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<number;i++) {
            sb.append('#');
        }
        return sb.toString();
    }

    public static String offsetHeaderMarkdown(String text, int level) {
        String regex = "^(#+)(.{0,}?)(#*)$";
        return Pattern.compile(regex, Pattern.MULTILINE).matcher(text).replaceAll(padding(level)+"$1$2");
    }

    public static String changeLocationImages(String text) {
        String regex = "()(!\\[.*?\\]\\()([^http])(.+?)(\\))";
        return Pattern.compile(regex, Pattern.MULTILINE).matcher(text).replaceAll("$1$2http://zestedesavoir.com/$3$4$5");
    }

    public static Container getContainerOfMetaAttribute(Container c, MetaAttribute meta) {
        if(c == null || meta == null) {
            return null;
        }
        if(meta.equals(c.getIntroduction()) || meta.equals(c.getConclusion())) {
            return c;
        } else {
            for(MetaContent ch:c.getChildren()) {
                if(ch instanceof Container) {
                    Container result = getContainerOfMetaAttribute((Container) ch, meta);
                    if(result != null) {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    public static void addTheming(Pane pane) {
        Theme forcedTheme = Theme.getActiveTheme();
        if(forcedTheme == null ) {
            pane.getStylesheets().add(MainApp.class.getResource("css/" + MainApp.config.getDisplayTheme()).toExternalForm());
        } else {
            pane.getStylesheets().add(MainApp.class.getResource("css/" + forcedTheme.getFilename()).toExternalForm());
        }
    }

    public static String getUniqueFilePath(String path, String ext) {
        String realLocalPath = path + "." + ext;
        File file = new File(realLocalPath);
        int i = 1;
        while(file.exists()){
            realLocalPath = path + "-" + i + "." + ext;
            file = new File(realLocalPath);
            i++;
        }
        return file.getAbsolutePath();
    }

    public static String getUniqueDirPath(String path) {
        String realLocalPath = path;
        File file = new File(realLocalPath);
        int i = 1;
        while(file.exists()){
            realLocalPath = path + "-" + i;
            file = new File(realLocalPath);
            i++;
        }
        return file.getAbsolutePath();
    }

    public static void switchContent(Content content, ObservableList<Content> contents) {
        if(!contents.isEmpty()) {
            contents.set(0, content);
        } else {
            contents.add(content);
        }

    }

    public static void openFindReplaceDialog(StyleClassedTextArea sourceText) {
        FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/FindReplaceDialog.fxml"));

        Stage dialogStage = new CustomStage(loader, Configuration.bundle.getString("ui.dialog.find.title"));
        dialogStage.setAlwaysOnTop(true);
        dialogStage.initModality(Modality.NONE);
        dialogStage.setTitle(Configuration.bundle.getString("ui.dialog.find.title"));
        dialogStage.setResizable(false);

        FindReplaceDialog findReplaceDialog = loader.getController();
        findReplaceDialog.setSourceText(sourceText);

        dialogStage.show();
    }

    public static String getNumberOfTextualReadMinutes(String text) {
        Double mins = Readability.getNumberOfReadMinutes(text);
        int[] steps = new int[]{1, 2, 5, 10, 15, 20, 30, 40, 60, 90, 120};
        for(int step:steps) {
            StringBuilder sb = new StringBuilder();
            if(mins < step) {
                if(step == steps[0]) {
                    sb.append(Configuration.bundle.getString("ui.label.lessof"));
                } else if(step == steps[steps.length-1]) {
                    sb.append(Configuration.bundle.getString("ui.label.moreof"));
                }

                if (step < 60) {
                    sb.append(" ").append(step).append(" ");
                    sb.append(Configuration.bundle.getString("ui.label.time.minute"));
                    if (step > 1) sb.append("s");
                } else {
                    int value = step / 60;
                    sb.append(" ").append(value).append(" ");
                    sb.append(Configuration.bundle.getString("ui.label.time.hour"));
                    if (value > 1) sb.append("s");
                }
                return sb.toString();
            }
        }
        return "Too long";
    }

    public static void generateMetadataAttributes(String file) {
        performGenerateMetadataAttributes(new File (file, Constant.DEFAULT_INTRODUCTION_FILENAME));
        performGenerateMetadataAttributes(new File (file, Constant.DEFAULT_CONCLUSION_FILENAME));
    }

    public static void generateMetadataAttributes(Container container) {
        performGenerateMetadataAttributes(new File (container.getIntroduction().getFilePath()));
        performGenerateMetadataAttributes(new File (container.getConclusion().getFilePath()));
    }

    public static void performGenerateMetadataAttributes(File file) {
        try {
            if(!file.exists ()) {
                if(!file.createNewFile ()) {
                    MainApp.getLogger().error("Impossible de créer le fichier "+file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            MainApp.getLogger().error(e.getMessage(), e);
        }
    }

}
