package com.zestedesavoir.zestwriter.utils.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

public class ApiDownloader extends Observable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(ApiDownloader.class);

    private static final int MAX_BUFFER_SIZE = 1024;
    private URL url;
    private int size = -1;
    private int downloaded = 0;
    private Status status;

    public enum Status{
        WAIT,
        DOWNLOAD,
        PAUSE,
        COMPLETE,
        CANCELLED,
        ERROR
    }

    public ApiDownloader(URL url){
        this.url = url;
        download();
    }

    private void download(){
        status = Status.DOWNLOAD;
        Thread t = new Thread(this);
        t.start();
    }

    public void pause(){
        status = Status.PAUSE;
    }

    public void resume(){
        status = Status.DOWNLOAD;
    }

    public URL getUrl(){
        return url;
    }

    public float getProgress(){
        return ((float) downloaded / size) * 100;
    }

    public int getSize(){
        return size;
    }

    public int getDownloaded(){
        return downloaded;
    }

    public Status getStatus(){
        return status;
    }

    private void error(){
        logger.error("Error");
        status = Status.CANCELLED;
    }

    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    // Notify observers that this download's status has changed.
    private void stateChanged() {
        setChanged();
        notifyObservers();
    }

    @Override
    public void run(){
        RandomAccessFile file = null;
        InputStream stream = null;

        try{
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            connection.connect();

            if(connection.getResponseCode() != 200)
                error();

            int contentLength = connection.getContentLength();
            if(contentLength < 1)
                error();

            if(size == -1)
                size = contentLength;

            file = new RandomAccessFile(getFileName(url), "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == Status.DOWNLOAD) {
                byte buffer[];
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1)
                    break;

                // Write buffer to file.
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }

            if (status == Status.DOWNLOAD) {
                status = Status.COMPLETE;
                stateChanged();
            }
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }finally {
            // Close file.
            if(file != null){
                try{
                    file.close();
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }

            // Close connection to server.
            if(stream != null){
                try{
                    stream.close();
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
