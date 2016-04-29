package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.events.WindowEvents;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Plugin{
    private MainApp mainApp;
    private ArrayList<Class> listenerClass = new ArrayList<>();
    private Class plugin;
    private boolean enabled = false;
    private boolean pluginError = false;


    public Plugin(MainApp mainApp){
        this.mainApp = mainApp;
        pluginError = true;
    }

    public Plugin(MainApp mainApp, Class plugin){
        this.mainApp = mainApp;
        this.plugin = plugin;

        listenerClass.add(plugin);
    }

    public void enable(){
        enabled = true;
        System.out.println("Version: " + method("getVersion").toString());
        method("onEnable");
        listenerClass = (ArrayList<Class>)method("getListener");

        if(!listenerClass.contains(plugin)){
            listenerClass.add(plugin);
        }
    }

    public void disable(){
        method("onDisable");
        System.out.println("Invoke disable");
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
                return methodInvoke.invoke(instance);
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
                return methodInvoke.invoke(instance, value);
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
}
