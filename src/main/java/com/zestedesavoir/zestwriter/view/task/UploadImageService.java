package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class UploadImageService extends Service<String>{
    private Content content;
	private File imageFile;

    public UploadImageService(Content content, File imageFile) {
        this.content = content;
        this.imageFile = imageFile;
	}

	public MetadataContent getContentFromSlug() {
        for (MetadataContent c : MainApp.getZdsutils().getContentListOnline()) {
            if (c.getSlug().equals(MainApp.getZdsutils().getLocalSlug()) && c.getType().equalsIgnoreCase(content.getType())) {
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
                if (MainApp.getZdsutils().isAuthenticated()) {
                    MetadataContent find = getContentFromSlug();
                    if(find == null ) throw new IOException();

                    String targetId = find.getId();
                    String targetSlug = find.getSlug();

                    if (MainApp.getZdsutils().getGalleryId() == null) {
                        updateMessage(Configuration.getBundle().getString("ui.task.gallery.init")+" : "+targetSlug);
                        MainApp.getZdsutils().initGalleryId(targetId, targetSlug);
                    }

                    updateMessage(Configuration.getBundle().getString("ui.task.gallery.send_image") + " " + imageFile.getAbsolutePath() + " " + Configuration.getBundle().getString("ui.task.gallery.to") + " " + MainApp.getZdsutils().getGalleryId());

                    return MainApp.getZdsutils().importImage(imageFile);
                }
                return null;
            }
        };
    }
}
