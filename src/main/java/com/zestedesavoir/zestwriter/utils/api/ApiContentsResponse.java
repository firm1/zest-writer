package com.zestedesavoir.zestwriter.utils.api;

import java.util.List;

/**
 * Based on ZestWriter API [0.0.0]
 *
 * Schema:
 *
 *  "plugins": [
 *      {
 *          >>ApiContentResponse<<
 *      },
 *  ]
 */
public class ApiContentsResponse{
    private List<ApiContentResponse> contents;

    public List<ApiContentResponse> getContents(){
        return contents;
    }

    public void setContents(List<ApiContentResponse> contents){
        this.contents = contents;
    }
}
