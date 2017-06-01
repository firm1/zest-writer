package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.MetadataContent;
import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadContentService extends Service<Void>{

    String typeContent;

    public DownloadContentService(String typeContent) {
        this.typeContent = typeContent;
    }

	@Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                if (MainApp.getZdsutils().isAuthenticated()) {
                    List<MetadataContent> workedList = MainApp.getZdsutils().getContentListOnline();
                    if(typeContent != null) {
                        workedList = workedList.stream()
                                .filter(c -> c.getType().equals(typeContent.toLowerCase()))
                                .collect(Collectors.toList());
                    }
                    int max = workedList.size();
                    int iterations = 0;

                    for (MetadataContent meta : workedList) {
                        updateMessage(Configuration.getBundle().getString("ui.task.download.label")+" : " + meta.getSlug());
                        updateProgress(iterations, max);
                        MainApp.getZdsutils().downloaDraft(meta.getId(), meta.getType());
                        iterations++;
                    }

                    iterations = 0;
                    for (MetadataContent meta : workedList) {
                        updateMessage(Configuration.getBundle().getString("ui.task.unzip.label")+" : " + meta.getSlug());
                        updateProgress(iterations, max);
                        MainApp.getZdsutils().unzipOnlineContent(MainApp.getZdsutils().getOnlineContentPathDir() + File.separator + meta.getSlug() + ".zip");
                        iterations++;
                    }
                    updateMessage(Configuration.getBundle().getString("ui.task.end.label"));
                    updateProgress(iterations, max);
                }
                return null;
            }
        };
    }
}
