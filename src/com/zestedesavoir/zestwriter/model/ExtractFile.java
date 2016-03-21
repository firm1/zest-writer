package com.zestedesavoir.zestwriter.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExtractFile {
    private StringProperty basePath;
    private StringProperty title;
    private StringProperty slug;
    private StringProperty version;
    private StringProperty description;
    private StringProperty type;
    private StringProperty object;
    private StringProperty licence;
    private StringProperty introduction;
    private StringProperty conclusion;
    private StringProperty text;
    private StringProperty mdText;

    // for introduction and conclusion
    public ExtractFile(String title, String slug, String basePath) {
        super();
        this.basePath = new SimpleStringProperty(basePath);
        this.title = new SimpleStringProperty(title);
        this.slug = new SimpleStringProperty(slug);
        this.version = new SimpleStringProperty(null);
        this.description = new SimpleStringProperty(null);
        this.type = new SimpleStringProperty(null);
        this.licence = new SimpleStringProperty(null);
        this.introduction = new SimpleStringProperty(null);
        this.conclusion = new SimpleStringProperty(null);
        this.object = new SimpleStringProperty(null);
        this.text = new SimpleStringProperty(null);
    }

    // for root content
    public ExtractFile(String title, String slug, String basePath, String version, String descritpion, String type, String licence, String introduction, String conclusion) {
        super();
        this.basePath = new SimpleStringProperty(basePath);
        this.title = new SimpleStringProperty(title);
        this.slug = new SimpleStringProperty(slug);
        this.version = new SimpleStringProperty(version);
        this.description = new SimpleStringProperty(descritpion);
        this.type = new SimpleStringProperty(type);
        this.licence = new SimpleStringProperty(licence);
        this.introduction = new SimpleStringProperty(introduction);
        this.conclusion = new SimpleStringProperty(conclusion);
        this.object = new SimpleStringProperty("container");
        this.text = new SimpleStringProperty(null);
    }

    // for containers
    public ExtractFile(String title, String slug, String basePath, String introduction, String conclusion) {
        super();
        this.basePath = new SimpleStringProperty(basePath);
        this.title = new SimpleStringProperty(title);
        this.slug = new SimpleStringProperty(slug);
        this.version = new SimpleStringProperty(null);
        this.description = new SimpleStringProperty(null);
        this.type = new SimpleStringProperty(null);
        this.licence = new SimpleStringProperty(null);
        if (introduction == null || conclusion == null) {
            this.object = new SimpleStringProperty(null);
        } else {
            this.object = new SimpleStringProperty("container");
        }
        this.introduction = new SimpleStringProperty(introduction);
        this.conclusion = new SimpleStringProperty(conclusion);
        this.text = new SimpleStringProperty(null);
    }

    // for extract
    public ExtractFile(String title, String slug, String basePath, String text) {
        super();
        this.basePath = new SimpleStringProperty(basePath);
        this.title = new SimpleStringProperty(title);
        this.slug = new SimpleStringProperty(slug);
        this.version = new SimpleStringProperty(null);
        this.description = new SimpleStringProperty(null);
        this.type = new SimpleStringProperty(null);
        this.licence = new SimpleStringProperty(null);
        this.introduction = new SimpleStringProperty(null);
        this.conclusion = new SimpleStringProperty(null);
        this.object = new SimpleStringProperty("extract");
        this.text = new SimpleStringProperty(text);
    }

    public StringProperty getSlug() {
        return slug;
    }

    public String getFilePath() {

        Path path = null;

        if (isRoot()) {
            path = Paths.get(basePath.getValue());
        } else if (this.object.getValue() == null) {
            if (introduction.getValue() != null) {
                path = Paths.get(basePath.getValue(), this.introduction.getValue());
            } else if (conclusion.getValue() != null) {
                path = Paths.get(basePath.getValue(), this.conclusion.getValue());
            }
        } else if (this.isContainer()) {
            path = Paths.get(basePath.getValue(), this.introduction.getValue());
            path = path.getParent();
        } else {
            path = Paths.get(basePath.getValue(), this.text.getValue());
        }
        return path.toAbsolutePath().toString();
    }

    public StringProperty getType() {
        return type;
    }

    public StringProperty getVersion() {
        return version;
    }

    public StringProperty getDescription() {
        return description;
    }

    public StringProperty getLicence() {
        return licence;
    }

    public StringProperty getIntroduction() {
        return introduction;
    }

    public StringProperty getConclusion() {
        return conclusion;
    }

    public StringProperty getText() {
        return text;
    }

    public StringProperty getOject() {
        return object;
    }

    public void setSlug(String slug) {
        this.slug.set(slug);
    }

    public StringProperty getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty getMdText() {
        return mdText;
    }

    public void setMdText(String mdText) {
        this.mdText.set(mdText);
    }

    public void setIntroduction(String introduction) {
        this.introduction.set(introduction);
    }

    public void setConclusion(String conclusion) {
        this.conclusion.set(conclusion);
    }



    @Override
    public String toString() {
        return "ExtractFile [basePath=" + basePath + ", title=" + title + ", slug=" + slug + ", type=" + type
                + ", object=" + object + ", text=" + text + "]";
    }

    public void loadMarkdown() {
        Path path = Paths.get(this.getFilePath());
        Scanner scanner;
        StringBuilder bfString = new StringBuilder();
        try {
            scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            while (scanner.hasNextLine()) {
                bfString.append(scanner.nextLine());
                bfString.append("\n");
            }
            this.mdText = new SimpleStringProperty(bfString.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFilePath()), "UTF8"));
            writer.append(getMdText().getValue());
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

    public boolean canTakeContainer(int parentCountContainers, int childCountExtract) {
        boolean rule1 = !isContainer(); // can't create container in extract
        boolean rule2 = parentCountContainers >= 3; // max level in zds is 3
        boolean rule3 = childCountExtract > 2; // container with 3 extract can't take container

        return !(rule1 || rule2 || rule3 );

    }

    public boolean canTakeExtract(int childCountContainers) {
        boolean rule1 = !isContainer(); // can't create extract in extract
        boolean rule2 = isRoot() && childCountContainers > 0;// root with internal container can't have extract

        return !(rule1 || rule2);

    }

    public boolean canDelete() {
        boolean rule1 = getOject().getValue() == null; // can't create extract in extract

        return !rule1;

    }

    private void delete(File file) {
        if(file.isDirectory()) {
            if(file.list().length==0) {
                file.delete();
            }
            else {
                String files[] = file.list();
                for(String temp:files) {
                    File fileDelete = new File(file, temp);
                    delete(fileDelete);
                }
                if(file.list().length==0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public void deleteExtract() {

        File file = new File(getFilePath());

        if (file.exists()) {
            delete(file);
        }
    }

    public boolean canEdit() {
        boolean rule1 = isContainer(); // can't edit container

        return !rule1;

    }

    public boolean isEditable() {
        return this.object.getValue() != null;
    }

    /**
     * @param extract
     * @param depthContainers depth of containers. If extract is leaf, depth equals 0.
     * @return
     */
    public boolean isMoveableIn(ExtractFile extract, int depthContainers, int countExtractContainer, int countExtractParentContainer) {
        boolean rules1 = this.object == null; // detect introduction and conclusion
        boolean rules2 = extract.isRoot() && (!isContainer()); // detect when we put other way that container on root dir
        boolean rules3 = extract.getTitle().getValue().equalsIgnoreCase("Conclusion"); // nothing after conclusion
        boolean rules4 = isContainer() && (depthContainers >= 3); // zds content have 3 levels (contain, part, chapter)
        boolean rules5 = this.getFilePath().equals(extract.getFilePath());
        boolean rules6 = (!isContainer()) && (extract.isContainer()) && (countExtractContainer > 0); // detect movement of extract in container on container
        boolean rules7 = extract.getTitle().getValue().equalsIgnoreCase("introduction") && countExtractParentContainer > 0;

        return !(rules1 || rules2 || rules3 || rules4 || rules5 || rules6 || rules7);

    }

    public boolean isContainer() {
        return this.introduction.getValue() != null && this.conclusion.getValue() != null;
    }

    public boolean isRoot() {
        return this.type.getValue() != null;
    }

}
