package com.zestedesavoir.zestwriter.utils.api;

import com.zestedesavoir.zestwriter.view.dialogs.ContentsDialog;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiDownloader implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(ApiDownloader.class);
    private List<ApiDownloaderListener> listeners = new ArrayList<>();

    private Stage owner;
    private Stage waitStage;

    private static final int MAX_BUFFER_SIZE = 1024;
    private URL urlContent;
    private URL urlData;
    private int sizeContent = -1;
    private int sizeData = -1;
    private int downloadedContent = 0;
    private int downloadedData = 0;
    private Status status;
    private File outputContentFile;
    private File outputDataFile;
    private String outputDirPath;
    private ApiContentResponse content;
    private ContentsDialog.ContentType contentType;

    public enum Status{
        WAIT,
        DOWNLOAD,
        PAUSE,
        COMPLETE,
        CANCELLED,
        ERROR
    }

    public ApiDownloader(ContentsDialog.ContentType contentType, String outputDirPath, URL urlContent, URL urlData){
        this.contentType = contentType;
        this.outputDirPath = outputDirPath;
        this.urlContent = urlContent;
        this.urlData = urlData;
        download();
    }

    public ApiDownloader(ContentsDialog.ContentType contentType, String outputDirPath, String urlStringContent, String urlStringData){
        this.contentType = contentType;
        this.outputDirPath = outputDirPath;

        try{
            this.urlContent = new URL(urlStringContent);
            this.urlData = new URL(urlStringData);
            download();
        }catch(MalformedURLException e){
            logger.error(e.getMessage(), e);
        }
    }

    public void addListener(ApiDownloaderListener listener){
        listeners.add(listener);
    }

    public void initOwner(Stage owner){
        this.owner = owner;
    }

    public ApiContentResponse getContent(){
        return content;
    }

    public void setContent(ApiContentResponse content){
        this.content = content;
    }

    private void download(){
        status = Status.DOWNLOAD;
        Thread t = new Thread(this);
        t.start();
    }

    public void pause(){
        status = Status.PAUSE;
        listeners.forEach(ApiDownloaderListener::onDownloadPaused);
    }

    public void resume(){
        status = Status.DOWNLOAD;
        listeners.forEach(ApiDownloaderListener::onDownloadResumed);
    }

    public URL getUrlContent(){
        return urlContent;
    }

    public URL getUrlData(){
        return urlData;
    }

    public float getProgress(){
        return ((float)downloadedContent / sizeContent) * 100;
    }

    public int getSizeContent(){
        return sizeContent;
    }

    public int getDownloadedContent(){
        return downloadedContent;
    }

    public Status getStatus(){
        return status;
    }

    public File getOutputContentFile(){
        return outputContentFile;
    }

    public File getOutputDataFile(){
        return outputDataFile;
    }

    public ContentsDialog.ContentType getContentType(){
        return contentType;
    }

    private void error(){
        logger.error("Error");
        status = Status.CANCELLED;


        listeners.forEach(ApiDownloaderListener::onDownloadError);
        listeners.forEach(ApiDownloaderListener::onDownloadCancelled);
    }

    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    @Override
    public void run(){
        logger.info("---STARTING DOWNLOAD CONTENT : " + content.getName() + "---");
        RandomAccessFile fileContent = null;
        InputStream streamContent = null;
        RandomAccessFile fileData = null;
        InputStream streamData = null;

        try{
            Thread.sleep(1500);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        try{
            HttpURLConnection connectionContent = (HttpURLConnection)urlContent.openConnection();
            HttpURLConnection connectionData = (HttpURLConnection)urlData.openConnection();

            connectionContent.setRequestProperty("Range", "bytes=" + downloadedContent + "-");
            connectionContent.connect();
            connectionData.setRequestProperty("Range", "bytes=" + downloadedContent + "-");
            connectionData.connect();

            if(connectionContent.getResponseCode() != 200)
                error();

            if(connectionData.getResponseCode() != 200)
                error();

            int contentLengthContent = connectionContent.getContentLength();
            int contentLengthData = connectionData.getContentLength();

            if(contentLengthContent < 1)
                error();

            if(contentLengthData < 1)
                error();

            if(sizeContent == -1)
                sizeContent = contentLengthContent;

            if(sizeData == -1)
                sizeData = contentLengthData;

            logger.debug("Download content file to: " + outputDirPath + getFileName(urlContent) + ".tmp");
            fileContent = new RandomAccessFile(outputDirPath + getFileName(urlContent) + ".tmp", "rw");
            fileContent.seek(downloadedContent);

            streamContent = connectionContent.getInputStream();

            //ContentFile
            while (status == Status.DOWNLOAD) {
                byte bufferContent[];
                if (sizeContent - downloadedContent > MAX_BUFFER_SIZE) {
                    bufferContent = new byte[MAX_BUFFER_SIZE];
                } else {
                    bufferContent = new byte[sizeContent - downloadedContent];
                }

                // Read from server into buffer.
                int read = streamContent.read(bufferContent);
                if (read == -1)
                    break;

                // Write buffer to file.
                fileContent.write(bufferContent, 0, read);
                downloadedContent += read;
            }

            logger.debug("Download data file to: " + outputDirPath + getFileName(urlData) + ".tmp");
            fileData = new RandomAccessFile(outputDirPath + getFileName(urlData) + ".tmp", "rw");
            fileData.seek(downloadedData);

            streamData = connectionData.getInputStream();

            //DataFile
            while (status == Status.DOWNLOAD) {
                byte bufferData[];
                if (sizeData - downloadedData > MAX_BUFFER_SIZE) {
                    bufferData = new byte[MAX_BUFFER_SIZE];
                } else {
                    bufferData = new byte[sizeData - downloadedData];
                }

                // Read from server into buffer.
                int read = streamData.read(bufferData);
                if (read == -1)
                    break;

                // Write buffer to file.
                fileData.write(bufferData, 0, read);
                downloadedData += read;
            }

            if(status == Status.DOWNLOAD){
                outputContentFile = new File(outputDirPath + getFileName(urlContent) + ".tmp");
                outputDataFile = new File(outputDirPath + getFileName(urlData) + ".tmp");

                status = Status.COMPLETE;
                listeners.forEach(ApiDownloaderListener::onDownloadSuccess);
            }else{
                outputContentFile = null;
                outputDataFile = null;

                status = Status.ERROR;
                listeners.forEach(ApiDownloaderListener::onDownloadError);
            }
        }catch(IOException e){
            error();
            logger.error(e.getMessage(), e);
        }finally {
            // Close Content file.
            if(fileContent != null){
                try{
                    fileContent.close();
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }

            // Close connection to server.
            if(streamContent != null){
                try{
                    streamContent.close();
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }

            // Close Data file.
            if(fileData != null){
                try{
                    fileData.close();
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }

            // Close connection to server.
            if(streamData != null){
                try{
                    streamData.close();
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }

        logger.info("---ENDING DOWNLOAD CONTENT : " + content.getName() + "---");
    }
}
