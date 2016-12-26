package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExportPdfService extends Service<Void>{

    private final Logger logger;
    private File fileDest;
    private Content content;
    private HttpClient client;
    private File markdownFile;
    HttpPost post;

    public ExportPdfService(String urlProvider, Content content, File fileDest) {
        this.fileDest = fileDest;
        this.content = content;
        logger = LoggerFactory.getLogger(getClass());
        markdownFile = new File(System.getProperty("java.io.tmpdir"), ZdsHttp.toSlug(content.getTitle()) + ".md");

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(500);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setConnectionManager(cm).build();
        FileBody cbFile = new FileBody(markdownFile);

        post = new HttpPost(urlProvider);
        logger.debug("Création du formulaire");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", cbFile);
        post.setEntity(builder.build());
        logger.debug("Exécution de la requête. Protocol : '"+post.getProtocolVersion()+"' Uri : '"+post.getURI()+"' Method : '"+post.getMethod());
    }



    public File getMarkdownFile() {
        return markdownFile;
    }

    public Service<Void> getThis() {
        return this;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            public boolean downloadPdf() {
                logger.debug("Tentative de téléchargement du contenu au format Pdf");
                try(FileOutputStream fos = new FileOutputStream(fileDest)) {
                    updateMessage(Configuration.getBundle().getString("ui.task.export.prepare.label")+" ...");
                    HttpResponse response = client.execute(post);
                    logger.debug("Début du traitement de la réponse");
                    if(response.getStatusLine().getStatusCode() >= 400) {
                        return false;
                    }
                    InputStream is = response.getEntity().getContent();
                    long max = response.getEntity().getContentLength();
                    long remain = 0;
                    updateProgress(remain, max);
                    int inByte;
                    updateMessage(Configuration.getBundle().getString("ui.task.export.build.label"));
                    while ((inByte = is.read()) != -1) {
                        fos.write(inByte);
                        remain++;
                        updateProgress(remain, max);
                    }
                    updateProgress(max, max);
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                    return false;
                } catch (UnsupportedOperationException e) {
                    logger.error(e.getMessage(),e);
                    return false;
                }

                logger.info("Pdf téléchargé avec succès dans "+fileDest.getAbsolutePath());
                return true;
            }

            @Override
            protected Void call() throws Exception {
                updateMessage(Configuration.getBundle().getString("ui.task.export.assemble.label"));
                content.saveToMarkdown(markdownFile);

                updateMessage(Configuration.getBundle().getString("ui.task.export.label"));
                if(!downloadPdf()) {
                    updateMessage(Configuration.getBundle().getString("ui.task.export.error"));
                    throw new IOException();
                }
                return null;
            }
        };
    }
}
