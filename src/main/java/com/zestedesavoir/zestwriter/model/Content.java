package com.zestedesavoir.zestwriter.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    public String exportContentToMarkdown(int level, int levelDepth) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("% ").append(getTitle().toUpperCase()).append("\n");
        sb.append("% ").append(dateFormat.format(new Date())).append("\n\n");
        sb.append("# ").append(getIntroduction().getTitle()).append("\n\n");
        sb.append(getIntroduction().readMarkdown()).append("\n\n");
        for(MetaContent c:getChildren()) {
            sb.append(c.exportContentToMarkdown(level+1, levelDepth));
        }
        sb.append("# ").append(getConclusion().getTitle()).append("\n\n");
        sb.append(getConclusion().readMarkdown());
        return sb.toString();
    }

    public void saveToMarkdown(File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            writer.append(exportContentToMarkdown(0, 2));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception ignored) {
            }
        }
    }

}
