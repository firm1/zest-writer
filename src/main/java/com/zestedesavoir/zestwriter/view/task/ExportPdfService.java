package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.PdfUtilExport;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ExportPdfService extends Service<Void>{
    private File fileDest;
    private Content content;
    private File htmlFile;

    public ExportPdfService(Content content, File fileDest) {
        this.fileDest = fileDest;
        this.content = content;
        htmlFile = new File(System.getProperty("java.io.tmpdir"), ZdsHttp.toSlug(content.getTitle()) + ".html");
    }

    public Service<Void> getThis() {
        return this;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                updateMessage(Configuration.getBundle().getString("ui.task.export.assemble.label"));
                content.saveToHtml(htmlFile);

                updateMessage(Configuration.getBundle().getString("ui.task.export.label"));
                PdfUtilExport act = new PdfUtilExport(content.getTitle(), content.getLicence(), "file://"+htmlFile, fileDest.getAbsolutePath());
                if(!act.exportToPdf()) {
                    updateMessage(Configuration.getBundle().getString("ui.task.export.error"));
                    throw new IOException();
                }
                return null;
            }
        };
    }
}
