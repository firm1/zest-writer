/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zds.zw.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 *
 * @author fdambrine
 */
@FunctionalInterface
public interface StorageSaver {
    String getBaseDirectory();

    static void deleteFile(File file) {
        Logger logger = LoggerFactory.getLogger(StorageSaver.class);

        if(file.isDirectory()) {
            if(file.list().length==0) {
                if(file.delete()) {
                    logger.debug("Répertoire " + file.getAbsolutePath() + " Supprimé");
                } else {
                    logger.error("Impossible de supprimer le répertoire "+file.getAbsolutePath());
                }
            }
            else {
                String[] files = file.list();
                for(String temp: files != null ? files : new String[0]) {
                    File fileDelete = new File(file, temp);
                    deleteFile(fileDelete);
                }
                if(file.list().length==0) {
                    if(file.delete()) {
                        logger.debug("Répertoire " + file.getAbsolutePath() + " Supprimé");
                    } else {
                        logger.error("Impossible de supprimer le répertoire "+file.getAbsolutePath());
                    }
                }
            }
        } else {
            if(file.delete()) {
                logger.debug("Fichier " + file.getAbsolutePath() + " Supprimé");
            } else {
                logger.error("Impossible de supprimer le fichier "+file.getAbsolutePath());
            }
        }
    }
}
