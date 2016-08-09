package com.zestedesavoir.zestwriter.utils.api;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.contents.internal.ContentsConfig;
import com.zestedesavoir.zestwriter.view.dialogs.ContentsDialog;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ApiInstaller implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(ApiInstaller.class);
    private ArrayList<ApiInstallerListener> listeners = new ArrayList<>();
    private Status status;

    private ContentsDialog.ContentType contentType;
    private ApiContentResponse content;
    private File fileContent;
    private File fileData;

    public enum Status{
        ERROR,
        SUCCESS
    }

    public ApiInstaller(ContentsDialog.ContentType contentType, ApiContentResponse content, File fileContent, File fileData){
        this.contentType = contentType;
        this.content = content;
        this.fileContent = fileContent;
        this.fileData = fileData;

        if(!fileContent.exists() || !fileData.exists()){
            logger.error("The fileContent or fileData for installation doesn't exist -> Content: " + fileContent.getPath() + " - Data: " + fileData.getPath());
            onError();
            return;
        }

        install();
    }

    public ApiInstaller(ContentsDialog.ContentType contentType, ApiContentResponse content, String fileContentLocation, String fileDataLocation){
        this(contentType, content, new File(fileContentLocation), new File(fileDataLocation));
    }

    public void addListener(ApiInstallerListener listener){
        listeners.add(listener);
    }

    private void install(){
        Thread t = new Thread(this);
        t.start();
    }

    private void onError(){
        status = Status.ERROR;
    }

    private void onSuccess(){
        if(status != Status.ERROR)
            listeners.forEach(ApiInstallerListener::onInstallSuccess);
    }

    private void onStarting(){
        listeners.forEach(ApiInstallerListener::onInstallStarting);
    }

    private void onEnding(){
        if(status == Status.ERROR)
            listeners.forEach(ApiInstallerListener::onInstallError);

        listeners.forEach(ApiInstallerListener::onInstallEnding);
    }

    public static boolean uninstall(ContentsDialog.ContentType contentType, File fileContent, File fileData){
        logger.info("---STARTING UNINSTALL CONTENT : " + fileContent.getName() + "---");
        if(!fileContent.exists() || !fileData.exists())
            return false;

        if(!fileContent.delete())
            logger.error("Failed for delete file -> " + fileContent.getPath());
        if(!fileData.delete())
            logger.error("Failed for delete file -> " + fileData.getPath());

        logger.info("---ENDING UNINSTALL CONTENT : " + fileContent.getName() + "---");
        return ! (fileContent.exists() || fileData.exists());
    }

    @Override
    public void run(){
        logger.info("---STARTING INSTALL CONTENT : " + content.getName() + "---");
        onStarting();

        String dir = null;
        String ext = null;
        if(contentType == ContentsDialog.ContentType.PLUGIN){
            dir = "/plugins/";
            ext = "jar";
        }else if(contentType == ContentsDialog.ContentType.THEME){
            dir = "/themes/";
            ext = "css";
        }
        else
            logger.error("Error in content type");

        File sourceContent = new File(fileContent.getPath());
        File destinationContent = new File(MainApp.getConfig().getContentsPath() + dir + content.getUrl_id() + "." + ext);
        File sourceData = new File(fileData.getPath());
        File destinationData = new File(MainApp.getConfig().getContentsPath() + dir + content.getUrl_id() + ".data");

        try{
            logger.debug("Move content file from <" + sourceContent.getPath() + "> to <" + destinationContent.getPath() + ">");
            FileUtils.moveFile(sourceContent, destinationContent);
        }catch(IOException e){
            logger.error(e.getMessage(), e);
            onError();
        }

        try{
            logger.debug("Move data file from <" + sourceData.getPath() + "> to <" + destinationData.getPath() + ">");
            FileUtils.moveFile(sourceData, destinationData);
        }catch(IOException e){
            logger.error(e.getMessage(), e);
            onError();
        }

        if(sourceContent.exists() || sourceData.exists() || !destinationContent.exists() || !destinationData.exists()){
            onError();
            logger.error("An error has been detected, try remove all file");

            if(sourceContent.exists())
                if(!sourceContent.delete()) logger.error("Error for remove file <" + sourceContent.getPath() + ">");
            if(sourceData.exists())
                if(!sourceData.delete()) logger.error("Error for remove file <" + sourceContent.getPath() + ">");
            if(destinationContent.exists())
                if(!destinationContent.delete()) logger.error("Error for remove file <" + sourceContent.getPath() + ">");
            if(destinationData.exists())
                if(!destinationData.delete()) logger.error("Error for remove file <" + sourceContent.getPath() + ">");
        }

        logger.info("---ENDING INSTALL CONTENT : " + content.getName() + "---");
        onSuccess();
        onEnding();
    }
}
