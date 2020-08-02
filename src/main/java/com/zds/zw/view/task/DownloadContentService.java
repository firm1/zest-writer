package com.zds.zw.view.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.zw.MainApp;
import com.zds.zw.model.Content;
import com.zds.zw.model.MetadataContent;
import com.zds.zw.utils.Configuration;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;

public class DownloadContentService extends Service<Map<Content, Map<String, List<Map<File,String>>>>>{

    String typeContent;
    ObjectMapper mapper;

    public DownloadContentService(String typeContent) {
        this.typeContent = typeContent;
        mapper = new ObjectMapper();
    }

    private Content getContentFromDir(String dirName) {
        File manifest = new File(dirName, "manifest.json");
        Content content = null;
        try {
            content = mapper.readValue(manifest, Content.class);
        } catch (IOException e) {
            return null;
        }
        content.setRootContent(content, manifest.getParentFile().getAbsolutePath());

        return content;
    }

    private static long getCrc(String filename) {
        try {
            CheckedInputStream cis = null;
            long fileSize = 0;
            try {
                cis = new CheckedInputStream(new FileInputStream(filename), new CRC32());
                fileSize = new File(filename).length();
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0) {

            }
            return cis.getChecksum().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public Map<String, List<Map<File, String>>> compareOfflineAndOnline(String onlineZipPath, String offlineDirPath) {

        File existFolder = new File(offlineDirPath);
        if (!existFolder.exists()) {
            return null;
        }
        Map<String, List<Map<File,String>>> result = new HashMap<>();
        result.put("update", new ArrayList<>());
        result.put("add", new ArrayList<>());
        ZipUtil.iterate(new File(onlineZipPath), new ZipEntryCallback() {
            @Override
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                File fileOffline = new File(existFolder,zipEntry.getName());
                if(fileOffline.exists()) { // file for merge
                    if(zipEntry.getCrc() != getCrc(fileOffline.getAbsolutePath())) {
                        result.get("update").add(inputToMapping(in, fileOffline));
                    }
                } else { // file for add
                    result.get("add").add(inputToMapping(in, fileOffline));
                }
            }
        });
        return result;
    }

    private Map<File, String> inputToMapping(InputStream src, File dest) throws IOException {
        Map<File, String> mapping = new HashMap<>();
        String text = IOUtils.toString(src, "UTF-8");
        mapping.put(dest, text);
        return mapping;
    }

    @Override
    protected Task<Map<Content, Map<String, List<Map<File,String>>>>> createTask() {
        return new Task<Map<Content, Map<String, List<Map<File,String>>>>>() {
            @Override
            protected Map<Content, Map<String, List<Map<File,String>>>> call() throws Exception {

                Map<Content, Map<String, List<Map<File,String>>>> conflicts = new HashMap<>();

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
                        Map<String, List<Map<File,String>>> conflict = compareOfflineAndOnline(MainApp.getZdsutils().getOnlineContentPathDir() + File.separator + meta.getSlug() + ".zip", MainApp.getZdsutils().getOfflineContentPathDir() + File.separator + meta.getSlug());
                        conflicts.put(getContentFromDir(MainApp.getZdsutils().getOfflineContentPathDir() + File.separator + meta.getSlug()), conflict);
                        MainApp.getZdsutils().unzipOnlineContent(MainApp.getZdsutils().getOnlineContentPathDir() + File.separator + meta.getSlug() + ".zip");
                        iterations++;
                    }
                    updateMessage(Configuration.getBundle().getString("ui.task.end.label"));
                    updateProgress(iterations, max);
                }
                return conflicts;
            }
        };
    }
}
