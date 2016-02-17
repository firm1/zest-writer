/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zestedesavoir.zestwriter.utils;

import java.io.File;
import java.io.IOException;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author fdambrine
 */
public class FilePickerDirectorySaver extends LocalDirectorySaver{
    private Stage stage;
    public FilePickerDirectorySaver(Stage stage) throws IOException {
        super(null);
        this.stage = stage;
    }

    @Override
    public String getBaseDirectory() throws NullPointerException{
        if(super.getBaseDirectory() == null){
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle("Sélectionnez un dossier"); // perhaps someday will have a intl tool.
            File selectedDirectory = fileChooser.showDialog(stage);
            this.setBaseDirectory(selectedDirectory.getAbsolutePath()); // will throw NPE if no dir is selected
            
        }
        return super.getBaseDirectory(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
