package com.zestedesavoir.zestwriter.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
public class Theme {
    private static List<Theme> themeAvailable = Arrays.asList(
            new Theme("dark.css", Configuration.getBundle().getString("ui.options.display.theme.dark")),
            new Theme("light.css", Configuration.getBundle().getString("ui.options.display.theme.light")),
            new Theme("halloween.css", Configuration.getBundle().getString("ui.options.display.theme.halloween"), 31, 10, 2, 11),
            new Theme("winxaito_light.css", "WinXaito's Light"),
            new Theme("winxaito_dark.css", "WinXaito's Dark"),
            new Theme("christmas.css", Configuration.getBundle().getString("ui.options.display.theme.christmas"), 15, 12, 27, 12));
    private String filename;
    private String label;
    private int startDay = 0;
    private int startMonth = 0;
    private int endDay = 0;
    private int endMonth = 0;

    public Theme(String filename, String label) {
        this.filename = filename;
        this.label = label;
    }

    public static List<Theme> getThemeAvailable() {
        return themeAvailable;
    }

    public static Theme getThemeFromFileName(String filename) {
        Optional<Theme> t = themeAvailable.stream().filter(p -> p.getFilename().equals(filename)).findFirst();
        return t.orElse(null);
    }

    public static Theme getActiveTheme() {
        for(Theme t:themeAvailable) {
            if(t.startDay !=0) {
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH) + 1;
                int dateStartComparator = (t.getStartMonth() * 100 ) + t.getStartDay();
                int dateEndComparator = (t.getEndMonth() * 100 ) + t.getEndDay();
                int dateComparator = (month * 100 ) + day;
                if (dateComparator <= dateEndComparator && dateComparator >= dateStartComparator) {
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}
