package com.zestedesavoir.zestwriter.view.com;

import com.kenai.jffi.Main;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Container;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.ContentNode;
import com.zestedesavoir.zestwriter.model.Extract;
import com.zestedesavoir.zestwriter.model.MetaAttribute;
import com.zestedesavoir.zestwriter.model.MetaContent;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.view.dialogs.EditContentDialog;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

public class FunctionTreeFactory {

    public static boolean isMacOs() {
        return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
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

    public static boolean clearContent(ObservableMap<Textual, Tab> extracts, TabPane editorList) {
        for(Entry<Textual, Tab> entry:extracts.entrySet()) {
            Platform.runLater(() -> {
                Event.fireEvent(entry.getValue(), new Event(Tab.TAB_CLOSE_REQUEST_EVENT));
            });
        }
        if(editorList.getTabs().size() <= 1) {
            extracts.clear();
            return true;
        } else {
            return false;
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
        pane.getStylesheets().add(MainApp.class.getResource("css/"+ MainApp.config.getDisplayTheme()).toExternalForm());
    }
}
