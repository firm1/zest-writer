package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DownloadContentService extends Service<Void>{
	private ZdsHttp zdsUtils;

	public DownloadContentService(ZdsHttp zdsUtils) {
		this.zdsUtils = zdsUtils;
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
                        updateMessage(Configuration.bundle.getString("ui.task.download.label")+" : " + meta.getSlug());
                        updateProgress(iterations, max);
                        zdsUtils.downloaDraft(meta.getId(), meta.getType());
                        iterations++;
                    }

                    iterations = 0;
                    for (MetadataContent meta : zdsUtils.getContentListOnline()) {
                        updateMessage(Configuration.bundle.getString("ui.task.unzip.label")+" : " + meta.getSlug());
                        updateProgress(iterations, max);
                        zdsUtils.unzipOnlineContent(zdsUtils.getOnlineContentPathDir() + File.separator + meta.getSlug() + ".zip");
                        iterations++;
                    }
                    updateMessage(Configuration.bundle.getString("ui.task.end.label"));
                    updateProgress(iterations, max);
                }
                return null;
            }
        };
    }
}
