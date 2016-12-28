package com.zestedesavoir.zestwriter.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

    @Override
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
    public boolean isMovableIn(ContentNode receiver, Content root) {
        if(receiver.getFilePath().equals(getFilePath())) {
            return false;
        }
        if(receiver instanceof Container) {
            return ((Container)receiver).getCountDescendantContainer() == 0;
        } else if(receiver instanceof MetaAttribute) {
            return !"conclusion".equalsIgnoreCase(receiver.getTitle());
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
    public int hashCode() {
        return Objects.hash(getFilePath());
    }

    @Override
    public String exportContentToMarkdown(int level, int levelDepth) {
        return FunctionTreeFactory.padding(level) +
                " " + getTitle() + "\n\n" +
                FunctionTreeFactory.changeLocationImages(FunctionTreeFactory.offsetHeaderMarkdown(readMarkdown(), levelDepth)) + "\n\n";
    }

    @Override
    public <R> Map<Textual, R> doOnTextual(Function<Textual,R> f) {
        Map<Textual, R> map = new LinkedHashMap<>();

        map.put(this, f.apply(this));

        return map;
    }

    @Override
    public <R> Map<Textual, R> doOnTextual(Function<Textual,R> f, Function<Textual, Void> execBefore) {
        execBefore.apply(this);
        return doOnTextual(f);
    }

}
