package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.events.WindowEvents;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Plugin{
    private MainApp mainApp;
    private Stage window;
    private Class plugin;
    private Method[] windowEventsMethod = WindowEvents.class.getDeclaredMethods();

    public Plugin(MainApp mainApp, Class plugin){
        this.mainApp = mainApp;
        this.window = this.mainApp.getPrimaryStage();
        this.plugin = plugin;
    }

    public void enable(){
        method("onEnable");
    }

    public void disable(){
        method("onDisable");
    }

    public void method(String method){
        try{
            Method methodInvoke;
            methodInvoke = plugin.getDeclaredMethod(method);
            Object instance = plugin.newInstance();
            Object result = methodInvoke.invoke(instance);
        }catch(NoSuchMethodException e){
            System.out.println("No such method " + method);
        }catch(IllegalAccessException | InstantiationException | InvocationTargetException e){
            e.printStackTrace();
        }
    }

    public void method(String method, Object... value){
        /**
         * Cette méthode ne fonctionne pas, voir pourquoi car elle serait nettement plus pratique !
         */

        Class[] type = new Class[value.length];

        for(int i = 0;i < value.length;i++){
            System.out.println(i + " -- " + value[i].getClass().getTypeName());
            type[i] = value[i].getClass();
            System.out.println("-- " + type[i].getTypeName() + " ---- " + value.getClass().getTypeName());
        }

        try{
            Method methodInvoke;
            methodInvoke = plugin.getDeclaredMethod(method, type);
            Object instance = plugin.newInstance();
            Object result = methodInvoke.invoke(instance, value);
        }catch(NoSuchMethodException e){
            System.out.println("No such method " + method);
        }catch(IllegalAccessException | InstantiationException | InvocationTargetException e){
            e.printStackTrace();
        }
    }

    public void method(String method, Class[] type, Object... value){
        try{
            Method methodInvoke;
            methodInvoke = plugin.getDeclaredMethod(method, type);
            Object instance = plugin.newInstance();
            Object result = methodInvoke.invoke(instance, value);
        }catch(NoSuchMethodException e){
            System.out.println("No such method " + method);
        }catch(IllegalAccessException | InstantiationException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
