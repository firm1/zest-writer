package com.zestedesavoir.zestwriter.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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

    public MetaContent(String object, String slug, String title, String basePath) {
        super();
        this._object = object;
        this._slug = slug;
        this._title = title;
        this.basePath = basePath;
    }


    public Content getRootContent() {
        return rootContent;
    }

    public void setRootContent(Content rootContent, String basePath) {
        setBasePath(basePath);
        this.rootContent = rootContent;
        if(this instanceof Container) {
            Container c = ((Container) this);
            c.getIntroduction().setBasePath(basePath);
            c.getConclusion().setBasePath(basePath);
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
        if(this instanceof Container) {
            Container c = ((Container) this);
            c.getIntroduction().setBasePath(basePath);
            c.getConclusion().setBasePath(basePath);
            for(MetaContent meta: c.getChildren()) {
                meta.setBasePath(basePath);
            }
        }
    }

    public abstract String getFilePath();
    public abstract String exportContentToMarkdown(int level, int levelDepth);
    public abstract<R> Map<Textual, R> doOnTextual(Function<Textual,R> f);

    public static void loadMarkdown(Textual textual) {
        Path path = Paths.get(textual.getFilePath());
        Scanner scanner;
        StringBuilder bfString = new StringBuilder();
        try {
            scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            while (scanner.hasNextLine()) {
                bfString.append(scanner.nextLine());
                bfString.append("\n");
            }
            textual.setMarkdown(bfString.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean canDelete() {
        return true;
    }

    private void deleteFile(File file) {
        if(file.isDirectory()) {
            if(file.list().length==0) {
                file.delete();
            }
            else {
                String files[] = file.list();
                for(String temp:files) {
                    File fileDelete = new File(file, temp);
                    deleteFile(fileDelete);
                }
                if(file.list().length==0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public void delete() {

        File file = new File(getFilePath());

        if (file.exists()) {
            deleteFile(file);
        }
    }

}
