package com.zds.zw.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


public class Lang {
    private static List<Lang> langAvailable = Arrays.asList(new Lang(Locale.FRANCE), new Lang(Locale.ENGLISH));
    private Locale locale;

    public Lang(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }


    public static Lang getLangFromCode(String code) {
        Optional <Lang> lang = langAvailable.stream().filter(p -> p.locale.toString().equals(code)).findFirst();
        return lang.orElse(null);
    }

    public static List<Lang> getLangAvailable() {
        return langAvailable;
    }

    @Override
    public String toString() {
        return locale.getDisplayLanguage();
    }
}
