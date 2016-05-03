package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.view.MdConvertController;

import java.util.ArrayList;
import java.util.Collections;

abstract public class ZwPlugin{
    private ArrayList<Class> listener = new ArrayList<>();
    protected MainApp mainApp;
    private MdConvertController editor;

    abstract public ZwPlugin onDefine();

    abstract public void onEnable();

    abstract public void onDisable();

    abstract public ZwPluginVersion getVersion();

    abstract public ArrayList<Class> getListener();

    public MainApp getMainApp(){
        return mainApp;
    }

    protected MdConvertController getEditor(){
        return editor;
    }

    abstract public void setMainApp(MainApp mainApp);

    public void setEditor(MdConvertController editor){
        this.editor = editor;
    }

    public ZwPlugin getZwPlugin(){
        return this;
    }
}
