package com.zestedesavoir.zestwriter.contents.plugins.internal;

public class PluginConfigDetailJson{
    private int id;
    private String name;
    private String user_name;
    private String description;
    private String version;
    private String url_id;
    private String plugin_url;
    private String download_url;

    public PluginConfigDetailJson(){
        id = 1;
        name = "Un plugin d'example";
        user_name = "Test";
        description = "Description";
        version = "0.0.0";
        url_id = "KxPd1";
        plugin_url = "http://zw.winxaito.com/api/plugin/KxPd1";
        download_url = "http://zw.winxaito.com/api/plugin/download/KxPd1";
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
}
