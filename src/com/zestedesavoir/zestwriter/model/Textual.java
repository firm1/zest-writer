package com.zestedesavoir.zestwriter.model;

public interface Textual{
    public void save();
    public String getMarkdown();
    public void loadMarkdown();
    public String getTitle();
    public void setMarkdown(String markdown);
    public String getFilePath();
    public void setBasePath(String basePath);
    public Content getRootContent();
    public void setRootContent(Content rootContent, String basePath);
}
