package com.zestedesavoir.zestwriter.model;

public class License {
    private String code;
    private String label;

    public License(String code, String label) {
        super();
        this.code = code;
        this.label = label;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }



    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return label;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof License) {
            return ((License) obj).getCode().equals(this.getCode());
        }
        return false;
    }




}
