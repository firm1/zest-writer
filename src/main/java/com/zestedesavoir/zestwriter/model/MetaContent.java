package com.zestedesavoir.zestwriter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zestedesavoir.zestwriter.MainApp;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static com.zestedesavoir.zestwriter.utils.StorageSaver.deleteFile;

@JsonIgnoreProperties({"basePath", "filePath", "editable", "object", "countChildrenExtract", "countDescendantContainer", "rootContent"})
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="object", visible=true)
@JsonSubTypes({@Type(value = Extract.class, name = "extract"), @Type(value = Container.class, name = "container") })
public abstract class MetaContent{
    private String _object;
    private String _slug;
    private String _title;
    private String basePath;
    private Content rootContent;

    public MetaContent(String object, String slug, String title) {
        super();
        this._object = object;
        this._slug = slug;
        this._title = title;
    }

    public Content getRootContent() {
        return rootContent;
    }

    public void setRootContent(Content rootContent, String basePath) {
        setBasePath(basePath);
        this.rootContent = rootContent;
        if(this instanceof Container) {
            Container c = ((Container) this);
            c.getIntroduction().setRootContent(rootContent, basePath);
            c.getConclusion().setRootContent(rootContent, basePath);
            for(MetaContent meta: c.getChildren()) {
                meta.setRootContent(rootContent, basePath);
            }
        }
        setBasePath(rootContent.getFilePath());
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getSlug() {
        return _slug;
    }

    public void setSlug(String slug) {
        this._slug = slug;
    }

    public String getObject() {
        return _object;
    }

    public void setObject(String object) {
        this._object = object;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
        File base = new File(getFilePath());
        File parent = base.getParentFile();
        if(!parent.exists()) {
            parent.mkdirs();
        }
        if(this instanceof Container) {
            if(! base.exists()) {
                base.mkdirs();
            }
            Container c = ((Container) this);
            c.getIntroduction().setBasePath(basePath);
            c.getConclusion().setBasePath(basePath);
            for(MetaContent meta: c.getChildren()) {
                meta.setBasePath(basePath);
            }
        } else {
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
    }

    public abstract String getFilePath();
    public abstract String exportContentToMarkdown(int level, int levelDepth);
    public abstract<R> Map<Textual, R> doOnTextual(Function<Textual,R> f);

    public boolean canDelete() {
        return true;
    }

    public void delete() {

        File file = new File(getFilePath());

        if (file.exists()) {
            deleteFile(file);
        }
    }

}
