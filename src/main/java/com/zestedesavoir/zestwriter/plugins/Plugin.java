package com.zestedesavoir.zestwriter.plugins;


import com.kenai.jffi.Main;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.events.WindowEvents;
import com.zestedesavoir.zestwriter.view.MdConvertController;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class Plugin{
    private MainApp mainApp;
    private ArrayList<Class> listenerClass = new ArrayList<>();
    private ZwPlugin zwPlugin;
    private Class plugin;
    private Logger logger;
    private String name;
    private boolean enabled = false;
    private boolean pluginError = false;


    public Plugin(MainApp mainApp, String name){
        logger = LoggerFactory.getLogger(Plugin.class);

        this.mainApp = mainApp;
        this.name = name;
        pluginError = true;
    }

    public Plugin(MainApp mainApp, String name, Class plugin){
        logger = LoggerFactory.getLogger(Plugin.class);

        this.mainApp = mainApp;
        this.name = name;
        this.plugin = plugin;

        listenerClass.add(plugin);
    }

    public void enable(){
        logger.debug("[PLUGINS] Enable <" + name + ">");

        enabled = true;
        logger.debug("[PLUGINS]   Call <onDefine> method");
        zwPlugin = (ZwPlugin)method("onDefine");

        if(zwPlugin == null)
            System.err.println("zwPlugin vaut NULL");

        logger.debug("[PLUGINS]   Version: " + method("getVersion"));


        logger.debug("[PLUGINS]   Call <getListener> method");
        listenerClass = (ArrayList<Class>)method("getListener");

        if(listenerClass == null){
            pluginError = true;
        }else{
            if(! listenerClass.contains(plugin)){
                listenerClass.add(plugin);
            }
        }

        logger.debug("[PLUGINS]   Call <onEnable> method of plugin");
        method("onEnable", new Class[]{MainApp.class}, mainApp);
    }

    public void disable(){
        logger.debug("[PLUGINS] Disable <" + plugin.getName() + ">");
        logger.debug("[PLUGINS]   Call <onDisable> method");

        method("onDisable");
        enabled = false;
    }

    public Object method(String method){
        if(!enabled || pluginError)
            return null;

        for(Class listener : listenerClass){
            try{
                Method methodInvoke;
                methodInvoke = listener.getDeclaredMethod(method);
                Object instance = listener.newInstance();
                Object result = methodInvoke.invoke(instance);
                System.out.println("Return .. " + method);
                return result;
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException | InstantiationException | InvocationTargetException e){
                e.printStackTrace();
            }
        }

        return null;
    }

    public Object method(String method, Object... value){
        /**
         * Cette méthode ne fonctionne pas, voir pourquoi car elle serait nettement plus pratique !
         */

        if(!enabled || pluginError)
            return null;

        Class[] type = new Class[value.length];

        for(int i = 0;i < value.length;i++){
            System.out.println(i + " -- " + value[i].getClass().getTypeName());
            type[i] = value[i].getClass();
            System.out.println("-- " + type[i].getTypeName() + " ---- " + value.getClass().getTypeName());
        }

        for(Class listener : listenerClass){
            try{
                Method methodInvoke;
                methodInvoke = listener.getDeclaredMethod(method, type);
                Object instance = listener.newInstance();
                return methodInvoke.invoke(instance, value);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException | InstantiationException | InvocationTargetException e){
                e.printStackTrace();
            }
        }

        return null;
    }

    public Object method(String method, Class[] type, Object... value){
        if(!enabled || pluginError)
            return null;

        for(Class listener : listenerClass){
            try{
                Method methodInvoke;
                methodInvoke = listener.getDeclaredMethod(method, type);
                Object instance = listener.newInstance();
                Object result = methodInvoke.invoke(instance, value);
                System.out.println("Return .. " + method);
                return result;
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException | InstantiationException | InvocationTargetException e){
                e.printStackTrace();
            }
        }

        return null;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setEnabled(boolean enabled){
        this.enabled = !pluginError && enabled;
    }

    public void setEditor(MdConvertController editor){
        zwPlugin.setEditor(editor);
    }

    public void setName(String name){
        this.name = name;
    }
}
