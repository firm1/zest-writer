/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.utils;

import java.io.File;

/**
 *
 * @author fdambrine
 */
public class LocalDirectorySaver implements StorageSaver{
    private String baseDirectory;
    private File baseDirectoryDescriptor;

    public LocalDirectorySaver(String baseDirectory)throws RuntimeException{
        this.baseDirectory = baseDirectory;
        openDirCreateIfNecessary();
    }

    @Override
    public String getBaseDirectory() {
        return this.baseDirectory;
    }

    private void openDirCreateIfNecessary(){
        this.baseDirectoryDescriptor = new File(baseDirectory);
        if(!this.baseDirectoryDescriptor.exists() && !this.baseDirectoryDescriptor.mkdirs()){
            throw new RuntimeException("Could not create " + baseDirectory);
        }
    }

}
