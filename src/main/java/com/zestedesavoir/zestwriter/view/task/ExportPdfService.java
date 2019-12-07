package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MdTextController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;

import java.io.File;

public class ExportPdfService extends Service<Void>{

    private File fileDest;
    private Content content;
    private MdTextController index;
    private File htmlFile;
    private final Logger log = Logger.getLogger(getClass());

    public ExportPdfService(MdTextController index, Content content, File fileDest) {
        this.fileDest = fileDest;
        this.content = content;
        this.index = index;
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
                content.saveToHtml(htmlFile, index);

                updateMessage(Configuration.getBundle().getString("ui.task.export.label"));
                /*
                PdfUtilExport act = new PdfUtilExport(content.getTitle(), content.getLicence(), "file://"+htmlFile, fileDest.getAbsolutePath());
                if(!act.exportToPdf()) {
                    updateMessage(Configuration.getBundle().getString("ui.task.export.error"));
                    throw new IOException();
                }*/
                return null;
            }
        };
    }
}
