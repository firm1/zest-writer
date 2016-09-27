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
public interface StorageSaver {
    String getBaseDirectory();

    void deleteFile(File file);
}
