package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
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
	private ZdsHttp zdsUtils;
	private final Logger logger;
	private Optional<Pair<String, MetadataContent>> result;

	public UploadContentService(ZdsHttp zdsUtils, Optional<Pair<String, MetadataContent>> result) {
		this.zdsUtils = zdsUtils;
		this.result = result;
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (zdsUtils.isAuthenticated() && result.isPresent()) {
                    String targetId = result.get().getValue().getId();
                    String localSlug = zdsUtils.getLocalSlug();
                    String targetSlug = result.get().getValue().getSlug();

                    String pathDir = zdsUtils.getOfflineContentPathDir() + File.separator + localSlug;
                    updateMessage(Configuration.bundle.getString("ui.task.zip.label")+" : "+targetSlug+" "+Configuration.bundle.getString("ui.task.pending.label")+" ...");
                    ZipUtil.pack(new File(pathDir), new File(pathDir + ".zip"));
                    updateMessage(Configuration.bundle.getString("ui.task.import.label")+" : "+targetSlug+" "+Configuration.bundle.getString("ui.task.pending.label")+" ...");
                    if(result.get().getValue().getType() == null) {
                        if(!zdsUtils.importNewContent(pathDir+ ".zip", result.get().getKey())) {
                            throw new IOException();
                        }
                    } else {
                        if(!zdsUtils.importContent(pathDir + ".zip", targetId, targetSlug, result.get().getKey())) {
                            throw new IOException();
                        }
                    }

                    updateMessage(Configuration.bundle.getString("ui.task.content.sync")+" ...");
                    try {
                        zdsUtils.getContentListOnline().clear();
                        zdsUtils.initInfoOnlineContent("tutorial");
                        zdsUtils.initInfoOnlineContent("article");
                    } catch (IOException e) {
                        logger.error("Echec de téléchargement des metadonnés des contenus en ligne", e);
                    }
                }
                return null;
            }
        };
    }
}
