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
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
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

    public String readMarkdown() {
        Path path = Paths.get(this.getFilePath());
        StringBuilder bfString = new StringBuilder();
        try (Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                bfString.append(scanner.nextLine());
                bfString.append("\n");
            }
            return bfString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadMarkdown() {
        setMarkdown(readMarkdown());
    }

    @Override
    public MaterialDesignIconView buildIcon() {
        return IconFactory.createFileBlankIcon();
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public boolean canTakeContainer(Content c) {
        return false;
    }

    @Override
    public boolean canTakeExtract() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public void delete() {
    }

    @Override
    public boolean isMoveableIn(ContentNode receiver, Content root) {
        return false;
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
