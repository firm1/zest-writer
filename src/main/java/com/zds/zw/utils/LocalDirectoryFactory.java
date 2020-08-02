/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zds.zw.utils;

import java.io.File;

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
    public LocalDirectorySaver getOnlineSaver() {

        return new LocalDirectorySaver(baseSaver.getBaseDirectory() + File.separator + "online");
    }

    public LocalDirectorySaver getOfflineSaver() {

        return new LocalDirectorySaver(baseSaver.getBaseDirectory() + File.separator + "offline");
    }

}
