package com.zestedesavoir.zestwriter.utils;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Lang {
    private Locale locale;
    private static List<Lang> langAvailable = Arrays.asList(new Lang(Locale.FRANCE), new Lang(Locale.ENGLISH));

    public static Lang getLangFromCode(String code) {
        Optional <Lang> lang = langAvailable.stream().filter(p -> p.locale.toString().equals(code)).findFirst();
        return lang.orElse(null);
    }

    public static List<Lang> getLangAvailable() {
        return langAvailable;
    }
}
