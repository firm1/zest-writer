package com.zestedesavoir.zestwriter.view.dialogs;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.Markdown;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MdCheatSheetDialog {
    private final String CHEAT_SHEET_LOCATION = "assets/static/html/zMdCheatSheet.html";
    private final String TITLE_REGEX = "<!--(.*)-->\n";

    private MainApp mainApp;
    @FXML private TabPane cheatSheetTabPane;
    private Logger logger;
    private List<String> chaptersTitles;
    private String[] chaptersContents;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        chaptersTitles = new ArrayList<>();

        InputStream is = MainApp.class.getResourceAsStream(CHEAT_SHEET_LOCATION);
        String cheatSheet = "";
        try {
            cheatSheet = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            logger.error("Error when reading the cheatSheet stream.", e);
        }

        Matcher titleMatcher = Pattern.compile(TITLE_REGEX).matcher(cheatSheet);
        while (titleMatcher.find()) {
            String lang = "ui.md_cheat_sheet." + titleMatcher.group(1);
            System.out.println(lang);
            chaptersTitles.add(Configuration.bundle.getString("ui.md_cheat_sheet." + titleMatcher.group(1)));
        }

        chaptersContents = cheatSheet.split(TITLE_REGEX);

        List<Tab> tabs = cheatSheetTabPane.getTabs();

        for (int i=1 ; i<chaptersContents.length; i++) {
            Tab tab = new Tab();

            tab.setText(chaptersTitles.get(i-1));

            WebView webView = new WebView();
            String chapterContent = mainApp.getMdUtils().addHeaderAndFooter(chaptersContents[i]);
            webView.getEngine().loadContent(chapterContent);

            tab.setContent(webView);
            tabs.add(tab);
        }
    }
}
