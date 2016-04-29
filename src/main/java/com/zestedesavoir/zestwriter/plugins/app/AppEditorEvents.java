package com.zestedesavoir.zestwriter.plugins.app;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.Plugin;
import com.zestedesavoir.zestwriter.view.MdConvertController;
import com.zestedesavoir.zestwriter.view.MdTextController;
import javafx.stage.Stage;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;

public class AppEditorEvents{
    private MainApp mainApp;
    private MdConvertController editor;
    private StyleClassedTextArea sourceText;
    private ArrayList<Plugin> plugins;

    public AppEditorEvents(MainApp mainApp, ArrayList<Plugin> plugins, MdConvertController editor){
        this.mainApp = mainApp;
        this.plugins = plugins;
        this.editor = editor;

        if(editor != null){
            sourceText = editor.getSourceText();
            setEditorEvents();
        }
    }

    private void setEditorEvents(){
        sourceText.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            for(Plugin plugin : plugins){
                plugin.method("onEditorPositionChange", new Class[]{Integer.TYPE, Integer.TYPE}, oldValue, newValue);
            }
        });
        sourceText.textProperty().addListener((observable, oldValue, newValue) -> {
            for(Plugin plugin : plugins){
                plugin.method("onEditorTextChange", new Class[]{String.class, String.class}, oldValue, newValue);
            }
        });
    }
}
