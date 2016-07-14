package com.zestedesavoir.zestwriter.utils.api;

/**
 * Based on ZestWriter API [0.0.0]
 *
 * Schema:
 *
 *  {
 *      "id": 1,
 *      "name": "Name",
 *      "user": {
 *          >>ApiUserResponse<<
 *      },
 *      "official": true,
 *      "validate": true,
 *      "description": "Description",
 *      "version": "0.0.0",
 *      "downloads": 0,
 *      "url_id": "url_id",
 *      "plugin_url": "plugin_url",
 *      "download_url": "download_url"
 *  },
 */
public class ApiContentResponse{
    private int id;
    private String name;
    private ApiUserResponse user;
    private boolean official;
    private boolean validate;
    private String description;
    private String version;
    private int downloads;
    private String url_id;
    private String plugin_url;
    private String download_url;

    public ApiContentResponse(){
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

    public ApiUserResponse getUser(){
        return user;
    }

    public void setUser(ApiUserResponse user){
        this.user = user;
    }

    public boolean isOfficial(){
        return official;
    }

    public void setOfficial(boolean official){
        this.official = official;
    }

    public boolean isValidate(){
        return validate;
    }

    public void setValidate(boolean validate){
        this.validate = validate;
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

    public int getDownloads(){
        return downloads;
    }

    public void setDownloads(int downloads){
        this.downloads = downloads;
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

    public String toString(){
        return "[" + version + "] " + name;
    }
}
