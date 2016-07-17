package com.zestedesavoir.zestwriter.contents.plugins;


import com.zestedesavoir.zestwriter.MainApp;

import java.util.ArrayList;

public interface ZwPlugin{
    void onEnable(MainApp mainApp);

    void onDisable();

    ZwPluginVersion getVersion();

    ArrayList<Class> getListener();
}