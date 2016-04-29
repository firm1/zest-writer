package com.zestedesavoir.zestwriter.plugins.app;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.Plugin;
import javafx.stage.Stage;

import java.util.ArrayList;

public class AppWindowEvents{
    private MainApp mainApp;
    private Stage window;
    private ArrayList<Plugin> plugins;

    public AppWindowEvents(MainApp mainApp, ArrayList<Plugin> plugins){
        this.mainApp = mainApp;
        this.window = mainApp.getPrimaryStage();
        this.plugins = plugins;
        setMainWindowEvents();
    }

    private void setMainWindowEvents(){
        window.widthProperty().addListener((observable, oldValue, newValue) -> {
            for(Plugin plugin : plugins){
                plugin.method("WindowWidthResizeEvent", new Class[]{Double.TYPE, Double.TYPE}, oldValue.doubleValue(), newValue.doubleValue());
            }
        });
        window.heightProperty().addListener((observable, oldValue, newValue) -> {
            for(Plugin plugin : plugins){
                plugin.method("WindowHeightResizeEvent", new Class[]{Double.TYPE, Double.TYPE}, oldValue.doubleValue(), newValue.doubleValue());
            }
        });
        window.setOnCloseRequest(event -> {
            for(Plugin plugin : plugins){
                plugin.method("WindowCloseEvent");
            }
        });
        window.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            for(Plugin plugin : plugins){
                plugin.method("WindowMaximizedChangeEvent", new Class[]{Boolean.TYPE, Boolean.TYPE}, oldValue, newValue);
            }
        });
        window.focusedProperty().addListener((observable, oldValue, newValue) -> {
            for(Plugin plugin : plugins){
                plugin.method("WindowFocusChangeEvent", new Class[]{Boolean.TYPE, Boolean.TYPE}, oldValue, newValue);
            }
        });
    }
}
