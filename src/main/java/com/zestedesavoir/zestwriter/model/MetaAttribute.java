package com.zestedesavoir.zestwriter.model;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.com.IconFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpandTitle() {
        return getTitle()+" (" + getParent().getTitle() + ")";
    }

    public String getLimitedExpandTitle() {
        String title = getExpandTitle();
        if (title.length() > Constant.LIMIT_COUNT_CHARS_OF_TITLE) {
            return title.substring(0, Constant.LIMIT_COUNT_CHARS_OF_TITLE) + " ...";
        } else {
            return title;
        }
    }

    public String getSlug() {
        return _slug.replace("\\", "/");
    }

    public void setSlug(String slug) {
        this._slug = slug.replace("\\", "/");
    }

    public String getBasePath() {
        return basePath;
    }

    @Override
    public void setBasePath(String basePath) {
        this.basePath = basePath;
        File base = new File(getFilePath());
        File parent = base.getParentFile();
        if(!parent.exists()) {
            parent.mkdirs();
        }
        if(! base.exists()) {
            try {
                if(!base.createNewFile()) {
                    MainApp.getLogger().error("Problème lors de la création de "+base.getAbsolutePath());
                }
            } catch (IOException e) {
                MainApp.getLogger().error("Problème lors de la création de "+base.getAbsolutePath(), e);
            }
        }
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

    @Override
    public int hashCode() {
        return Objects.hash(getFilePath());
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
