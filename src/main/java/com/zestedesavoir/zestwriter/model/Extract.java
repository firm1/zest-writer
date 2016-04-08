package com.zestedesavoir.zestwriter.model;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;


public class Extract extends MetaContent implements Textual, ContentNode{
    private String _text;
    @JsonIgnore private String markdown;


    @JsonCreator
    public Extract(@JsonProperty("object") String object, @JsonProperty("slug") String slug, @JsonProperty("title") String title, @JsonProperty("text") String text) {
        super(object, slug, title);
        this._text = text;
    }

    public Extract(String object, String slug, String title, String basePath, String text) {
        super(object, slug, title, basePath);
        this._text = text;
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        this._text = text;
    }

    @Override
    public void save() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFilePath()), "UTF8"));
            writer.append(getMarkdown());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception ignored) {
            }
        }

    }

    @Override
    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public String readMarkdown() {
        Path path = Paths.get(this.getFilePath());
        Scanner scanner  = null;
        StringBuilder bfString = new StringBuilder();
        try {
            scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            while (scanner.hasNextLine()) {
                bfString.append(scanner.nextLine());
                bfString.append("\n");
            }
            return bfString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        return null;
    }

    @Override
    public void loadMarkdown() {
        setMarkdown(readMarkdown());
    }

    @Override
    public String getFilePath() {
        Path path = Paths.get(getBasePath(), getText());

        return path.toAbsolutePath().toString();
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public MaterialDesignIconView buildIcon() {
        return IconFactory.createFileIcon();
    }

    @Override
    public boolean canTakeContainer(Content c) {
        return false;
    }

    @Override
    public boolean canTakeExtract() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isMoveableIn(ContentNode receiver, Content root) {
        if(receiver.getFilePath().equals(getFilePath())) {
            return false;
        }
        if(receiver instanceof Container) {
            if(((Container)receiver).getCountDescendantContainer() > 0 || receiver.getFilePath().equals(getFilePath())) {
                return false;
            }
        }
        if(receiver instanceof MetaAttribute) {
            if(receiver.getTitle().equalsIgnoreCase("conclusion")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Extract) {
            return getFilePath().equals(((Extract)obj).getFilePath());
        }
        return super.equals(obj);
    }

    @Override
    public String exportContentToMarkdown(int level, int levelDepth) {
        StringBuilder sb = new StringBuilder();
        sb.append(FunctionTreeFactory.padding(level, '#'));
        sb.append(" ").append(getTitle()).append("\n\n");
        sb.append(FunctionTreeFactory.offsetHeaderMarkdown(readMarkdown(), levelDepth)).append("\n\n");
        return sb.toString();
    }

}
