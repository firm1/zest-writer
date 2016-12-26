package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class UploadContentService extends Service<Void>{
	private final Logger logger;
    private Content content;
	private Optional<Pair<String, MetadataContent>> result;

	public UploadContentService(Optional<Pair<String, MetadataContent>> result, Content content) {
		this.result = result;
        this.content = content;
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (MainApp.getZdsutils().isAuthenticated() && result.isPresent()) {
                    String targetId = result.get().getValue().getId();
                    String targetSlug = result.get().getValue().getSlug();

                    String pathDir = content.getFilePath ();
                    updateMessage(Configuration.getBundle().getString("ui.task.zip.label")+" : "+targetSlug+" "+Configuration.getBundle().getString("ui.task.pending.label")+" ...");
                    ZipUtil.pack(new File(pathDir), new File(pathDir + ".zip"));
                    updateMessage(Configuration.getBundle().getString("ui.task.import.label")+" : "+targetSlug+" "+Configuration.getBundle().getString("ui.task.pending.label")+" ...");
                    if(result.get().getValue().getType() == null) {
                        if(!MainApp.getZdsutils().importNewContent(pathDir+ ".zip", result.get().getKey())) {
                            throw new IOException();
                        }
                    } else {
                        if(!MainApp.getZdsutils().importContent(pathDir + ".zip", targetId, targetSlug, result.get().getKey())) {
                            throw new IOException();
                        }
                    }

                    updateMessage(Configuration.getBundle().getString("ui.task.content.sync")+" ...");
                    try {
                        MainApp.getZdsutils().getContentListOnline().clear();
                        MainApp.getZdsutils().initInfoOnlineContent("tutorial");
                        MainApp.getZdsutils().initInfoOnlineContent("article");
                    } catch (IOException e) {
                        logger.error("Echec de téléchargement des metadonnés des contenus en ligne", e);
                    }
                }
                return null;
            }
        };
    }
}
