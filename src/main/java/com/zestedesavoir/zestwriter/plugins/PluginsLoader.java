package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginsLoader{
    private MainApp mainApp;
    private Configuration config;
    private ArrayList<Plugin> plugins = new ArrayList<>();

    public PluginsLoader(MainApp mainApp){
        this.mainApp = mainApp;
        this.config = this.mainApp.getConfig();
    }

    public ArrayList<Plugin> getPlugins(){
        File pluginsFile[];

        File pluginFolder = new File(config.getPluginsPath());
        pluginsFile = pluginFolder.listFiles();


        if(pluginsFile != null){
            System.out.println("---Start List plugins---");
            for(File pluginFile : pluginsFile){
                System.out.println(pluginFile.getName());
            }
            System.out.println("---End List plugins---");

            String mainClass = "";
            URL[] url = new URL[1];

            for(File pluginFile : pluginsFile){
                try{
                    url[0] = new URL("file:///" + pluginFile.getPath());
                }catch(MalformedURLException e){
                    e.printStackTrace();
                }

                JarFile jarFile = null;
                try{
                    jarFile = new JarFile(pluginFile);

                    Manifest manifest = jarFile.getManifest();
                    Attributes attrs = manifest.getMainAttributes();

                    for(Object o : attrs.keySet()){
                        Attributes.Name attrName = (Attributes.Name)o;
                        String attrValue = attrs.getValue(attrName);

                        if(Objects.equals(attrName.toString(), "Main-Class"))
                            mainClass = attrValue;
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }


                if(! mainClass.isEmpty()){
                    try{
                        System.out.println(mainClass + " - (MainClass)");
                        URLClassLoader child = new URLClassLoader(url, this.getClass().getClassLoader());
                        Class classToLoad = Class.forName(mainClass, true, child);

                        Plugin plugin = new Plugin(mainApp, classToLoad);
                        plugins.add(plugin);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Plugin error");
                    alert.setHeaderText("Plugin");
                    alert.setContentText("Unable to load <" + pluginFile.getName() + ">, the Main-Class has not ben founded in Manifest file");
                }
            }

            return plugins;
        }else{
            System.out.println("No plugins founded");
        }

        return new ArrayList<>();
    }
}
