package com.zestedesavoir.zestwriter.view.com;

import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class CustomFXMLLoader extends FXMLLoader{

    public CustomFXMLLoader(URL location) {
        super(location);
        this.setResources(Configuration.bundle);
    }

    @Override
    public <T> T load() throws IOException {
        T pane= super.load();
        if(pane instanceof Pane) {
            FunctionTreeFactory.addTheming((Pane) pane);
        }
        return pane;
    }
}
