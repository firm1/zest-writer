package com.zestedesavoir.zestwriter.model;

import java.util.Objects;

public class TypeContent {
    private String code;
    private String label;
    public TypeContent(String code, String label) {
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
        return getLabel();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TypeContent) {
            return ((TypeContent) obj).getCode().equals(this.getCode());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, label);
    }

}
