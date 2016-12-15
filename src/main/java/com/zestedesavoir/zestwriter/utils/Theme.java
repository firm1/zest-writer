package com.zestedesavoir.zestwriter.utils;

import javafx.util.Pair;

import java.util.*;

public class Theme {
    private String filename;
    private String label;
    private int startDay=0;
    private int startMonth=0;
    private int endDay=0;
    private int endMonth=0;

    private static List<Theme> themeAvailable = Arrays.asList(
            new Theme("dark.css", Configuration.bundle.getString("ui.options.display.theme.dark")),
            new Theme("light.css", Configuration.bundle.getString("ui.options.display.theme.light")),
            new Theme("halloween.css", Configuration.bundle.getString("ui.options.display.theme.halloween"), 31, 10, 02, 11),
            new Theme("winxaito_light.css", "WinXaito's Light"),
            new Theme("winxaito_dark.css", "WinXaito's Dark"),
            new Theme("christmas.css", Configuration.bundle.getString("ui.options.display.theme.christmas"), 15, 10, 27, 10));

    public Theme(String filename, String label, int startDay, int startMonth, int endDay, int endMonth) {
        this.filename = filename;
        this.label = label;
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.endDay = endDay;
        this.endMonth = endMonth;
    }

    public Theme(String filename, String label) {
        this.filename = filename;
        this.label = label;
    }

    public String getFilename() {
        return filename;
    }

    public String getLabel() {
        return label;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public static List<Theme> getThemeAvailable() {
        return themeAvailable;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static Theme getThemeFromFileName(String filename) {
        Optional<Theme> t = themeAvailable.stream().filter(p -> p.getFilename().equals(filename)).findFirst();
        if(t.isPresent()) return t.get();
        return null;
    }

    public static Theme getActiveTheme() {
        Date d = new Date();
        for(Theme t:themeAvailable) {
            if(t.startDay !=0) {
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH) + 1;
                if (month <= t.getEndMonth() && month >= t.getStartMonth() && day <= t.getEndDay() && day >= t.getStartDay()) {
                    return t;
                }
            }
        }
        return null;
    }
}
