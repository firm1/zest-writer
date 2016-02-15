/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.utils;

import java.io.IOException;

/**
 *
 * @author fdambrine
 */
public class LocalDirectoryFactory {
    private String baseDirectory;

    public LocalDirectoryFactory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    public StorageSaver getOnlineSaver() throws IOException{
        
        return new LocalDirectorySaver(baseDirectory + "/online");
    }
    
    public StorageSaver getOfflineSaver() throws IOException{
        return new LocalDirectorySaver(baseDirectory + "/offline");
    }
    
}
