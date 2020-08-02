/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zds.zw.utils;

import com.zds.zw.MainApp;

import java.io.File;

/**
 *
 * @author fdambrine
 */
public class LocalDirectorySaver implements StorageSaver{
    private String baseDirectory;

    public LocalDirectorySaver(String baseDirectory){
        this.baseDirectory = baseDirectory;
        openDirCreateIfNecessary();
    }

    @Override
    public String getBaseDirectory() {
        return this.baseDirectory;
    }

    private void openDirCreateIfNecessary(){
        File baseDirectoryDescriptor = new File(baseDirectory);
        if(!baseDirectoryDescriptor.exists() && !baseDirectoryDescriptor.mkdirs()){
            MainApp.getLogger().error("Could not create " + baseDirectory);
        }
    }

}
