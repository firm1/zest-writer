package com.zestedesavoir.zestwriter.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class License {
    private String code;
    private String label;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof License) {
                return ((License) obj).getCode().equals(this.getCode());
            }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
