package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
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
    private ArrayList<Class> plugins = new ArrayList<>();

    public ArrayList<Class> getPlugins(){
        File pluginsFile[];
        try{
            System.out.println(MainApp.class.getResource("plugins/").getPath());
        }catch(URISyntaxException e){
            e.printStackTrace();
        }
        try{
            File pluginFolder = new File(MainApp.class.getResource("plugins").toURI());
            pluginsFile = pluginFolder.listFiles();
        }catch(URISyntaxException e){
            e.printStackTrace();
            return null;
        }

        System.out.println("---Start List plugins---");
        assert pluginsFile != null;
        for(File pluginFile : pluginsFile){
            System.out.println(pluginFile.getName());
        }
        System.out.println("---End List plugins---");

        String mainClass = "";
        URL[] url = new URL[1];
        url[0] = MainApp.class.getResource("FirstPlugin.jar");

        try{
            JarFile jarFile = new JarFile(new File(MainApp.class.getResource("FirstPlugin.jar").toURI()).getAbsolutePath());
            System.out.println(jarFile.toString());
            Manifest manifest = jarFile.getManifest();
            Attributes attrs = manifest.getMainAttributes();

            for(Object o : attrs.keySet()){
                Attributes.Name attrName = (Attributes.Name)o;
                String attrValue = attrs.getValue(attrName);

                if(Objects.equals(attrName.toString(), "Main-Class"))
                    mainClass = attrValue;
            }
        }catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }

        if(!mainClass.isEmpty()){
            try{
                URLClassLoader child = new URLClassLoader(url, this.getClass().getClassLoader());
                Class classToLoad = Class.forName(mainClass, true, child);

                plugins.add(classToLoad);
                return plugins;
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Plugin error");
            alert.setHeaderText("Plugin");
            alert.setContentText("Unable to load <FirstPlugin.jar>, the Main-Class has not ben founded in Manifest file");
        }

        return new ArrayList<>();
    }
}
