package com.zestedesavoir.zestwriter.model;

import com.zestedesavoir.zestwriter.MainApp;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public interface Textual{

    String getMarkdown();
    default void save() {
        try (FileOutputStream fos = new FileOutputStream(getFilePath())) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
            writer.append(getMarkdown());
            writer.flush();
        } catch (IOException e) {
            MainApp.getLogger().error(e.getMessage(), e);
        }
    }
    default String readMarkdown() {
        Path path = Paths.get(this.getFilePath());
        StringBuilder bfString = new StringBuilder();
        try(Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                bfString.append(scanner.nextLine());
                bfString.append("\n");
            }
            return bfString.toString();
        } catch (IOException e) {
            MainApp.getLogger().error(e.getMessage(), e);
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
