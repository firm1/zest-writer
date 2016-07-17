package com.zestedesavoir.zestwriter.contents.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.contents.internal.ContentsConfig;
import com.zestedesavoir.zestwriter.contents.plugins.app.AppEditorEvents;
import com.zestedesavoir.zestwriter.contents.plugins.app.AppWindowEvents;
import com.zestedesavoir.zestwriter.view.MdConvertController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PluginsManager{
    private MainApp mainApp;
    private MdConvertController editor;
    private PluginsLoader pluginsLoader;
    private Logger logger;
    private ArrayList<Plugin> plugins = new ArrayList<>();

    public PluginsManager(MainApp mainApp){
        logger = LoggerFactory.getLogger(PluginsManager.class);
        this.mainApp = mainApp;

        pluginsLoader = new PluginsLoader(mainApp);
        plugins = pluginsLoader.getPlugins();

        windowEvents();
        editorEvents();
    }

    public void enablePlugins(){
        logger.info("[PLUGINS] Enable plugins");
        plugins.forEach(Plugin::enable);
    }

    public void disablePlugins(){
        logger.info("[PLUGINS] Disable plugins");
        plugins.forEach(Plugin::disable);
    }

    public void setPluginEditor(MdConvertController editor){
        AppEditorEvents.setEditor(editor);
    }

    private void windowEvents(){
        new AppWindowEvents(mainApp, plugins);
    }

    private void editorEvents(){
        new AppEditorEvents(plugins);
    }
}
