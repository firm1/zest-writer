/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.utils;

import java.io.File;
import java.io.IOException;


/**
 *
 * @author fdambrine
 */
public interface StorageSaver {
    String getBaseDirectory();
    boolean isStorageCurrentlyWritable();
    boolean isStorageCurrentlyReadable();
    void saveDirectory(String subdirectory) throws SecurityException;
    void saveFile(String fpath, String content) throws SecurityException, IOException;
}
