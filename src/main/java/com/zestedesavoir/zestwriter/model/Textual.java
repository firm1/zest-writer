package com.zestedesavoir.zestwriter.model;

import com.zestedesavoir.zestwriter.utils.readability.Readability;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public interface Textual{

    String getMarkdown();
    default void save() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFilePath()), "UTF8"));
            writer.append(getMarkdown());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }
    default String readMarkdown() {
        Path path = Paths.get(this.getFilePath());
        Scanner scanner;
        StringBuilder bfString = new StringBuilder();
        try {
            scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            while (scanner.hasNextLine()) {
                bfString.append(scanner.nextLine());
                bfString.append("\n");
            }
            scanner.close();
            return bfString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    default void loadMarkdown() {
        setMarkdown(readMarkdown());
    }
    String getTitle();
    void setMarkdown(String markdown);
    String getFilePath();
    void setBasePath(String basePath);
    Content getRootContent();
    void setRootContent(Content rootContent, String basePath);
}
