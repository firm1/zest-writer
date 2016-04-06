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
public class LocalDirectoryFactory {

    private LocalDirectorySaver baseSaver;

    public LocalDirectoryFactory(String baseDirectory){
        baseSaver = new LocalDirectorySaver(baseDirectory);
    }

    public String getWorkspaceDir(){
        return baseSaver.getBaseDirectory();
    }
    public LocalDirectorySaver getOnlineSaver() throws IOException{

        return new LocalDirectorySaver(baseSaver.getBaseDirectory() + File.separator + "online");
    }

    public LocalDirectorySaver getOfflineSaver() throws IOException{

        return new LocalDirectorySaver(baseSaver.getBaseDirectory() + File.separator + "offline");
    }

}
