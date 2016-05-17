package com.zestedesavoir.zestwriter.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;


public class Extract extends MetaContent implements Textual, ContentNode{
    private String _text;
    @JsonIgnore private String markdown;


    @JsonCreator
    public Extract(@JsonProperty("object") String object, @JsonProperty("slug") String slug, @JsonProperty("title") String title, @JsonProperty("text") String text) {
        super(object, slug, title);
        this._text = text;
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        this._text = text;
    }

    @Override
    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
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
        return FunctionTreeFactory.padding(level, '#') +
                " " + getTitle() + "\n\n" +
                FunctionTreeFactory.changeLocationImages(FunctionTreeFactory.offsetHeaderMarkdown(readMarkdown(), levelDepth)) + "\n\n";
    }

    @Override
    public <R> Map<Textual, R> doOnTextual(Function<Textual,R> f) {
        Map<Textual, R> map = new HashMap<>();

        map.put(this, f.apply(this));

        return map;
    }

}
