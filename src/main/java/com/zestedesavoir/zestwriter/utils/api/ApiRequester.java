package com.zestedesavoir.zestwriter.utils.api;


import com.zestedesavoir.zestwriter.utils.api.ApiMapper;
import com.zestedesavoir.zestwriter.view.MenuController;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ApiRequester{
    private static Logger logger = LoggerFactory.getLogger(MenuController.class);
    private static String apiUrl = "http://zw.winxaito.com/api/";

    private boolean apiOk;

    public enum RequestMethod{
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        public String str;

        RequestMethod(String str){
            this.str = str;
        }
    }

    public ApiRequester(){
        if(apiTest() != 200){
            apiAlert();
            apiOk = false;
        }else{
            apiOk = true;
        }
    }

    public StringBuilder request(URL url, RequestMethod method){
        if(!apiOk){
            apiAlert();
            return null;
        }

        StringBuilder response = new StringBuilder();

        try{
            logger.debug("Request to : " + url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(method.str);
            connection.connect();

            int code = connection.getResponseCode();
            String out = connection.getResponseMessage();

            logger.debug("Response code: " + code);
            logger.debug("Response : " + out);

            if(code == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while((line = reader.readLine()) != null){
                    response.append(line);
                }
            }else{
                logger.error("Response error, code: " + code);
            }

            connection.disconnect();
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }

        return response;
    }

    public boolean isApiOk(){
        return apiOk;
    }

    public void setApiOk(boolean apiOk){
        this.apiOk = apiOk;
    }

    private void apiAlert(){
        Alert alert = new CustomAlert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle("Connexion à l'API");
        alert.setContentText("Une erreur est survenu lors de la connexion à l'API de zest-writer. Merci de signaler ce problème.");
        alert.showAndWait();
    }

    private int apiTest(){
        int code = 0;
        logger.debug("Try to connect to API: " + apiUrl);

        try{
            HttpURLConnection connection = (HttpURLConnection)new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            code = connection.getResponseCode();

            if(code == 200)
                logger.debug("Connexion to API : OK (Status -> 200)");
            else
                logger.error("Error for connect to API (" + apiUrl + ") (Status -> " + code + ")");
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }

        return code;
    }
}
