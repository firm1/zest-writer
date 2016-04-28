package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.events.WindowEvents;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class PluginsManager{
    private MainApp mainApp;
    private PluginsLoader pluginsLoader;
    private ArrayList<Class> plugins = new ArrayList<>();

    public PluginsManager(MainApp mainApp){
        this.mainApp = mainApp;
        pluginsLoader = new PluginsLoader(mainApp);
        plugins = pluginsLoader.getPlugins();
    }

    public void enablePlugins(){
        for(Class plugin : plugins){
            Method method = null;
            try{
                method = plugin.getDeclaredMethod ("onEnable");
                Object instance = plugin.newInstance ();
                Object result = method.invoke (instance);
            }catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e){
                e.printStackTrace();
            }
        }
    }

    public void disablePlugins(){
        for(Class plugin : plugins){
            Method method = null;
            try{
                method = plugin.getDeclaredMethod ("onDisable");
                Object instance = plugin.newInstance ();
                Object result = method.invoke (instance);
            }catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e){
                e.printStackTrace();
            }
        }
    }
}
