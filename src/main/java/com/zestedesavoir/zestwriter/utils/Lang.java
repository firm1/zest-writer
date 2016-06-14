package com.zestedesavoir.zestwriter.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Lang {
    private Locale locale;
    public static List<Lang> langAvailable = Arrays.asList(new Lang(Locale.FRANCE), new Lang(Locale.ENGLISH));

    public Lang(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return locale.getDisplayName();
    }

    public static Lang getLangFromCode(String code) {
        return langAvailable.stream().filter(p -> p.getLocale().toString().equals(code)).findFirst().get();
    }
}
