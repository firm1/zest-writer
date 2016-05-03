package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.app.AppEditorEvents;
import com.zestedesavoir.zestwriter.plugins.app.AppWindowEvents;
import com.zestedesavoir.zestwriter.view.MdConvertController;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PluginsManager{
    private MainApp mainApp;
    private Stage window;
    private MdConvertController editor;
    private PluginsLoader pluginsLoader;
    private ArrayList<Plugin> plugins = new ArrayList<>();

    public PluginsManager(MainApp mainApp){
        this.mainApp = mainApp;
        this.window = mainApp.getPrimaryStage();
        pluginsLoader = new PluginsLoader(mainApp);
        plugins = pluginsLoader.getPlugins();

        windowEvents();
        editorEvents();
    }

    public void setEditor(MdConvertController editor){
        this.editor = editor;

        for(Plugin plugin : plugins){
            plugin.setEditor(editor);
        }

        editorEvents();
    }

    public void enablePlugins(){
        plugins.forEach(Plugin::enable);
    }

    public void disablePlugins(){
        System.out.println("Disable plugin");
        plugins.forEach(Plugin::disable);
    }

    private void windowEvents(){
        new AppWindowEvents(mainApp, plugins);
    }

    private void editorEvents(){
        new AppEditorEvents(mainApp, plugins, editor);
    }
}
