package com.zestedesavoir.zestwriter.view.task;

import java.io.File;
import java.io.IOException;

import com.zestedesavoir.zestwriter.utils.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UploadImageService extends Service<String>{
	private ZdsHttp zdsUtils;
	private Content content;
	private File imageFile;
	private final Logger logger;

	public UploadImageService(ZdsHttp zdsUtils, Content content, File imageFile) {
		this.zdsUtils = zdsUtils;
		this.content = content;
		this.imageFile = imageFile;
		logger = LoggerFactory.getLogger(getClass());
	}

	public MetadataContent getContentFromSlug() {
	    for(MetadataContent c: zdsUtils.getContentListOnline()) {
	        if(c.getSlug().equals(zdsUtils.getLocalSlug()) && c.getType().equalsIgnoreCase(content.getType())) {
	            return c;
	        }
	    }
	    return null;
	}
	@Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                if (zdsUtils.isAuthenticated()) {
                    MetadataContent find = getContentFromSlug();
                    if(find == null ) throw new IOException();

                    String targetId = find.getId();
                    String targetSlug = find.getSlug();

                    if(zdsUtils.getGalleryId() == null ) {
                        updateMessage(Configuration.bundle.getString("ui.task.gallery.init")+" : "+targetSlug);
                        zdsUtils.initGalleryId(targetId, targetSlug);
                    }

                    updateMessage(Configuration.bundle.getString("ui.task.gallery.send_image")+" "+imageFile.getAbsolutePath()+" "+Configuration.bundle.getString("ui.task.gallery.to")+" "+zdsUtils.getGalleryId());

                    return zdsUtils.importImage(imageFile);
                }
                return null;
            }
        };
    }
}
