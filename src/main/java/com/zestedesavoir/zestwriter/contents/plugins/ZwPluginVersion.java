package com.zestedesavoir.zestwriter.contents.plugins;


public class ZwPluginVersion{
    private int majorIndice;
    private int minorIndice;
    private int hotFixIndice;

    public ZwPluginVersion(){
        this(0, 0, 0);
    }

    public ZwPluginVersion(int majorIndice, int minorIndice){
        this(majorIndice, minorIndice, 0);
    }

    public ZwPluginVersion(int majorIndice, int minorIndice, int hotFixIndice){
        this.majorIndice = majorIndice;
        this.minorIndice = minorIndice;
        this.hotFixIndice = hotFixIndice;
    }


    public String toString(){
        return majorIndice + "." + minorIndice + "." + hotFixIndice;
    }

    public int getMajorIndice(){
        return majorIndice;
    }

    public void setMajorIndice(int majorIndice){
        this.majorIndice = majorIndice;
    }

    public int getMinorIndice(){
        return minorIndice;
    }

    public void setMinorIndice(int minorIndice){
        this.minorIndice = minorIndice;
    }

    public int getHotFixIndice(){
        return hotFixIndice;
    }

    public void setHotFixIndice(int hotFixIndice){
        this.hotFixIndice = hotFixIndice;
    }
}
