package com.zestedesavoir.zestwriter.contents.internal;

import com.zestedesavoir.zestwriter.view.dialogs.ContentsDialog;

public class ContentsConfigDetailJson{
    private ContentsDialog.ContentType contentsType;
    private int id;
    private String name;
    private String user_name;
    private String description;
    private String version;
    private String url_id;
    private String plugin_url;
    private String download_url;
    private boolean enabled;

    public ContentsConfigDetailJson(){
    }

    public ContentsDialog.ContentType getContentsType(){
        return contentsType;
    }

    public void setContentsType(ContentsDialog.ContentType contentsType){
        this.contentsType = contentsType;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUser_name(){
        return user_name;
    }

    public void setUser_name(String user_name){
        this.user_name = user_name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getVersion(){
        return version;
    }

    public void setVersion(String version){
        this.version = version;
    }

    public String getUrl_id(){
        return url_id;
    }

    public void setUrl_id(String url_id){
        this.url_id = url_id;
    }

    public String getPlugin_url(){
        return plugin_url;
    }

    public void setPlugin_url(String plugin_url){
        this.plugin_url = plugin_url;
    }

    public String getDownload_url(){
        return download_url;
    }

    public void setDownload_url(String download_url){
        this.download_url = download_url;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
}
