package com.zestedesavoir.zestwriter.model;

import com.zestedesavoir.zestwriter.view.dialogs.EditContentDialog;

import java.util.Optional;

public class License {
    private String code;
    private String label;

    public License(String code, String label) {
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

    public static License getLicenseFromCode(String code) {
        Optional<License> license = EditContentDialog.getLicOptions().stream().filter(l -> l.code.equals(code)).findFirst();
        return license.orElse(null);
    }
}
