package com.zestedesavoir.zestwriter.contents.internal;

import java.util.ArrayList;

public class ContentsConfigJson{
    private ArrayList<ContentsConfigDetailJson> contents = new ArrayList<>();

    public ContentsConfigJson(){
    }

    public ArrayList<ContentsConfigDetailJson> getContents(){
        return contents;
    }

    public void setContents(ArrayList<ContentsConfigDetailJson> contents){
        this.contents = contents;
    }

    public void addContent(ContentsConfigDetailJson content){
        contents.add(content);
    }
}
