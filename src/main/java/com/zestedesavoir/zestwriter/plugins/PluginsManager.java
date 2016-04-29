package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.app.AppWindowEvents;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PluginsManager{
    private MainApp mainApp;
    private Stage window;
    private PluginsLoader pluginsLoader;
    private ArrayList<Plugin> plugins = new ArrayList<>();

    public PluginsManager(MainApp mainApp){
        this.mainApp = mainApp;
        this.window = mainApp.getPrimaryStage();
        pluginsLoader = new PluginsLoader(mainApp);
        plugins = pluginsLoader.getPlugins();

        event();
    }

    public void enablePlugins(){
        plugins.forEach(Plugin::enable);
    }

    public void disablePlugins(){
        plugins.forEach(Plugin::disable);
    }

    private void event(){
        new AppWindowEvents(mainApp, plugins);
    }
}
