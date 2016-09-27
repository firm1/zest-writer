/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.utils;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author fdambrine
 */
public class StorageSaver {
    static Logger logger = LoggerFactory.getLogger(LocalDirectorySaver.class);
    String getBaseDirectory() {return null;}

    public static void deleteFile(File file) {
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
}
