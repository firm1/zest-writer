package com.zestedesavoir.zestwriter.utils.api;

/**
 * Based on ZestWriter API [0.0.0]
 *
 * Schema:
 *
 *  "user": {
 *      id: 1,
 *      name: "Unknown
 *  }
 */
public class ApiUserResponse{
    private int id;
    private String name;

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
}
