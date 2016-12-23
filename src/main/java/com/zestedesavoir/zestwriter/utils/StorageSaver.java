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
public interface StorageSaver {
    String getBaseDirectory();

    static void deleteFile(File file) {
        Logger logger = LoggerFactory.getLogger(StorageSaver.class);

        if(file.isDirectory()) {
            if(file.list().length==0) {
                file.delete();
                logger.debug("Répertoire "+file.getAbsolutePath()+" Supprimé");
            }
            else {
                String files[] = file.list();
                for(String temp: files != null ? files : new String[0]) {
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
}
