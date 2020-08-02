package com.zds.zw.view.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.zw.model.Content;
import com.zds.zw.utils.Configuration;
import com.zds.zw.utils.ZdsHttp;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DownloadZdsService extends Service<Content>{
    String url;
    String offlineFolder;
    String onlineFolder;
    Logger logger;

    public DownloadZdsService(String url, String offlineFolder, String onlineFolder) {
        this.url = url;
        this.offlineFolder = offlineFolder;
        this.onlineFolder = onlineFolder;
        this.logger = LoggerFactory.getLogger(getClass ());
    }

    @Override
    protected Task<Content> createTask() {
        return new Task<Content>() {
            @Override
            protected Content call() throws Exception {
                String[] elts = url.split ("/");
                if (elts.length > 5) {
                    String type = elts[3];
                    String id = elts[4];
                    String slug = elts[5];
                    logger.debug ("type : " + type);
                    logger.debug ("id : " + id);
                    logger.debug ("slug : " + slug);
                    updateMessage (Configuration.getBundle().getString("ui.dialog.download.zds.message.downloading"));
                    String filePath = ZdsHttp.getZdsZipball (id, slug, type, onlineFolder);
                    //String filePath ="";
                    updateMessage (Configuration.getBundle().getString("ui.dialog.download.zds.message.unpacking"));
                    File folder = ZdsHttp.unzipOnlineContent (filePath, offlineFolder);
                    // get folder in unzip folder
                    if (folder.isDirectory()) {
                        logger.info ("(Zds import) Répertoire cible : " + folder.getAbsolutePath ());
                        ObjectMapper mapper = new ObjectMapper();
                        File manifest = new File(folder.getAbsolutePath() + File.separator + "manifest.json");
                        Content c = mapper.readValue(manifest, Content.class);
                        c.setRootContent(c, folder.getAbsolutePath());
                        updateMessage (Configuration.getBundle().getString("ui.dialog.download.zds.message.done"));
                        return c;
                    }
                }
                throw new IOException ();
            }
        };
    }
}
