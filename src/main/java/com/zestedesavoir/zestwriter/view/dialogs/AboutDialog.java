package com.zestedesavoir.zestwriter.view.dialogs;


import javafx.fxml.FXML;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutDialog{
    @FXML
    private void HandleGplHyperlinkAction(){
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/firm1/zest-writer/blob/master/LICENSE"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void HandleSourceHyperlinkAction(){
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/firm1/zest-writer"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void HandleZdsHyperlinkAction(){
        try {
            Desktop.getDesktop().browse(new URI("https://zestedesavoir.com/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
