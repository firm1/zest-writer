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
    private List<ApiContentResponse> plugins;

    public List<ApiContentResponse> getPlugins(){
        return plugins;
    }

    public void setPlugins(List<ApiContentResponse> plugins){
        this.plugins = plugins;
    }
}
