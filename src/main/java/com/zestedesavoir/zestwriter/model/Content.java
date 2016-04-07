package com.zestedesavoir.zestwriter.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"basePath", "filePath", "editable", "countChildrenExtract", "countDescendantContainer" ,"rootContent"})
public class Content extends Container implements ContentNode{
    private int _version;
    private String _licence;
    private String _description;
    private String _type;


    @JsonCreator
    public Content(@JsonProperty("object") String object, @JsonProperty("slug") String slug, @JsonProperty("title") String title, @JsonProperty("introduction") String introduction, @JsonProperty("conclusion") String conclusion,
            @JsonProperty("children") List<MetaContent> children, @JsonProperty("version") int version, @JsonProperty("licence") String licence, @JsonProperty("description") String description, @JsonProperty("type") String type) {
        super(object, slug, title, introduction, conclusion, children);
        this._version = version;
        this._licence = licence;
        this._description = description;
        this._type = type;
    }

    public Content(String object, String slug, String title, String basePath, Textual introduction, Textual conclusion,
            List<MetaContent> children, int version, String licence, String description, String type) {
        super(object, slug, title, basePath, introduction, conclusion, children);
        this._version = version;
        this._licence = licence;
        this._description = description;
        this._type = type;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(int version) {
        this._version = version;
    }

    public String getLicence() {
        return _licence;
    }

    public void setLicence(String licence) {
        this._licence = licence;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        this._type = type;
    }

    @Override
    public String getFilePath() {
        Path path = Paths.get(getBasePath());
        return path.toAbsolutePath().toString();
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean isMoveableIn(ContentNode receiver, Content root) {
        return false;
    }

}
