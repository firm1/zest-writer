package com.zestedesavoir.zestwriter.view.task;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UploadContentService extends Service<Void>{
	private ZdsHttp zdsUtils;
	private final Logger logger;
	private Optional<MetadataContent> result;

	public UploadContentService(ZdsHttp zdsUtils, Optional<MetadataContent> result) {
		this.zdsUtils = zdsUtils;
		this.result = result;
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (zdsUtils.isAuthenticated()) {
                    String targetId = result.get().getId();
                    String localSlug = zdsUtils.getLocalSlug();
                    String targetSlug = result.get().getSlug();
                    try {
                        String pathDir = zdsUtils.getOfflineContentPathDir() + File.separator + localSlug;
                        updateMessage("Compression : "+targetSlug+" en cours ...");
                        ZipUtil.pack(new File(pathDir), new File(pathDir + ".zip"));
                        updateMessage("Import : "+targetSlug+" en cours ...");
                        if(targetId == null) {
                            if(!zdsUtils.importNewContent(pathDir+ ".zip")) {
                                failed();
                            }
                        } else {
                            if(!zdsUtils.importContent(pathDir + ".zip", targetId, targetSlug)) {
                                failed();
                            }
                        }
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
                return null;
            }
        };
    }
}
