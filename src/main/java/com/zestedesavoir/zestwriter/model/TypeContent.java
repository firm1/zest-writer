package com.zestedesavoir.zestwriter.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
@AllArgsConstructor
public class TypeContent {
    private String code;
    private String label;

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

    @Override
    public String toString() {
        return getLabel();
    }
}
