package com.zestedesavoir.zestwriter.view.task;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DownloadContentService extends Service<Void>{
	private ZdsHttp zdsUtils;
	private final Logger logger;

	public DownloadContentService(ZdsHttp zdsUtils) {
		this.zdsUtils = zdsUtils;
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int max = zdsUtils.getContentListOnline().size();
                int iterations = 0;
                if (zdsUtils.isAuthenticated()) {
                    for (MetadataContent meta : zdsUtils.getContentListOnline()) {
                        updateMessage("Téléchargement : " + meta.getSlug());
                        updateProgress(iterations, max);
                        zdsUtils.downloaDraft(meta.getId(), meta.getType());
                        iterations++;
                    }

                    iterations = 0;
                    for (MetadataContent meta : zdsUtils.getContentListOnline()) {
                        updateMessage("Décompression : " + meta.getSlug());
                        updateProgress(iterations, max);
                        zdsUtils.unzipOnlineContent(zdsUtils.getOnlineContentPathDir() + File.separator + meta.getSlug() + ".zip");
                        iterations++;
                    }
                    updateMessage("Terminé");
                    updateProgress(iterations, max);
                }
                return null;
            }
        };
    }
}
