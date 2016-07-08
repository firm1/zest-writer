package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.view.MdConvertController;

import java.util.ArrayList;
import java.util.Collections;

public interface ZwPlugin{
    void onEnable(MainApp mainApp);

    void onDisable();

    ZwPluginVersion getVersion();

    ArrayList<Class> getListener();
}