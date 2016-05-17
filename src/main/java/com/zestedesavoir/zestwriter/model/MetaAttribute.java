package com.zestedesavoir.zestwriter.model;

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
import java.util.Scanner;

public class MetaAttribute implements Textual, ContentNode{
    private String basePath;
    private String _slug;
    private String title;
    private String markdown;
    private Content rootContent;

    public MetaAttribute(String _slug, String title) {
        super();
        this._slug = _slug;
        this.title = title;
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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return _slug;
    }

    public void setSlug(String slug) {
        this._slug = slug;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String getFilePath() {
        Path path = Paths.get(getBasePath(), this.getSlug());
        return path.toAbsolutePath().toString();
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public MaterialDesignIconView buildIcon() {
        return IconFactory.createFileBlankIcon();
    }

    @Override
    public Content getRootContent() {
        return rootContent;
    }

    @Override
    public void setRootContent(Content rootContent, String basePath) {
        this.rootContent = rootContent;
        setBasePath(basePath);
    }

    public Container getParent() {
        return FunctionTreeFactory.getContainerOfMetaAttribute(rootContent, this);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MetaAttribute) {
            return getFilePath().equals(((MetaAttribute) obj).getFilePath());
        }
        return super.equals(obj);
    }

}
