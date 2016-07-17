package com.zestedesavoir.zestwriter.contents.plugins.internal;

import java.util.ArrayList;

public class PluginConfigJson{
    private String pluginSystemVersion;
    private ArrayList<PluginConfigDetailJson> plugins = new ArrayList<>();

    public PluginConfigJson(){
        pluginSystemVersion = "0.0.0";
        plugins.add(new PluginConfigDetailJson());
        plugins.add(new PluginConfigDetailJson());
    }

    public String getPluginSystemVersion(){
        return pluginSystemVersion;
    }

    public void setPluginSystemVersion(String pluginSystemVersion){
        this.pluginSystemVersion = pluginSystemVersion;
    }

    public ArrayList<PluginConfigDetailJson> getPlugins(){
        return plugins;
    }

    public void setPlugins(ArrayList<PluginConfigDetailJson> plugins){
        this.plugins = plugins;
    }
}
