/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 * @author fdambrine
 */
public class LocalDirectorySaver implements StorageSaver{
    Logger logger = LoggerFactory.getLogger(LocalDirectorySaver.class);

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

    @Override
    public void deleteFile(File file) {
        if(file.isDirectory()) {
            if(file.list().length==0) {
                file.delete();
                logger.debug("Répertoire "+file.getAbsolutePath()+" Supprimé");
            }
            else {
                String files[] = file.list();
                for(String temp:files) {
                    File fileDelete = new File(file, temp);
                    deleteFile(fileDelete);
                }
                if(file.list().length==0) {
                    file.delete();
                    logger.debug("Répertoire "+file.getAbsolutePath()+" Supprimé");
                }
            }
        } else {
            file.delete();
            logger.debug("Fichier "+file.getAbsolutePath()+" Supprimé");
        }
    }

    private void openDirCreateIfNecessary(){
        this.baseDirectoryDescriptor = new File(baseDirectory);
        if(!this.baseDirectoryDescriptor.exists() && !this.baseDirectoryDescriptor.mkdirs()){
            throw new RuntimeException("Could not create " + baseDirectory);
        }
    }

}
