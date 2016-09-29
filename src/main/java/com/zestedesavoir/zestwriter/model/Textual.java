package com.zestedesavoir.zestwriter.model;

public interface Textual {

    String getMarkdown();
    void save();
    String readMarkdown();
    void loadMarkdown(); // { setMarkdown(readMarkdown()); }
    String getTitle();
    void setMarkdown(String markdown);
    String getFilePath();
    void setBasePath(String basePath);
    Content getRootContent();
    void setRootContent(Content rootContent, String basePath);
}
