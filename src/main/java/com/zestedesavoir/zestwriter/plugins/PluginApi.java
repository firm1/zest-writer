package com.zestedesavoir.zestwriter.plugins;


import com.zestedesavoir.zestwriter.view.MenuController;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PluginApi{
    private static Logger logger = LoggerFactory.getLogger(MenuController.class);
    private static String apiUrl = "http://zw.winxaito.com/api/";

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

    public PluginApi(){
        try{
            request(new URL(apiUrl + "plugins"), RequestMethod.GET);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
    }

    public StringBuilder request(URL url, RequestMethod method){
        StringBuilder response = new StringBuilder();

        try{
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

                logger.debug("Content: " + response);
            }else{
                logger.error("Response error, code: " + code);
            }

            connection.disconnect();
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }

        return response;
    }
}
