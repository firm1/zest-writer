package com.zestedesavoir.zestwriter.model;

import com.zestedesavoir.zestwriter.utils.Lang;
import com.zestedesavoir.zestwriter.view.dialogs.EditContentDialog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

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

    public static License getLicenseFromCode(String code) {
        Optional<License> license = EditContentDialog.getLicOptions().stream().filter(l -> l.code.equals(code)).findFirst();
        return license.orElse(null);
    }
}
