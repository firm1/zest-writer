package com.zestedesavoir.zestwriter.contents.plugins.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;

import java.io.File;
import java.io.IOException;

public class PluginConfig{
    public PluginConfig(){
        ObjectMapper mapper = new ObjectMapper();
        PluginConfigJson configJson = new PluginConfigJson();

        try{
            mapper.writeValue(new File(MainApp.getConfig().getContentsPath() + "/config.data"), configJson);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
