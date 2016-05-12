package com.zestedesavoir.zestwriter.model;

public interface Textual{
    void save();
    String getMarkdown();
    String readMarkdown();
    void loadMarkdown();
    String getTitle();
    void setMarkdown(String markdown);
    String getFilePath();
    void setBasePath(String basePath);
    Content getRootContent();
    void setRootContent(Content rootContent, String basePath);
}
