package com.zestedesavoir.zestwriter.plugins;


abstract public class Plugin{
    abstract public void onEnable();

    abstract public void onDisable();

    abstract public void getVersion();
}
