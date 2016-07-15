package com.zestedesavoir.zestwriter.view.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.GithubHttp;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DownloadGithubService extends Service<Content>{
    String url;
    String offlineFolder;
    String onlineFolder;
    Logger logger;

    public DownloadGithubService(String url, String offlineFolder, String onlineFolder) {
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
                if (elts.length > 4) {
                    String owner = elts[3];
                    String repo = elts[4];
                    updateMessage ("Téléchargement du contenu sur Github ...");
                    String filePath = GithubHttp.getGithubZipball (owner, repo, onlineFolder);
                    updateMessage ("Décompression de l'archive téléchargée");
                    File folder = GithubHttp.unzipOnlineContent (filePath, offlineFolder);
                    logger.info ("Répertoire à analyser : " + folder.getAbsolutePath ());
                    // get folder in unzip folder
                    File[] listFolder = folder.listFiles ();
                    if (listFolder.length > 0) {
                        if (listFolder[0].isDirectory ()) {
                            File target = listFolder[0];
                            logger.info ("Répertoire cible : " + target.getAbsolutePath ());
                            updateMessage ("Création du fichier manifest ...");
                            Content c = GithubHttp.loadManifest (target.getAbsolutePath (), owner, repo);

                            ObjectMapper mapper = new ObjectMapper ();
                            File manifest = new File (target, "manifest.json");
                            logger.info ("Tentative de création du fichier : " + manifest.getAbsolutePath ());
                            mapper.writerWithDefaultPrettyPrinter ().writeValue (manifest, c);
                            updateMessage ("Fin du téléchargement !");
                            return c;
                        }
                    }
                }
                throw new IOException ();
            }
        };
    }
}
