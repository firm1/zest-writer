package com.zestedesavoir.zestwriter.plugins;


import java.util.ArrayList;
import java.util.Collections;

abstract public class ZwPlugin{
    private ArrayList<Class> listener = new ArrayList<>();

    abstract public void onEnable();

    abstract public void onDisable();

    abstract public ZwPluginVersion getVersion();

    abstract public ArrayList<Class> getListener();
}
