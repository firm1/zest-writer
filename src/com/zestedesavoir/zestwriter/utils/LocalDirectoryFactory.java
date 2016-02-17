/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.utils;

import java.io.IOException;
import javafx.stage.Stage;

/**
 *
 * @author fdambrine
 */
public class LocalDirectoryFactory {

    private LocalDirectorySaver baseSaver;

    public LocalDirectoryFactory(String baseDirectory){
        baseSaver = new LocalDirectorySaver(baseDirectory);
    }
 
    public LocalDirectoryFactory(Stage windows) throws IOException{
        baseSaver = new FilePickerDirectorySaver(windows);
    }
    public String getWorkspaceDir(){
        return baseSaver.getBaseDirectory();
    }
    public LocalDirectorySaver getOnlineSaver() throws IOException{
        
        return new LocalDirectorySaver(baseSaver.getBaseDirectory() + "/online");
    }
    
    public LocalDirectorySaver getOfflineSaver() throws IOException{

        return new LocalDirectorySaver(baseSaver.getBaseDirectory() + "/offline");
    }
    
}
