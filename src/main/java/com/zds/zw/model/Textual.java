package com.zds.zw.model;

import com.zds.zw.MainApp;

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

    void setMarkdown(String markdown);

    default void save() {
        try (FileOutputStream fos = new FileOutputStream(getFilePath())) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
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

    default String getLimitedTitle() {
        String title = getTitle();
        if (title.length() > Constant.LIMIT_COUNT_CHARS_OF_TITLE) {
            return title.substring(0, Constant.LIMIT_COUNT_CHARS_OF_TITLE) + " ...";
        } else {
            return title;
        }
    }

    String getFilePath();
    void setBasePath(String basePath);
    void setRootContent(Content rootContent, String basePath);
}
